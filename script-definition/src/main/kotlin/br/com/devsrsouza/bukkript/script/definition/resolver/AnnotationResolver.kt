package br.com.devsrsouza.bukkript.script.definition.resolver

import br.com.devsrsouza.bukkript.script.definition.ScriptDescription
import br.com.devsrsouza.bukkript.script.definition.annotation.DependPlugin
import br.com.devsrsouza.bukkript.script.definition.annotation.Maven
import br.com.devsrsouza.bukkript.script.definition.annotation.MavenRepository
import br.com.devsrsouza.bukkript.script.definition.annotation.Script
import br.com.devsrsouza.bukkript.script.definition.api.LogLevel
import br.com.devsrsouza.bukkript.script.definition.configuration.info
import br.com.devsrsouza.bukkript.script.definition.error.DiagnosticResult
import br.com.devsrsouza.bukkript.script.definition.error.requireOrFail
import kotlin.script.experimental.api.*
import kotlin.script.experimental.jvm.updateClasspath

fun resolveScriptAnnotation(
    ctx: ScriptConfigurationRefinementContext
): ResultWithDiagnostics<ScriptCompilationConfiguration> {
    val depConfiguration = (resolveIdeScriptDependencies(ctx) as ResultWithDiagnostics.Success).value

    val annotations = ctx.collectedData?.get(ScriptCollectedData.foundAnnotations)
        ?.takeIf { it.isNotEmpty() } ?: return depConfiguration.asSuccess()

    val reports = mutableListOf<ScriptDiagnostic>()

    val configuration = depConfiguration.with {

        var name = "None"
        var version = "None"
        var author = "Unknown"
        var website = "None"
        var logLevel = LogLevel.INFO

        val pluginDepends = mutableSetOf<String>()

        val dependencies = mutableSetOf<String>()
        val repositories = mutableSetOf<String>()

        for (annotation in annotations) {
            when (annotation) {
                is Script -> {
                    val requireOrFail =
                        annotation.name.requireOrFail({ "Script name require to be not blank!" }) { it.isNotBlank() }

                    when(requireOrFail) {
                        is DiagnosticResult.Success -> name = requireOrFail.value
                        is DiagnosticResult.Fail -> reports.add(requireOrFail.diagnostic)
                    }

                    annotation.version.takeIf { it.isNotBlank() }?.also { version = it }
                    annotation.author.takeIf { it.isNotBlank() }?.also { author = it }
                    annotation.website.takeIf { it.isNotBlank() }?.also { website = it }
                    logLevel = annotation.logLevel
                }

                is DependPlugin -> pluginDepends.addAll(annotation.plugin)

                is Maven -> dependencies.add(annotation.dependency)

                is MavenRepository -> repositories.add(annotation.url)
            }
        }

        // Resolving/downloading external dependencies provided with annotations: adding to
        // classpath for compilation and to Description for be usaged in the Classloader.
        val dependenciesFiles = if (dependencies.isNotEmpty())
            resolveExternalDependencies(ctx.script, repositories, dependencies).also {
                updateClasspath(it)
            }.map { it.absolutePath }.toSet()
        else emptySet()

        info(
            ScriptDescription(
                name,
                version,
                author,
                website,
                pluginDepends,
                logLevel,
                dependenciesFiles
            )
        )
    }

    return if(reports.isEmpty()) {
        configuration.asSuccess()
    } else {
        makeFailureResult(reports)
    }
}
