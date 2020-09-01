package br.com.devsrsouza.bukkript.script.definition.resolver

import br.com.devsrsouza.bukkript.script.definition.classpath.classpathFromPluginsByExcluding
import br.com.devsrsouza.bukkript.script.definition.dependencies.SPIGOT_DEPENDENCY
import br.com.devsrsouza.bukkript.script.definition.dependencies.baseDependencies
import br.com.devsrsouza.bukkript.script.definition.dependencies.buildBaseDependencies
import br.com.devsrsouza.bukkript.script.definition.dependencies.ignoredPluginDependencies
import br.com.devsrsouza.bukkript.script.definition.findParentPluginFolder
import br.com.devsrsouza.bukkript.script.definition.isJar
import kotlinx.coroutines.runBlocking
import java.io.File
import kotlin.script.experimental.api.*
import kotlin.script.experimental.dependencies.*
import kotlin.script.experimental.dependencies.impl.resolve
import kotlin.script.experimental.dependencies.maven.MavenDependenciesResolver
import kotlin.script.experimental.dependencies.resolveFromAnnotations
import kotlin.script.experimental.jvm.JvmDependency
import kotlin.script.experimental.jvm.updateClasspath
import kotlin.script.experimental.util.filterByAnnotationType

fun resolveScriptStaticDependencies(
    ctx: ScriptConfigurationRefinementContext
): ResultWithDiagnostics<ScriptCompilationConfiguration> {

    val configuration = ctx.compilationConfiguration.with {

        val mavenResolver = MavenDependenciesResolver()

        // If spigot is not available at this time, this means that is server running at a intellij
        if(!isPackageAvailable(SPIGOT_DEPENDENCY.fqnPackage)) {

            val scriptFile = ctx.script.finalFile

            val fileResolver = FileSystemDependenciesResolver()

            val files = mutableListOf<File>()
            val sources = mutableListOf<File>()


            runBlocking {
                // Resolve Server/User Dependencies
                val pluginsFolder = scriptFile.findParentPluginFolder(10)

                //  If was plugins folder, use it to find dependencies, if not, use base dependencies
                if (pluginsFolder != null) {
                    val allPlugins = (pluginsFolder.listFiles() ?: emptyArray())
                        .filter { it.isJar() }
                        .filterNot { plugin ->
                            ignoredPluginDependencies.any { plugin.name.contains(it, ignoreCase = true) }
                        }

                    val serverJar = (pluginsFolder.parentFile?.listFiles() ?: emptyArray())
                        .filter { it.isJar() }

                    for (jar in allPlugins + serverJar) {
                        files += fileResolver.resolve(jar.absolutePath, mapOf()).valueOrNull()  ?: emptyList()
                    }
                }

                // Resolve Ivy Static Dependencies
                for ((fqn, repositories, artifacts) in baseDependencies) {
                    if (!isPackageAvailable(fqn)) {
                        for (repository in repositories) {
                            mavenResolver.addRepository(repository)
                        }

                        for (artifact in artifacts) {
                            // Adding the dependency only in a plugin folder was not available
                            if(pluginsFolder == null)
                                files += mavenResolver.resolve(artifact, mapOf()).valueOrNull() ?: emptyList()

                            // Adding the source codes
                            sources += mavenResolver.resolve(artifactAsSource(artifact), mapOf()).valueOrNull() ?: emptyList()
                        }
                    }
                }
            }


            updateClasspath(files)

            ide.dependenciesSources.append(JvmDependency(sources))
        } else {
            // Is running on a server, then, add the hole classpath of the plugins here
            updateClasspath(classpathFromPluginsByExcluding())

            val buildDependencies = mutableListOf<File>()

            // Resolve Ivy Static Dependencies
            for ((fqn, repositories, artifacts) in buildBaseDependencies) {
                for (repository in repositories) {
                    mavenResolver.addRepository(repository)
                }

                runBlocking {
                    for (artifact in artifacts) {
                        buildDependencies += mavenResolver.resolve(artifact, mapOf()).valueOrNull() ?: emptyList()
                    }
                }
            }

            updateClasspath(buildDependencies)
        }
    }

    return configuration.asSuccess()
}

data class ExternalDependencies(
    val compiled: Set<File>,
    val sources: Set<File>
)

fun resolveExternalDependencies(
    scriptSource: SourceCode,
    annotations: List<Annotation>
): ExternalDependencies {

    // If is running in the Server, use server internal server cache folder for the libraries
    // Cache dir will be applied in the plugin by setting user.home property
    val mavenResolver = MavenDependenciesResolver()

    val sources = mutableSetOf<File>()

    // Checking where is spigot avaible, if not, is not running at the server
    // And should use sources.
    if(!isPackageAvailable(SPIGOT_DEPENDENCY.fqnPackage)) {
        // Downloading sources for IntelliJ
        runBlocking {
            sources += mavenResolver.resolveSourceFromAnnotations(annotations).valueOrThrow()
        }
    }

    return runBlocking {
        ExternalDependencies(
            // Downloading compiled dependencies
            mavenResolver.resolveFromAnnotations(annotations).valueOrThrow().toSet(),
            sources
        )
    }
}

private fun artifactAsSource(artifactsCoordinates: String): String {
    return if(artifactsCoordinates.count { it == ':' } == 2) {
        val lastColon = artifactsCoordinates.lastIndexOf(':')
        artifactsCoordinates.toMutableList().apply { addAll(lastColon, ":jar:sources".toList()) }.joinToString("")
    } else {
        artifactsCoordinates
    }
}

/**
 * An extension function that configures repositories and resolves artifacts denoted by the [Repository] and [DependsOn] annotations
 */
suspend fun ExternalDependenciesResolver.resolveSourceFromScriptSourceAnnotations(
    annotations: Iterable<ScriptSourceAnnotation<*>>
): ResultWithDiagnostics<List<File>> {
    val reports = mutableListOf<ScriptDiagnostic>()
    annotations.forEach { (annotation, locationWithId) ->
        when (annotation) {
            is Repository -> {

                for (coordinates in annotation.repositoriesCoordinates) {
                    val added = addRepository(coordinates, ExternalDependenciesResolver.Options.Empty, locationWithId)
                        .also { reports.addAll(it.reports) }
                        .valueOr { return it }

                    if (!added)
                        return reports + makeFailureResult(
                            "Unrecognized repository coordinates: $coordinates",
                            locationWithId = locationWithId
                        )
                }
            }
            is DependsOn -> {}
            else -> return reports + makeFailureResult("Unknown annotation ${annotation.javaClass}", locationWithId = locationWithId)
        }
    }

    return reports + annotations.filterByAnnotationType<DependsOn>()
        .flatMapSuccess { (annotation, locationWithId) ->
            annotation.artifactsCoordinates.asIterable().flatMapSuccess { artifactCoordinates ->
                resolve(artifactAsSource(artifactCoordinates), ExternalDependenciesResolver.Options.Empty, locationWithId)
            }
        }
}

/**
 * An extension function that configures repositories and resolves artifacts denoted by the [Repository] and [DependsOn] annotations
 */
suspend fun ExternalDependenciesResolver.resolveSourceFromAnnotations(
    annotations: Iterable<Annotation>
): ResultWithDiagnostics<List<File>> {
    val scriptSourceAnnotations = annotations.map { ScriptSourceAnnotation(it, null) }
    return resolveSourceFromScriptSourceAnnotations(scriptSourceAnnotations)
}