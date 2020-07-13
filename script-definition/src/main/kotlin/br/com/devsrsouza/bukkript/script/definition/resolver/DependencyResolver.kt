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
import java.util.concurrent.ConcurrentSkipListSet
import kotlin.script.experimental.api.*
import kotlin.script.experimental.dependencies.FileSystemDependenciesResolver
import kotlin.script.experimental.dependencies.tryAddRepository
import kotlin.script.experimental.dependencies.tryResolve
import kotlin.script.experimental.jvm.updateClasspath

fun resolveIdeScriptDependencies(
    ctx: ScriptConfigurationRefinementContext
): ResultWithDiagnostics<ScriptCompilationConfiguration> {

    val configuration = ctx.compilationConfiguration.with {

        // If spigot is not available at this time, this means that is server running at a server
        if(!isPackageAvailable(SPIGOT_DEPENDENCY.fqnPackage)) {

            val scriptFile = ctx.script.finalFile

            val ivyResolver = IvyResolver(null)
            val fileResolver = FileSystemDependenciesResolver()

            // TODO: add support to find plugins and the server jar and add to the classpath

            val files = mutableListOf<File>()


            runBlocking {
                // Resolve Server/User Dependencies
                val pluginsFolder = scriptFile.findParentPluginFolder(10)

                //  If was plugins folder, use it to find dependencies, if not, use base dependencies
                if (pluginsFolder != null) {
                    val allPlugins = (pluginsFolder.listFiles() ?: emptyArray())
                        .filter { it.isJar() }

                    val serverJar = (pluginsFolder.parentFile?.listFiles() ?: emptyArray())
                        .filter { it.isJar() }

                    for (jar in allPlugins + serverJar) {
                        files += fileResolver.tryResolve(jar.absolutePath) ?: emptyList()
                    }
                } else {
                    // Resolve Ivy Static Dependencies
                    for ((fqn, repositories, artifacts) in baseDependencies) {
                        if (!isPackageAvailable(fqn)) {
                            for (repository in repositories) {
                                ivyResolver.tryAddRepository(repository)
                            }

                            for (artifact in artifacts) {
                                files += ivyResolver.tryResolve(artifact) ?: emptyList()
                            }
                        }
                    }
                }
            }


            updateClasspath(files)
        } else {
            // Is running on a server, then, add the hole classpath of the plugins here
            updateClasspath(classpathFromPlugins())
        }
    }

    return configuration.asSuccess()
}

fun resolveExternalDependencies(
    scriptSource: SourceCode,
    repositories: Set<String>,
    dependencies: Set<String>
): Set<File> {
    val scriptFile = scriptSource.finalFile

    val pluginsFolder = scriptFile.findParentPluginFolder(10)

    // If is running in the Server, use server internal server cache folder for the libraries
    val ivyResolver = IvyResolver(
        pluginsFolder?.parentFile?.let { File(it, ".klibs").apply { mkdirs() } }
    )

    for(repository in repositories) {
        ivyResolver.tryAddRepository(repository)
    }

    return runBlocking {
        ConcurrentSkipListSet<File>().also {
            dependencies.asFlow()
                //.buffer(8)
                .flatMapConcat { (ivyResolver.tryResolve(it) ?: emptyList()).asFlow() }
                .toCollection(it)
        }
    }
}