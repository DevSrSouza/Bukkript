package br.com.devsrsouza.bukkript.host

import br.com.devsrsouza.bukkript.Bukkript
import br.com.devsrsouza.bukkript.api.ScriptDescription
import br.com.devsrsouza.bukkript.host.loader.BukkriptScriptClassLoader
import br.com.devsrsouza.bukkript.script.*
import br.com.devsrsouza.kotlinbukkitapi.extensions.plugin.info
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.bukkit.Bukkit
import java.io.File
import kotlin.reflect.KClass
import kotlin.script.experimental.api.*
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvm.defaultJvmScriptingHostConfiguration
import kotlin.script.experimental.jvmhost.*

suspend fun compileScripts(plugin: Bukkript) {

    val scripts = plugin.SCRIPT_DIR.listFiles()
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
                            diagnostics.add("Missing plugin $depend needed on the script ${context.script.name}".asErrorDiagnostics())
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
                            val dependScripts = dependency.map { File(plugin.dataFolder, it).normalize() }
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

    for (script in scripts) {
        val source = script.toScriptSource()

        // generate script dependencies entries

        val compiler = JvmScriptCompiler(defaultJvmScriptingHostConfiguration)

        compiler(source, configuration(script)).reports.forEach { println(it) } // generate scriptDepend entries
    }

    fun cache(script: File): FileBasedScriptCache {
        // CACHE
        val cachedDir = File(plugin.CACHE_DIR, script.scriptName())
        cachedDir.mkdirs()

        val modification = File(cachedDir, ".modification")
        val scriptModification = script.lastModified()

        if (modification.exists() && scriptModification != modification.lastModified()) {
            cachedDir.deleteRecursively()
        }

        if (!modification.exists()) {
            modification.createNewFile()
            modification.setLastModified(scriptModification)
        }

        return FileBasedScriptCache(cachedDir)
    }

    suspend fun compile(script: File): ResultWithDiagnostics<BukkriptCompiledScript> {
        val source = script.toScriptSource()

        val compiler = JvmScriptCompiler(defaultJvmScriptingHostConfiguration, cache = cache(script))

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

            ResultWithDiagnostics.Success(BukkriptCompiledScript(
                script.scriptName(),
                script,
                result as CompiledScript<BukkriptScript>,
                description
            ))
        } else {
            ResultWithDiagnostics.Failure(compiled.reports)
        }
    }

    val sorted = sortToCompile(plugin, scriptToSort).resultOrSeveral(plugin)!!

    suspend fun load(script: File) {
        val bkCompiledScript = compile(script).resultOrSeveral(plugin)

        if(bkCompiledScript != null) {
            plugin.info("Starting loading ${bkCompiledScript.scriptFileName}")
            loadScript(plugin, bkCompiledScript)
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

suspend fun loadScript(plugin: Bukkript, bukkriptCompiledScript: BukkriptCompiledScript) {

    val baseClassLoader = Bukkript::class.java.classLoader

    val bukkriptClassLoader = BukkriptScriptClassLoader(
        plugin.LOADER,
        baseClassLoader,
        bukkriptCompiledScript
    )

    val evalConfig = ScriptEvaluationConfiguration {
        constructorArgs(plugin)
        set(JvmScriptEvaluationConfiguration.actualClassLoader, bukkriptClassLoader)
    }

    val clazz = bukkriptCompiledScript.compiledScript.getClass(evalConfig).resultOrSeveral(plugin)

    if (clazz != null) {
        plugin.LOADER.loadScript(
            BukkriptLoadedScript(
                plugin,
                clazz as KClass<BukkriptScript>,
                bukkriptClassLoader,
                bukkriptCompiledScript
            )
        )
    }
}

fun sortToCompile(plugin: Bukkript, dependencies: MutableMap<File, MutableList<File>>) : ResultWithDiagnostics.Success<List<File>> {

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