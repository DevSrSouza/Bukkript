package br.com.devsrsouza.bukkript.script

import br.com.devsrsouza.bukkript.api.BukkriptAPI
import org.bukkit.Bukkit
import org.jetbrains.kotlin.script.util.DependsOn
import org.jetbrains.kotlin.script.util.FilesAndMavenResolver
import org.jetbrains.kotlin.script.util.Repository
import java.io.File
import java.net.URLClassLoader
import kotlin.script.dependencies.ScriptContents
import kotlin.script.experimental.api.*
import kotlin.script.experimental.jvm.JvmDependency
import kotlin.script.experimental.jvm.jvm
import kotlin.script.experimental.jvm.updateClasspath

object BukkriptScriptConfiguration : ScriptCompilationConfiguration({
    defaultImports(bukkitImports + bukkriptImports + kotlinBukkitAPICoreImports
            + kotlinBukkitAPIAttributeStorageImports + kotlinBukkitAPIPluginsImports)

    jvm {
        updateClasspath(classpathFromBukkit())
    }

    refineConfiguration {
        onAnnotations<Script> { context ->
            val annotations = context.collectedData?.get(ScriptCollectedData.foundAnnotations)?.takeIf { it.isNotEmpty() }
                ?: return@onAnnotations context.compilationConfiguration.asSuccess()

            val scriptContents = object : ScriptContents {
                override val annotations: Iterable<Annotation> = annotations
                override val file: File? = null
                override val text: CharSequence? = null
            }

            val diagnostics = arrayListOf<ScriptDiagnostic>()

            return@onAnnotations try {

                val script = scriptContents.annotations.find { it.annotationClass == Script::class } as? Script

                if(script != null) {
                    ScriptCompilationConfiguration(context.compilationConfiguration) {
                        script.name.takeIf { it.isNotBlank() }?.also { name(it) }
                        script.version.takeIf { it.isNotBlank() }?.also { version(it) }
                        script.author.takeIf { it.isNotBlank() }?.also { author(it) }
                        script.authors.takeIf { it.isNotEmpty() }?.also { authors(it.toList()) }
                        script.website.takeIf { it.isNotBlank() }?.also { website(it) }

                        script.depend.takeIf { it.isNotEmpty() }?.also { dependScripts(it.toList()) }
                        script.pluginDepend.takeIf { it.isNotEmpty() }?.also { dependPlugins(it.toList()) }
                    }.asSuccess()
                } else return@onAnnotations context.compilationConfiguration.asSuccess()
            } catch (e: Throwable) {
                ResultWithDiagnostics.Failure(*diagnostics.toTypedArray(), e.asDiagnostics())
            }
        }
        //onAnnotations(DependsOn::class, Repository::class, handler = ::configureMavenDepsOnAnnotations)
    }
})

/*private val resolver = FilesAndMavenResolver()

fun configureMavenDepsOnAnnotations(context: ScriptConfigurationRefinementContext): ResultWithDiagnostics<ScriptCompilationConfiguration> {

    val annotations = context.collectedData?.get(ScriptCollectedData.foundAnnotations)?.takeIf { it.isNotEmpty() }
        ?: return context.compilationConfiguration.asSuccess()

    val scriptContents = object : ScriptContents {
        override val annotations: Iterable<Annotation> = annotations
        override val file: File? = null
        override val text: CharSequence? = null
    }

    val diagnostics = arrayListOf<ScriptDiagnostic>()

    val report = ReportFunction(context, diagnostics)

    return try {
        val newDepsFromResolver = resolver.resolve(scriptContents, emptyMap(), report, null).get()
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
}*/

fun classpathFromBukkit(): List<File> =
    (Bukkit.getServer().pluginManager.plugins
        .map { it.javaClass.classLoader } + BukkriptAPI::class.java.classLoader.parent)
        .mapNotNull { it as? URLClassLoader }
        .flatMap { it.urLs.toList() }.mapNotNull {
            try {
                File(it.toURI().schemeSpecificPart)
            } catch (e: java.net.URISyntaxException) {
                if (it.protocol != "file") null
                else File(it.file)
            }
        }