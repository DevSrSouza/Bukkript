package br.com.devsrsouza.bukkript.script.host.loader

import br.com.devsrsouza.bukkript.script.definition.*
import br.com.devsrsouza.bukkript.script.host.compiler.BukkriptCompiledScript
import br.com.devsrsouza.bukkript.script.host.loader.classloader.ClassProvider
import br.com.devsrsouza.bukkript.script.host.loader.classloader.ScriptClassloader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import org.bukkit.plugin.Plugin
import java.io.File
import kotlin.script.experimental.api.*
import kotlin.script.experimental.jvm.BasicJvmScriptEvaluator
import kotlin.script.experimental.jvm.baseClassLoader
import kotlin.script.experimental.jvm.jvm
import kotlin.script.experimental.jvm.loadDependencies

class BukkriptScriptLoaderImpl(
    val plugin: Plugin,
    val scriptDir: File,
    val parentClassloader: ClassLoader,
    val classProvider: ClassProvider
) : BukkriptScriptLoader {

    override suspend fun load(compiledScript: BukkriptCompiledScript): BukkriptLoadedScript {

        val job = Job()
        val coroutineScope = CoroutineScope(job)

        val dataFolder = File(scriptDir, compiledScript.source.bukkritNameRelative(scriptDir))

        val classLoader = ScriptClassloader(
            classProvider,
            parentClassloader,
            compiledScript.description.dependenciesFiles.map { File(it) }.toSet()
        )

        val evalConfig = ScriptEvaluationConfiguration {
            constructorArgs(plugin, compiledScript.description, dataFolder, coroutineScope)
            jvm {
                baseClassLoader(classLoader)
                loadDependencies(false)
            }
        }

        val evaluator = BasicJvmScriptEvaluator()

        val result = evaluator(compiledScript.compiled, evalConfig)

        return when(result) {
            is ResultWithDiagnostics.Success -> {
                when (val it = result.value.returnValue) {
                    is ResultValue.Error -> TODO() // TODO: throw error
                    ResultValue.NotEvaluated -> TODO() // TODO: throw error
                    else -> {
                        // VALUE and UNIT
                        if(it.scriptClass != null && it.scriptInstance !== null) {
                            BukkriptLoadedScript(
                                it.scriptInstance as BukkriptScript,
                                it.scriptClass!!,
                                classLoader,
                                compiledScript,
                                dataFolder,
                                job,
                                coroutineScope
                            )
                        } else {
                            TODO() // TODO: throw error
                        }
                    }
                }
            }
            is ResultWithDiagnostics.Failure -> TODO() // TODO: throw error
        }
    }
}