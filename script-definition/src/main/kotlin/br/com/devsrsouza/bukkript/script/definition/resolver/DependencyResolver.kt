package br.com.devsrsouza.bukkript.script.definition.resolver

import br.com.devsrsouza.bukkript.script.definition.dependencies.IvyResolver
import br.com.devsrsouza.bukkript.script.definition.dependencies.baseDependencies
import kotlinx.coroutines.runBlocking
import java.io.File
import kotlin.script.experimental.api.*
import kotlin.script.experimental.dependencies.tryAddRepository
import kotlin.script.experimental.jvm.updateClasspath

fun resolveScriptDependencies(
    ctx: ScriptConfigurationRefinementContext
): ResultWithDiagnostics<ScriptCompilationConfiguration> {

    val configuration = ctx.compilationConfiguration.with {

        val resolver = IvyResolver()

        // TODO: remove this from the resolver and do every single time

        // TODO: add support to find plugins and the server jar and add to the classpath

        val files = mutableListOf<File>()

        runBlocking {
            for ((fqn, repositories, artifacts) in baseDependencies) {
                runCatching { Class.forName(fqn) }.onFailure {
                    for (repository in repositories) {
                        resolver.tryAddRepository(repository)
                    }

                    for (artifact in artifacts) {
                        files += (resolver.resolve(artifact) as ResultWithDiagnostics.Success<List<File>>).value
                    }
                }
            }
        }

        updateClasspath(files)
    }

    return configuration.asSuccess()
}