package br.com.devsrsouza.bukkript.host

import br.com.devsrsouza.bukkript.Bukkript
import br.com.devsrsouza.bukkript.api.ScriptDescription
import br.com.devsrsouza.bukkript.host.loader.BukkriptScriptClassLoader
import br.com.devsrsouza.bukkript.script.*
import java.io.File
import kotlin.script.experimental.api.*
import kotlin.script.experimental.host.ScriptingHostConfiguration
import kotlin.script.experimental.host.createCompilationConfigurationFromTemplate
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvm.defaultJvmScriptingHostConfiguration
import kotlin.script.experimental.jvmhost.*
import kotlin.script.experimental.util.PropertiesCollection

suspend fun compile(plugin: Bukkript) {

    val scripts = plugin.SCRIPT_DIR.listFiles()
        .filter { it.extension.equals(scriptExtension, true) }

    scripts.mapNotNull { script ->
        val source = script.toScriptSource()

        val fileWithoutExtension = script.nameWithoutExtension.substringBeforeLast(".")

        // CACHE
        val cachedDir = File(plugin.CACHE_DIR, fileWithoutExtension)
        cachedDir.mkdirs()

        val modification = File(cachedDir, ".modification")
        val scriptModification = script.lastModified()

        if(modification.exists() && scriptModification != modification.lastModified()) {
            cachedDir.deleteRecursively()
        }

        if(!modification.exists()) {
            modification.createNewFile()
            modification.setLastModified(scriptModification)
        }

        val cache = FileBasedScriptCache(cachedDir)

        // COMPILATION

        val compiler = JvmScriptCompiler(defaultJvmScriptingHostConfiguration, cache = cache)

        val configuration = createJvmCompilationConfigurationFromTemplate<BukkriptScript>()

        val compiled = compiler(source, configuration)

        val result = compiled.resultOrNull()

        if(result != null) {
            val description = result.compilationConfiguration.run {
                fun <T> PropertiesCollection.Key<T>.getKey() = get(this) ?: defaultValue!!
                ScriptDescription(
                    ScriptCompilationConfiguration.name.getKey(),
                    ScriptCompilationConfiguration.version.getKey(),
                    ScriptCompilationConfiguration.author.getKey(),
                    ScriptCompilationConfiguration.authors.getKey(),
                    ScriptCompilationConfiguration.website.getKey(),
                    ScriptCompilationConfiguration.dependScripts.getKey(),
                    ScriptCompilationConfiguration.softDependScripts.getKey(),
                    ScriptCompilationConfiguration.dependPlugins.getKey(),
                    ScriptCompilationConfiguration.softDependPlugins.getKey()
                )
            }

            BukkriptCompiledScript(
                fileWithoutExtension,
                script,
                result as CompiledScript<BukkriptScript>,
                description
            )
        } else {
            for (diag in compiled.reports) { println(diag) }
            null
        }
    }
}

suspend fun loadScripts(plugin: Bukkript) {

    val baseEvalConfig = ScriptEvaluationConfiguration {
        constructorArgs(plugin)
    }

    val baseClassLoader = Bukkript::class.java.classLoader

    // TODO sort before load

    val bukkriptClassLoader = BukkriptScriptClassLoader(
        plugin.LOADER,
        baseClassLoader,
        description
    )

    val evalConfig = ScriptEvaluationConfiguration(baseEvalConfig) {
        set(JvmScriptEvaluationConfiguration.actualClassLoader, bukkriptClassLoader)
    }

    result.getClass(evalConfig)
}