package br.com.devsrsouza.bukkript.host

import br.com.devsrsouza.bukkript.api.BukkriptAPI
import br.com.devsrsouza.bukkript.api.ScriptDescription
import br.com.devsrsouza.bukkript.api.script.AbstractScript
import br.com.devsrsouza.bukkript.api.script.scriptName
import br.com.devsrsouza.bukkript.host.loader.BukkriptScriptClassLoaderImpl
import br.com.devsrsouza.bukkript.script.*
import br.com.devsrsouza.kotlinbukkitapi.extensions.plugin.info
import br.com.devsrsouza.kotlinbukkitapi.extensions.text.msg
import br.com.devsrsouza.kotlinbukkitapi.extensions.text.plus
import kotlinx.coroutines.*
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.plugin.Plugin
import org.jetbrains.kotlin.utils.addToStdlib.ifNotEmpty
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible
import kotlin.script.experimental.api.*
import kotlin.script.experimental.host.FileScriptSource
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvm.defaultJvmScriptingHostConfiguration
import kotlin.script.experimental.jvm.jvm
import kotlin.script.experimental.jvm.updateClasspath
import kotlin.script.experimental.jvmhost.*
import kotlin.script.experimental.jvmhost.impl.KJvmCompiledModule
import kotlin.script.experimental.util.PropertiesCollection

fun compileScripts(api: BukkriptAPI, scripts: List<File>, sender: CommandSender? = null, afterCompile: () -> Unit = {}) {

    val scripts = scripts.toMutableList()

    if(api !is Plugin) return

    fun log(log: String, color: ChatColor = ChatColor.GREEN) {
        api.info(log)
        sender?.msg(ChatColor.AQUA + "[Bukkript] " + color + log)
    }

    log("Starting compilation of following scripts: " + scripts.map { it.path }.joinToString())

    val scriptNoDepend: MutableList<File> = mutableListOf()
    val scriptToSort: MutableMap<File, MutableList<File>> = mutableMapOf()

    val scriptDescriptions: MutableMap<File, ScriptDescription> = hashMapOf()
    val compiledJar = hashMapOf<File, File>()

    fun loadDependencies(script: File, description: ScriptDescription): Boolean {
        val dependPlugins = description.pluginDepend

        var pluginMissing = false
        for (depend in dependPlugins) {
            if (!Bukkit.getServer().pluginManager.isPluginEnabled(depend)) {
                log("Missing plugin $depend needed on the script ${script.scriptName(api)}")
                pluginMissing = true
            }
        }

        if (pluginMissing) return false

        val dependency = description.depend

        if (dependency.isEmpty()) {
            scriptNoDepend.add(script)
        } else {
            val dependScripts = dependency.map { File(api.SCRIPT_DIR, it).normalize() }
            var scriptMissing = false
            for (depend in dependScripts) {
                if (!depend.exists() || depend.isDirectory) {
                    log("Missing script ${depend.name} needed on the script ${script.scriptName(api)}")
                    scriptMissing = true
                }
            }

            if (!scriptMissing) scriptToSort.put(script, dependScripts.toMutableList())
            else return false
        }

        return true
    }

    fun configurationForLoadingDependencies(script: File) =
        ScriptCompilationConfiguration(createJvmCompilationConfigurationFromTemplate<BukkriptScript>()) {
            refineConfiguration {
                beforeCompiling { context ->
                    val description = context.compilationConfiguration.get(ScriptCompilationConfiguration.description)!!

                    if(loadDependencies(script, description))
                        scriptDescriptions.put(script, description)

                    return@beforeCompiling ResultWithDiagnostics.Failure()
                }
            }
        }

    fun configurationForCompile(script: File) =
        ScriptCompilationConfiguration(createJvmCompilationConfigurationFromTemplate<BukkriptScript>()) {
            refineConfiguration {
                beforeCompiling { context ->
                    ResultWithDiagnostics.Success(ScriptCompilationConfiguration(context.compilationConfiguration) {
                        jvm {
                            val description = scriptDescriptions[script]

                            if (description != null) {
                                // plugins classpath
                                for (plugin in description.pluginDepend) {
                                    updateClasspath(Bukkit.getPluginManager().getPlugin(plugin).classpath())
                                }
                                // scripts classpath
                                description.depend.mapNotNull { otherScript ->
                                    File(api.SCRIPT_DIR, otherScript).takeIf { it.exists() }
                                        ?.let { compiledJar.get(it) }
                                }.ifNotEmpty { updateClasspath(this) }
                            }
                        }
                    })
                }
            }
        }

    if(scripts.isEmpty()) {
        log("Scripts not founded", ChatColor.RED)
        return
    }

    log("Loading scripts dependencies")

    val cacheDescription = FileBasedScriptCache(api, api)

    for (script in scripts) {
        val source = FileScriptSource(script)

        // generate script dependencies

        if(cacheDescription.isValid(source)) {
            val description = cacheDescription.readDescription(source) ?: continue // TODO LOG

            loadDependencies(script, description)

            scriptDescriptions.put(script, description)
        } else {
            val compiler = JvmScriptCompiler(defaultJvmScriptingHostConfiguration)

            runBlocking {
                compiler(source, configurationForLoadingDependencies(script)).resultOrSeveral(
                    api,
                    api,
                    sender
                )
            } // generate scriptDepend script
        }
    }

    suspend fun compile(script: File): ResultWithDiagnostics<BukkriptCompiledScriptImpl> {
        val source = script.toScriptSource()

        log("Compiling ${script.scriptName(api)}...")

        val cache = FileBasedScriptCache(api, api, scriptDescriptions[script])

        val compiler = JvmScriptCompiler(defaultJvmScriptingHostConfiguration, cache = cache)

        val compiled = compiler(source, configurationForCompile(script))

        val result = compiled.resultOrNull()

        return if (result != null) {
            val description = cache.description ?: return ResultWithDiagnostics
                    .Failure("Not possible to find the script description of ${script.scriptName(api)}".asErrorDiagnostics())

            ResultWithDiagnostics.Success(BukkriptCompiledScriptImpl(
                script.scriptName(api),
                script,
                result as CompiledScript<BukkriptScript>,
                description
            ))
        } else {
            ResultWithDiagnostics.Failure(compiled.reports)
        }
    }

    if(scriptToSort.isEmpty() && scriptNoDepend.isEmpty()) return

    val toSort = scriptNoDepend.filter { f -> scriptToSort.values.any { it.any { it.absolutePath == f.absolutePath } } }
    scriptNoDepend.removeAll(toSort)
    scriptToSort += toSort.map { it to mutableListOf<File>() }.toMap()

    // removing already loaded scripts from sort and compilation
    for (key in (scriptToSort.keys + scriptNoDepend).filter { api.LOADER.scripts.containsKey(it.scriptName(api)) }) {
        log("Script ${key.scriptName(api)} already loaded, skipping compilation.")
        scriptToSort.remove(key) ?: scriptNoDepend.remove(key)
    }

    for((key, value) in scriptToSort) {
        value.removeAll { depend ->
            api.LOADER.scripts.any { depend.absolutePath == it.value.bukkriptCompiledScript.scriptFile.absolutePath }
        }
    }

    log("Sorting script dependencies")

    val sorted = sortToCompile(scriptToSort).resultOrSeveral(api, api)!!

    val tempCompilation = File(api.dataFolder, "/tempCompilation")
    if(tempCompilation.exists())
        tempCompilation.deleteRecursively()
    tempCompilation.mkdirs()

    suspend fun load(script: File) {
        val bkCompiledScript = compile(script).resultOrSeveral(api, api)

        if (bkCompiledScript != null) {
            log("Starting loading ${bkCompiledScript.scriptFileName}")
            loadScript(api, bkCompiledScript)
            val scriptTempJar = File(tempCompilation, script.scriptName(api) + ".jar")
            scriptTempJar.outputStream().use { jarOutput ->
                ZipOutputStream(jarOutput).use { zipOutput ->
                    api.LOADER.scripts.get(script.scriptName(api))?.also {
                        (it.classLoader as BukkriptScriptClassLoaderImpl).script.forEach {
                            val entry = ZipEntry(it.key)
                            zipOutput.putNextEntry(entry)
                            zipOutput.write(it.value)
                            zipOutput.closeEntry()
                        }
                    }
                }
            }
            compiledJar.put(script, scriptTempJar)
        }
    }

    val compilations = mutableListOf<Job>()

    for (script in scriptNoDepend) {
        compilations += GlobalScope.launch {
            load(script)
        }
    }

    compilations += GlobalScope.launch {
        for (script in sorted) {
            load(script)
        }
    }

    GlobalScope.launch {
        compilations.forEach { if(it.isActive) it.join() }
        tempCompilation.deleteRecursively()
        afterCompile()
    }
}

suspend fun loadScript(api: BukkriptAPI, bukkriptCompiledScript: BukkriptCompiledScriptImpl) {

    val baseClassLoader = api::class.java.classLoader

    val bukkriptClassLoader = BukkriptScriptClassLoaderImpl(
        api.LOADER,
        baseClassLoader,
        bukkriptCompiledScript
    )

    val evalConfig = ScriptEvaluationConfiguration {
        constructorArgs(api)
        jvm {
            actualClassLoader(bukkriptClassLoader)
        }
    }
    val compiled = bukkriptCompiledScript.compiledScript
    val moduleMember = compiled::class.memberProperties.find { it.name == "compiledModule" } as KProperty1<Any, Any>?
    moduleMember?.isAccessible = true
    val module = moduleMember?.get(compiled) as KJvmCompiledModule

    bukkriptClassLoader.script = module.compilerOutputFiles

    val clazz = bukkriptCompiledScript.compiledScript.getClass(evalConfig).resultOrSeveral(api as Plugin, api)

    if (clazz != null) {
        api.LOADER.loadScript(
            BukkriptLoadedScriptImpl(
                api,
                clazz as KClass<AbstractScript>,
                bukkriptClassLoader,
                bukkriptCompiledScript
            )
        )
    }
}

fun sortToCompile(dependencies: MutableMap<File, MutableList<File>>) : ResultWithDiagnostics.Success<List<File>> {

    val scripts = dependencies.keys.toMutableList()

    val diagnostics = mutableListOf<ScriptDiagnostic>()

    val sorteds = mutableListOf<File>()

    while (!scripts.isEmpty()) {
        var missingDependency = true
        var scriptsIterator = scripts.listIterator()

        while (scriptsIterator.hasNext()) {
            val script = scriptsIterator.next()

            if (dependencies.containsKey(script)) {
                val dependencyIterator = dependencies.get(script)!!.listIterator()

                while (dependencyIterator.hasNext()) {
                    val dependency = dependencyIterator.next()

                    if (sorteds.firstOrNull { it.absolutePath.equals(dependency.absolutePath) } != null) {
                        dependencyIterator.remove()
                    } else if (scripts.firstOrNull { it.absolutePath.equals(dependency.absolutePath) } === null) {
                        missingDependency = false
                        scriptsIterator.remove()
                        dependencies.remove(script)

                        diagnostics.add("Could not load ${script.name}: missing dependency $dependency".asErrorDiagnostics())
                        break
                    }
                }
                if (dependencies.containsKey(script) && dependencies.get(script)?.isEmpty() == true) {
                    dependencies.remove(script)
                }
            }
            if (!(dependencies.containsKey(script)) && scripts.contains(script)) {

                scriptsIterator.remove()
                missingDependency = false


                sorteds.add(script)
                continue
            }
        }
        if (missingDependency) {
            scriptsIterator = scripts.listIterator()

            while (scriptsIterator.hasNext()) {
                val script = scriptsIterator.next()

                if (!dependencies.containsKey(script)) {
                    missingDependency = false

                    scriptsIterator.remove()

                    sorteds.add(script)
                    break
                }
            }
            if (missingDependency) {
                dependencies.clear()

                val failedScriptIterator = scripts.listIterator()

                while (failedScriptIterator.hasNext()) {
                    val script = failedScriptIterator.next()
                    failedScriptIterator.remove()
                    diagnostics.add("Could not load ${script.name} in folder 'blalb': circular dependency detected".asErrorDiagnostics())
                }
            }
        }
    }

    return ResultWithDiagnostics.Success(sorteds, diagnostics)
}