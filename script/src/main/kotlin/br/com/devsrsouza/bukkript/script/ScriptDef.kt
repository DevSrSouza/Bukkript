package br.com.devsrsouza.bukkript.script

import br.com.devsrsouza.bukkript.Bukkript
import org.jetbrains.kotlin.script.util.DependsOn
import org.jetbrains.kotlin.script.util.FilesAndMavenResolver
import org.jetbrains.kotlin.script.util.Repository
import java.io.File
import kotlin.script.dependencies.ScriptContents
import kotlin.script.dependencies.ScriptDependenciesResolver
import kotlin.script.experimental.annotations.KotlinScript
import kotlin.script.experimental.api.*
import kotlin.script.experimental.jvm.JvmDependency
import kotlin.script.experimental.jvm.compat.mapLegacyDiagnosticSeverity
import kotlin.script.experimental.jvm.compat.mapLegacyScriptPosition

@KotlinScript("Bukkript script", "bukkrit.kts", BukkriptScriptConfiguration::class)
abstract class BukkriptScript(val plugin: Bukkript) {
    private var disable: (() -> Unit)? = null

    fun onDisable(block: () -> Unit) {
        disable = block
    }
}

object BukkriptScriptConfiguration : ScriptCompilationConfiguration({
    defaultImports(bukkitImports + bukkriptImports + kotlinBukkitAPICoreImports
            + kotlinBukkitAPIAttributeStorageImports + kotlinBukkitAPIPluginsImports)

    refineConfiguration {
        //onAnnotations(Depend::class, SoftDepend::class)
        onAnnotations(DependsOn::class, Repository::class, handler = ::configureMavenDepsOnAnnotations)
    }
})

private val resolver = FilesAndMavenResolver()

fun configureMavenDepsOnAnnotations(context: ScriptConfigurationRefinementContext): ResultWithDiagnostics<ScriptCompilationConfiguration> {

    val annotations = context.collectedData?.get(ScriptCollectedData.foundAnnotations)?.takeIf { it.isNotEmpty() }
        ?: return context.compilationConfiguration.asSuccess()

    val scriptContents = object : ScriptContents {
        override val annotations: Iterable<Annotation> = annotations
        override val file: File? = null
        override val text: CharSequence? = null
    }

    val diagnostics = arrayListOf<ScriptDiagnostic>()

    fun report(
        severity: ScriptDependenciesResolver.ReportSeverity,
        message: String,
        position: ScriptContents.Position?
    ) {
        diagnostics.add(
            ScriptDiagnostic(
                message,
                mapLegacyDiagnosticSeverity(severity),
                context.script.locationId,
                mapLegacyScriptPosition(position)
            )
        )
    }

    return try {
        val newDepsFromResolver = resolver.resolve(scriptContents, emptyMap(), ::report, null).get()
            ?: return context.compilationConfiguration.asSuccess(diagnostics)

        val resolvedClasspath = newDepsFromResolver.classpath.toList().takeIf { it.isNotEmpty() }
            ?: return context.compilationConfiguration.asSuccess(diagnostics)

        ScriptCompilationConfiguration(context.compilationConfiguration) {
            dependencies(JvmDependency(resolvedClasspath))
            //withUpdatedClasspath(resolvedClasspath).asSuccess(diagnostics)
        }.asSuccess()
    } catch (e: Throwable) {
        ResultWithDiagnostics.Failure(*diagnostics.toTypedArray(), e.asDiagnostics())
    }
}