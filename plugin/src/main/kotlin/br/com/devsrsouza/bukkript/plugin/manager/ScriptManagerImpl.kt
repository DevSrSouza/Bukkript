package br.com.devsrsouza.bukkript.plugin.manager

import br.com.devsrsouza.bukkript.plugin.BukkriptPlugin
import br.com.devsrsouza.bukkript.plugin.disable
import br.com.devsrsouza.bukkript.plugin.result.LoadResult
import br.com.devsrsouza.bukkript.script.definition.*
import br.com.devsrsouza.bukkript.script.host.compiler.BukkriptScriptCompilerImpl
import br.com.devsrsouza.bukkript.script.host.loader.BukkriptLoadedScript
import br.com.devsrsouza.bukkript.script.host.loader.BukkriptScriptLoaderImpl
import br.com.devsrsouza.kotlinbukkitapi.architecture.lifecycle.pluginCoroutineScope
import br.com.devsrsouza.kotlinbukkitapi.extensions.skedule.BukkitDispatchers
import kotlinx.coroutines.*
import org.bukkit.entity.Player
import java.io.File
import java.util.concurrent.ConcurrentHashMap

class ScriptManagerImpl(
    override val plugin: BukkriptPlugin
) : ScriptManager {

    override val loadedScripts: ConcurrentHashMap<String, BukkriptLoadedScript>
        get() = ConcurrentHashMap()

    private val scriptDir by lazy { File(plugin.dataFolder, "scripts").apply { mkdirs() } }
    private val cacheDir by lazy { File(plugin.dataFolder, ".cache").apply { mkdirs() } }

    private val compiler by lazy { BukkriptScriptCompilerImpl(scriptDir, cacheDir) }
    private val loader by lazy {
        BukkriptScriptLoaderImpl(plugin, scriptDir, plugin::class.java.classLoader, ::getClassByName)
    }

    override fun onPluginEnable() {
        pluginCoroutineScope.launch {
            loadAll().forEach {
                it.await()
            }
        }

    }

    override fun onPluginDisable() {
        unloadAll()
    }

    override fun load(scriptName: String): Deferred<LoadResult> {
        println("loading script: $scriptName")
        return pluginCoroutineScope.async(Dispatchers.Default) {
            val scriptFile = File(scriptDir, "$scriptName.$BUKKRIPT_EXTENSION")

            val cachedScript = compiler.getCachedScript(scriptFile)

            val compiled = if(cachedScript != null && cachedScript.isValid) {
                cachedScript.compiled
            } else {
                val description = compiler.retrieveDescriptor(scriptFile)
                    ?: TODO()

                runCatching { compiler.compile(scriptFile, description) }.getOrNull()
                    ?: TODO()
            }

            val loaded = runCatching { loader.load(compiled) }.onFailure { it.printStackTrace() }.getOrNull()
                ?: TODO()

            loadedScripts.put(scriptName, loaded)

            LoadResult.Success
        }
    }

    private fun loadAll(): List<Deferred<LoadResult>> {
        return listScriptsFromFolder().map { load(it)  }
    }

    override fun isLoaded(scriptName: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun unload(scriptName: String) {
        unload(loadedScripts[scriptName] ?: TODO())
    }

    private fun unload(loadedScript: BukkriptLoadedScript) {
        loadedScript.disable()
        loadedScripts.remove(loadedScript.compiledScript.source.bukkritNameRelative(scriptDir))
    }

    private fun unloadAll() {
        for (loadedScript in loadedScripts.values) {
            // TODO: logging
            unload(loadedScript)
        }
    }

    override fun reload(scriptName: String) {
        TODO("Not yet implemented")
    }

    override fun recompile(scriptName: String) {
        TODO("Not yet implemented")
    }

    override fun lockLog(player: Player, scriptName: String) {
        TODO("Not yet implemented")
    }

    override fun hotRecompile(scriptName: String) {
        TODO("Not yet implemented")
    }

    override fun listScriptsFromFolder(): Set<String> {
        return scriptDir.walkTopDown()
            .filter { it.isBukkriptScript }
            .map { it.bukkriptNameRelative(scriptDir) }
            .toSet()
    }

    private fun getClassByName(name: String): Class<*>? {
        for (value in loadedScripts.values) {
            val findClass = value.classLoader.findClass(name, false)

            if(findClass != null)
                return findClass
        }

        return null
    }
}