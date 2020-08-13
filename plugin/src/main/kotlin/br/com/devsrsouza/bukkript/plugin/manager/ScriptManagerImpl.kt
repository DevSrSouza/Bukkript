package br.com.devsrsouza.bukkript.plugin.manager

import br.com.devsrsouza.bukkript.plugin.BukkriptPlugin
import br.com.devsrsouza.bukkript.plugin.disable
import br.com.devsrsouza.bukkript.plugin.manager.script.ScriptState
import br.com.devsrsouza.bukkript.plugin.watcher.watchFolder
import br.com.devsrsouza.bukkript.script.definition.*
import br.com.devsrsouza.bukkript.script.definition.api.LogLevel
import br.com.devsrsouza.bukkript.script.host.compiler.BukkriptScriptCompilerImpl
import br.com.devsrsouza.bukkript.script.host.loader.BukkriptScriptLoaderImpl
import br.com.devsrsouza.kotlinbukkitapi.architecture.lifecycle.pluginCoroutineScope
import br.com.devsrsouza.kotlinbukkitapi.extensions.plugin.info
import br.com.devsrsouza.kotlinbukkitapi.extensions.skedule.BukkitDispatchers
import br.com.devsrsouza.kotlinbukkitapi.utils.time.now
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.bukkit.entity.Player
import java.io.File
import java.lang.IllegalStateException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentSkipListSet

class ScriptManagerImpl(
    override val plugin: BukkriptPlugin
) : ScriptManager {

    companion object {
        const val MINIMUM_MODIFY_TIME_TO_RECOMPILE_SECONDS = 5
    }

    override val scripts: ConcurrentHashMap<String, ScriptState> = ConcurrentHashMap()

    private val scriptDir by lazy { File(plugin.dataFolder, "scripts").apply { mkdirs() } }
    private val cacheDir by lazy { File(plugin.dataFolder, ".cache").apply { mkdirs() } }

    private val compiler by lazy { BukkriptScriptCompilerImpl(scriptDir, cacheDir) }
    private val loader by lazy {
        BukkriptScriptLoaderImpl(
            plugin,
            scriptDir,
            plugin::class.java.classLoader,
            ::getClassByName,
            plugin.loggingManager::logScript
        )
    }

    private val hotrecompileScripts = ConcurrentSkipListSet<String>()

    // String(Script Name), Long(last time modified)
    private val recompileQueue: ConcurrentHashMap<String, Long> = ConcurrentHashMap()

    private val logger get() = plugin.loggingManager

    override fun onPluginEnable() {
        discoveryAllScripts()

        runBlocking {
            compileAll().forEach {
                it.join()
            }

            loadAllUnloaded()
        }

        pluginCoroutineScope.launch(BukkitDispatchers.SYNC) {
            while (true) {
                for ((script, lastTimeModified) in recompileQueue) {
                    if (now() - lastTimeModified > MINIMUM_MODIFY_TIME_TO_RECOMPILE_SECONDS * 1000) {
                        recompileQueue.remove(script)
                        recompile(script)
                    }
                }
                delay(1000)
            }
        }

        watchFolder(scriptDir.toPath())
            .onEach {
                val scriptName = it.file.bukkriptNameRelative(scriptDir)

                if (scriptName in hotrecompileScripts) {
                    recompileQueue.put(scriptName, now())
                }
            }
            .launchIn(pluginCoroutineScope)
    }

    override fun onPluginDisable() {
        unloadAll()
    }

    override fun compile(scriptName: String): Job {
        var scriptState = scripts[scriptName]

        // if the script is not avaiable yet, trying to discovery it
        if (scriptState == null) {
            discoveryScript(scriptName)

            scriptState = scripts[scriptName]
        }

        // if the state is not discovered state is probably already loaded or unloaded in the Plugin.
        if(
            scriptState !is ScriptState.Discovered
            && scriptState !is ScriptState.CompileFail
            && scriptState !is ScriptState.LoadFail
        ) TODO("The script is currently is unloaded or loaded and could not compile again.")

        return pluginCoroutineScope.launch(Dispatchers.Default) {
            logger.logScript(scriptName, LogLevel.INFO, "Starting the compilation process.")

            val scriptFile = File(scriptDir, "$scriptName.$BUKKRIPT_EXTENSION")

            val cachedScript = compiler.getCachedScript(scriptFile)

            logger.logScript(
                scriptName,
                LogLevel.DEBUG,
                "Retrieved cache information from the script: is cached: ${cachedScript != null} & is valid: ${cachedScript?.isValid == true}"
            )

            scripts.put(scriptName, ScriptState.CheckingCache(scriptName))

            val compiled = if (cachedScript != null && cachedScript.isValid) {
                logger.logScript(
                    scriptName,
                    LogLevel.INFO,
                    "Found a compiled valid script cached, skipping compilation."
                )
                cachedScript.compiled
            } else {
                val description = compiler.retrieveDescriptor(scriptFile)
                    ?: TODO()

                logger.logScript(scriptName, LogLevel.INFO, "Retrieved descriptor: version=${description.version}, author=${description.author}, log level=${description.logLevel}.")

                scripts.put(scriptName, ScriptState.Compiling(scriptName, scriptFile, description))

                runCatching { compiler.compile(scriptFile, description) }
                    .getOrElse {
                        logger.logScript(scriptName, LogLevel.ERROR, it.toString())

                        scripts.put(scriptName, ScriptState.CompileFail(scriptName, it.toString()))

                        return@launch
                    }
                    .also {
                        logger.logScript(scriptName, LogLevel.INFO, "Complete compile the script.")
                    }
            }

            scripts.put(scriptName, ScriptState.Unloaded(scriptName, compiled))
        }
    }

    override fun load(scriptName: String) {
        val state = scripts[scriptName] ?: TODO("script not found")
        val unloaded = (state as? ScriptState.Unloaded) ?: TODO("the current state of the script is not unloaded.")

        load(unloaded)
    }

    private fun load(unloaded: ScriptState.Unloaded) {
        val scriptName = unloaded.scriptName

        scripts.put(scriptName, ScriptState.Loading(scriptName, unloaded.compiledScript))

        logger.logScript(scriptName, LogLevel.INFO, "Loading the script.")

        val loaded = runBlocking {
            runCatching { loader.load(unloaded.compiledScript) }
                .getOrElse {
                    logger.logScript(scriptName, LogLevel.ERROR, it.toString())

                    scripts.put(scriptName, ScriptState.LoadFail(scriptName, it.toString()))

                    null
                }
        } ?: return

        scripts.put(scriptName, ScriptState.Loaded(scriptName, loaded))

        logger.logScript(scriptName, LogLevel.INFO, "Complete load the script.")
    }

    private fun compileAll(): List<Job> {
        info("Compiling all discovered scripts")
        return scripts.keys.map { compile(it) }
    }

    private fun loadAllUnloaded() {
        info("Loading all unloaded scripts")
        for ((scriptName, state) in scripts) {
            if (state is ScriptState.Unloaded) {
                load(state)
            }
        }
    }

    override fun isLoaded(scriptName: String): Boolean {
        // TODO: use lower case or something?
        return (scripts[scriptName] as? ScriptState.Loaded?) != null
    }

    override fun unload(scriptName: String) {
        val script = scripts[scriptName] ?: TODO("Could not find the script")
        val loaded = script as? ScriptState.Loaded ?: TODO("The provided script is not in the Loaded State")

        unload(loaded)
    }

    private fun unload(loaded: ScriptState.Loaded) {
        val scriptName = loaded.scriptName
        logger.logScript(scriptName, LogLevel.INFO, "Unloading the script.")

        loaded.loadedScript.disable()

        scripts.put(scriptName, ScriptState.Unloaded(scriptName, loaded.loadedScript.compiledScript))

        logger.logScript(scriptName, LogLevel.INFO, "Complete unload the script.")
    }

    private fun unloadAll() {
        val loadedScripts = scripts.values.filterIsInstance<ScriptState.Loaded>()

        info("Unloading all loaded scripts: ${loadedScripts.joinToString { it.scriptName }}")

        for (loaded in loadedScripts) {
            unload(loaded)
        }
    }

    override fun reload(scriptName: String) {
        val script = scripts[scriptName] ?: TODO("Could not find the script")
        val loaded = script as? ScriptState.Loaded ?: TODO("The provided script is not in the Loaded State")

        logger.logScript(scriptName, LogLevel.INFO, "Reloading the script.")

        unload(loaded)

        val unloaded = scripts[scriptName] as? ScriptState.Unloaded
            ?: throw IllegalStateException("Reloading script invalid script state or missing.")

        load(unloaded)
    }

    override fun recompile(scriptName: String) {
        logger.logScript(scriptName, LogLevel.INFO, "Recompiling script.")

        unload(scriptName)

        // setting a discovered because the compile uses just discovered state.
        scripts.put(scriptName, ScriptState.Discovered(scriptName))

        pluginCoroutineScope.launch(BukkitDispatchers.SYNC) {
            compile(scriptName).join()

            load(scriptName)
        }
    }

    override fun lockLog(player: Player, scriptName: String) {
        scripts[scriptName] ?: TODO("Could not find the script to lock the log for the Player")

        logger.listenLog(player, scriptName)
    }

    override fun hotRecompile(scriptName: String) {
        if (scriptName !in scripts)
            TODO("Could not find the script to enable hot recompile")

        hotrecompileScripts.add(scriptName)
    }

    // retrieve all scripts files in put into de scripts
    override fun discoveryAllScripts() {
        for (scriptName in listScriptsFromFolder()) {
            if (!scripts.containsKey(scriptName))
                scripts.put(scriptName, ScriptState.Discovered(scriptName))
        }
    }

    private fun discoveryScript(scriptName: String) {
        if (scriptName in scripts)
            TODO("The script tring to discovery is already discovered.")

        val scriptFile = File(scriptDir, "$scriptName.$BUKKRIPT_EXTENSION")

        if (scriptFile.exists()) {
            scripts.put(scriptName, ScriptState.Discovered(scriptName))
        } else {
            TODO("Could not find the script file by the name $scriptName!")
        }
    }

    private fun getClassByName(name: String): Class<*>? {
        for (value in scripts.values.filterIsInstance<ScriptState.Loaded>()) {
            val findClass = value.loadedScript.classLoader.findClass(name, false)

            if (findClass != null)
                return findClass
        }

        return null
    }

    private fun listScriptsFromFolder(): Set<String> {
        return scriptDir.walkTopDown()
            .filter { it.isBukkriptScript }
            .map { it.bukkriptNameRelative(scriptDir) }
            .toSet()
    }
}