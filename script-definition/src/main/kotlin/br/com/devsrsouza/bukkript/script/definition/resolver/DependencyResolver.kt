package br.com.devsrsouza.bukkript.script.definition.resolver

import br.com.devsrsouza.bukkript.script.definition.configuration.classpathFromPlugins
import br.com.devsrsouza.bukkript.script.definition.dependencies.IvyResolver
import br.com.devsrsouza.bukkript.script.definition.dependencies.SPIGOT_DEPENDENCY
import br.com.devsrsouza.bukkript.script.definition.dependencies.baseDependencies
import br.com.devsrsouza.bukkript.script.definition.findParentPluginFolder
import br.com.devsrsouza.bukkript.script.definition.isJar
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import java.io.File
import kotlin.script.experimental.api.*
import kotlin.script.experimental.dependencies.FileSystemDependenciesResolver
import kotlin.script.experimental.dependencies.addRepository
import kotlin.script.experimental.dependencies.impl.resolve
import kotlin.script.experimental.jvm.JvmDependency
import kotlin.script.experimental.jvm.updateClasspath

fun resolveScriptStaticDependencies(
    ctx: ScriptConfigurationRefinementContext
): ResultWithDiagnostics<ScriptCompilationConfiguration> {

    val configuration = ctx.compilationConfiguration.with {

        // If spigot is not available at this time, this means that is server running at a server
        if(!isPackageAvailable(SPIGOT_DEPENDENCY.fqnPackage)) {

            val scriptFile = ctx.script.finalFile

            val ivyResolver = IvyResolver(null)
            val sourcesResolver = IvyResolver(null, true)
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
                        .filterNot { it.name.contains("bukkript", ignoreCase = true) }

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
                            ivyResolver.addRepository(repository)
                            sourcesResolver.addRepository(repository)
                        }

                        for (artifact in artifacts) {
                            // Adding the dependency only in a plugin folder was not available
                            if(pluginsFolder == null)
                                files += ivyResolver.resolve(artifact, mapOf()).valueOrNull() ?: emptyList()

                            // Adding the source codes
                            sources += sourcesResolver.resolve(artifact, mapOf()).valueOrNull() ?: emptyList()
                        }
                    }
                }
            }


            updateClasspath(files)

            // TODO: Bukkript definition sources is missing, adding when it get a Maven repo.
            ide.dependenciesSources.append(JvmDependency(sources))
        } else {
            // Is running on a server, then, add the hole classpath of the plugins here
            updateClasspath(classpathFromPlugins())
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
    repositories: Set<String>,
    dependencies: Set<String>
): ExternalDependencies {
    val scriptFile = scriptSource.finalFile

    val pluginsFolder = scriptFile.findParentPluginFolder(10)
    val cacheDir = pluginsFolder?.parentFile?.let { File(it, "klibs").apply { mkdirs() } }

    // If is running in the Server, use server internal server cache folder for the libraries
    val ivyResolver = IvyResolver(cacheDir)
    // If is running in the IntelliJ we will not need cache dir, and we can use the .ivy2
    val sourcesResolver = IvyResolver(null, true)

    for(repository in repositories) {
        ivyResolver.addRepository(repository)
        sourcesResolver.addRepository(repository)
    }

    val sources = mutableSetOf<File>()

    // Checking where is spigot avaible, if not, is not running at the server
    // And should use sources.
    if(!isPackageAvailable(SPIGOT_DEPENDENCY.fqnPackage)) {
        // Downloading sources for IntelliJ
        runBlocking {
            sources += dependencies.asFlow()
                //.buffer(8)
                .flatMapConcat { (sourcesResolver.resolve(it, mapOf()).valueOrNull() ?: emptyList()).asFlow() }
                .toSet()
        }
    }

    return runBlocking {
        ExternalDependencies(
            // Downloading compiled dependencies
            dependencies.asFlow()
                //.buffer(8)
                .flatMapConcat { (ivyResolver.resolve(it, mapOf()).valueOrNull() ?: emptyList()).asFlow() }
                .toSet(),
            sources
        )
    }
}