package br.com.devsrsouza.bukkript.script

import br.com.devsrsouza.bukkript.api.BukkriptAPI
import br.com.devsrsouza.bukkript.api.ScriptDescription
import br.com.devsrsouza.bukkript.script.annotations.*
import org.bukkit.Bukkit
import java.io.File
import java.net.URLClassLoader
import kotlin.script.experimental.api.*
import kotlin.script.experimental.jvm.jvm
import kotlin.script.experimental.jvm.updateClasspath

object BukkriptScriptConfiguration : ScriptCompilationConfiguration({
    defaultImports(bukkitImports + bukkriptImports + kotlinBukkitAPICoreImports
            + kotlinBukkitAPIAttributeStorageImports + kotlinBukkitAPIPluginsImports)

    jvm {
        updateClasspath(classpathFromBukkit())
    }

    refineConfiguration {
        onAnnotations(Script::class, DependPlugin::class, Import::class) { context ->
            val annotations = context.collectedData?.get(ScriptCollectedData.foundAnnotations)
                ?.takeIf { it.isNotEmpty() }
                ?: return@onAnnotations context.compilationConfiguration.asSuccess()

            val diagnostics = arrayListOf<ScriptDiagnostic>()

            ScriptCompilationConfiguration(context.compilationConfiguration) {

                val scriptDepends = mutableSetOf<String>()
                val pluginDepends = mutableSetOf<String>()

                var name= "None"
                var version = "None"
                var author = "Unknown"
                var website = "None"

                for (annotation in annotations) {
                    when (annotation) {
                        is Script -> {
                            annotation.name.takeIf { it.isNotBlank() }?.also { name = it }
                            annotation.version.takeIf { it.isNotBlank() }?.also { version = it }
                            annotation.author.takeIf { it.isNotBlank() }?.also { author = it }
                            annotation.website.takeIf { it.isNotBlank() }?.also { website = it }
                        }
                        is Import -> {
                            scriptDepends.add(annotation.script)
                        }
                        is DependPlugin -> {
                            pluginDepends.add(annotation.plugin)
                        }
                        else -> {
                            (annotation::class.members.find { it.name == "error" }?.call(annotation) as Exception?)?.printStackTrace()
                            //diagnostics.add() TODO
                        }
                    }
                }

                description(
                    ScriptDescription(
                        name,
                        version,
                        author,
                        emptyList(), // TODO
                        website,
                        scriptDepends.toList(),
                        pluginDepends.toList()
                    )
                )
            }.asSuccess()
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