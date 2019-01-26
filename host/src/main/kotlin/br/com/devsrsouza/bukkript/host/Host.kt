package br.com.devsrsouza.bukkript.host

import br.com.devsrsouza.bukkript.api.BukkriptAPI
import br.com.devsrsouza.bukkript.api.ScriptDescription
import br.com.devsrsouza.bukkript.api.script.AbstractScript
import br.com.devsrsouza.bukkript.host.loader.BukkriptScriptClassLoaderImpl
import br.com.devsrsouza.bukkript.script.*
import br.com.devsrsouza.kotlinbukkitapi.extensions.plugin.info
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import java.io.File
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible
import kotlin.script.experimental.api.*
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvm.defaultJvmScriptingHostConfiguration
import kotlin.script.experimental.jvmhost.*
import kotlin.script.experimental.jvmhost.impl.KJvmCompiledModule

fun compileScripts(api: BukkriptAPI) {

    if(api !is Plugin) return

    api.info("Starting scripts compilation")

    val scripts = api.SCRIPT_DIR.listFiles()
        .filter { it.extension.equals(scriptExtension, true) }

    val scriptNoDepend: MutableList<File> = mutableListOf()
    val scriptToSort: MutableMap<File, MutableList<File>> = mutableMapOf()

    fun configuration(script: File) =
        ScriptCompilationConfiguration(createJvmCompilationConfigurationFromTemplate<BukkriptScript>()) {
            refineConfiguration {
                beforeCompiling { context ->

                    val diagnostics = arrayListOf<ScriptDiagnostic>()

                    val dependPlugins =
                        context.compilationConfiguration.get(ScriptCompilationConfiguration.dependPlugins)!!

                    var pluginMissing = false
                    for (depend in dependPlugins) {
                        if (!Bukkit.getServer().pluginManager.isPluginEnabled(depend)) {
                            diagnostics.add("Missing api $depend needed on the script ${context.script.name}".asErrorDiagnostics())
                            pluginMissing = true
                        }
                    }

                    if (pluginMissing) ResultWithDiagnostics.Failure(diagnostics)

                    if (!(scriptToSort.containsKey(script) || scriptNoDepend.contains(script))) {

                        val dependency =
                            context.compilationConfiguration.get(ScriptCompilationConfiguration.dependScripts)
                                ?: emptyList()

                        if (dependency.isEmpty()) {
                            scriptNoDepend.add(script)
                        } else {
                            val dependScripts = dependency.map { File(api.dataFolder, it).normalize() }
                            var scriptMissing = false
                            for (depend in dependScripts) {
                                if (!depend.exists() || depend.isDirectory) {
                                    diagnostics.add("Missing script ${depend.name} needed on the script ${context.script.name}".asErrorDiagnostics())
                                    scriptMissing = true
                                }
                            }

                            if (!scriptMissing) scriptToSort.put(script, dependScripts.toMutableList())
                            return@beforeCompiling ResultWithDiagnostics.Failure(diagnostics)
                        }

                        ResultWithDiagnostics.Failure()
                    }

                    ResultWithDiagnostics.Success(context.compilationConfiguration)
                }
            }
        }

    fun File.scriptName() = this.nameWithoutExtension

    api.info("Loading scripts dependencies")

    if(scripts.isEmpty()) {
        api.info("scripts not founded")
        return
    }

    for (script in scripts) {
        val source = script.toScriptSource()

        // generate script dependencies script

        val compiler = JvmScriptCompiler(defaultJvmScriptingHostConfiguration)

        runBlocking { compiler(source, configuration(script)).resultOrSeveral(api, api) } // generate scriptDepend script
    }

    val cache = FileBasedScriptCache(api, api)

    suspend fun compile(script: File): ResultWithDiagnostics<BukkriptCompiledScriptImpl> {
        val source = script.toScriptSource()

        api.info("Compiling ${script.scriptName()}...")

        val compiler = JvmScriptCompiler(defaultJvmScriptingHostConfiguration, cache = cache)

        val compiled = compiler(source, configuration(script))

        val result = compiled.resultOrNull()

        return if (result != null) {
            val description = result.compilationConfiguration.run {
                ScriptDescription(
                    get(ScriptCompilationConfiguration.name)!!,
                    get(ScriptCompilationConfiguration.version)!!,
                    get(ScriptCompilationConfiguration.author)!!,
                    get(ScriptCompilationConfiguration.authors)!!,
                    get(ScriptCompilationConfiguration.website)!!,
                    get(ScriptCompilationConfiguration.dependScripts)!!,
                    get(ScriptCompilationConfiguration.dependPlugins)!!
                )
            }

            ResultWithDiagnostics.Success(BukkriptCompiledScriptImpl(
                script.scriptName(),
                script,
                result as CompiledScript<BukkriptScript>,
                description
            ))
        } else {
            ResultWithDiagnostics.Failure(compiled.reports)
        }
    }

    if(scriptToSort.isEmpty() && scriptNoDepend.isEmpty()) return

    api.info("Sorting script dependencies")

    val sorted = sortToCompile(scriptToSort).resultOrSeveral(api, api)!!

    suspend fun load(script: File) {
        val bkCompiledScript = compile(script).resultOrSeveral(api, api)

        if(bkCompiledScript != null) {
            api.info("Starting loading ${bkCompiledScript.scriptFileName}")
            loadScript(api, bkCompiledScript)
        }
    }

    for (script in scriptNoDepend) {
        GlobalScope.async {
            load(script)
        }
    }

    GlobalScope.async {
        for (script in sorted) {
            load(script)
        }
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
        var scriptsInterator = scripts.listIterator()

        while (scriptsInterator.hasNext()) {
            val script = scriptsInterator.next()

            if (dependencies.containsKey(script)) {
                val dependencyIterator = dependencies.get(script)!!.listIterator()

                while (dependencyIterator.hasNext()) {
                    val dependency = dependencyIterator.next()

                    if (sorteds.firstOrNull { it.name.equals(dependency) } != null) {
                        dependencyIterator.remove()
                    } else if (scripts.firstOrNull { it.name.equals(dependency) } === null) {
                        missingDependency = false
                        scriptsInterator.remove()
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

                scriptsInterator.remove()
                missingDependency = false


                sorteds.add(script)
                continue
            }
        }
        if (missingDependency) {
            scriptsInterator = scripts.listIterator()

            while (scriptsInterator.hasNext()) {
                val script = scriptsInterator.next()

                if (!dependencies.containsKey(script)) {
                    missingDependency = false

                    scriptsInterator.remove()

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