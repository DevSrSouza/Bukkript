package br.com.devsrsouza.bukkript.script.definition.resolver

import br.com.devsrsouza.bukkript.script.definition.ScriptDescription
import br.com.devsrsouza.bukkript.script.definition.annotation.DependPlugin
import br.com.devsrsouza.bukkript.script.definition.annotation.Script
import br.com.devsrsouza.bukkript.script.definition.api.LogLevel
import br.com.devsrsouza.bukkript.script.definition.configuration.info
import br.com.devsrsouza.bukkript.script.definition.dependencies.IvyResolver
import br.com.devsrsouza.bukkript.script.definition.dependencies.baseDependencies
import br.com.devsrsouza.bukkript.script.definition.error.DiagnosticResult
import br.com.devsrsouza.bukkript.script.definition.error.requireOrFail
import kotlinx.coroutines.runBlocking
import java.io.File
import kotlin.script.experimental.api.*
import kotlin.script.experimental.dependencies.tryAddRepository
import kotlin.script.experimental.jvm.updateClasspath

fun resolveScriptAnnotation(
    ctx: ScriptConfigurationRefinementContext
): ResultWithDiagnostics<ScriptCompilationConfiguration> {
    val annotations = ctx.collectedData?.get(ScriptCollectedData.foundAnnotations)
        ?.takeIf { it.isNotEmpty() } ?: return ctx.compilationConfiguration.asSuccess()

    val reports = mutableListOf<ScriptDiagnostic>()

    val configuration = ctx.compilationConfiguration.with {

        var name = "None"
        var version = "None"
        var author = "Unknown"
        var website = "None"
        var logLevel = LogLevel.INFO

        val pluginDepends = mutableSetOf<String>()

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
            }
        }

        info(
            ScriptDescription(
                name,
                version,
                author,
                website,
                pluginDepends,
                logLevel
            )
        )
    }

    return if(reports.isEmpty()) {
        configuration.asSuccess()
    } else {
        makeFailureResult(reports)
    }
}
