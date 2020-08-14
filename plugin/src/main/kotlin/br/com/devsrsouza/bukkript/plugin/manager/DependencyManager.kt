package br.com.devsrsouza.bukkript.plugin.manager

import br.com.devsrsouza.bukkript.plugin.BukkriptPlugin
import br.com.devsrsouza.bukkript.script.definition.dependencies.IvyResolver
import br.com.devsrsouza.kotlinbukkitapi.architecture.lifecycle.LifecycleListener
import br.com.devsrsouza.kotlinbukkitapi.extensions.plugin.info
import kotlinx.coroutines.runBlocking
import java.io.File
import java.net.URL
import java.net.URLClassLoader
import kotlin.script.experimental.dependencies.ExternalDependenciesResolver
import kotlin.script.experimental.dependencies.maven.MavenDependenciesResolver
import kotlin.script.experimental.dependencies.tryResolve

class DependencyManager(
    override val plugin: BukkriptPlugin
) : LifecycleListener<BukkriptPlugin> {

    private val kotlinVersion = "1.3.72"

    private val compilerDependencies = listOf(
        "org.jetbrains.kotlin:kotlin-scripting-jvm-host-embeddable:$kotlinVersion"
    )

    private val aetherDependencies = listOf(
        "org.eclipse.aether:aether-api:1.1.0",
        "org.eclipse.aether:aether-impl:1.1.0",
        "org.eclipse.aether:aether-util:1.1.0",
        "org.eclipse.aether:aether-connector-basic:1.1.0",
        "org.eclipse.aether:aether-transport-wagon:1.1.0",
        "org.eclipse.aether:aether-transport-http:1.1.0",
        "org.eclipse.aether:aether-transport-file:1.1.0",
        "org.apache.maven:maven-core:3.6.3",
        "org.apache.maven:maven-settings-builder:3.6.3",
        "org.apache.maven:maven-aether-provider:3.3.9",
        "org.apache.maven.wagon:wagon-provider-api:3.3.4"
    )

    private val addToUrlClassLoaderMethod by lazy {
        URLClassLoader::class.java.getDeclaredMethod("addURL", URL::class.java).apply {
            isAccessible = true
        }
    }

    private val currentClassLoader by lazy {
        this::class.java.classLoader as URLClassLoader
    }

    init {
        downloadAetherDependency()

        downloadCompilerDependency()
    }

    private fun downloadAetherDependency() {
        val ivyResolver = IvyResolver(File(plugin.dataFolder.parentFile.parentFile, "klibs"))

        ivyResolver.provideDependencies(aetherDependencies)
    }

    private fun downloadCompilerDependency() {
        val mavenResolver = MavenDependenciesResolver()

        mavenResolver.provideDependencies(compilerDependencies)
    }

    private fun insertDependencyAtClassLoader(dependencies: List<File>) {
        for (jar in dependencies) {
            addToUrlClassLoaderMethod.invoke(currentClassLoader, jar.toURI().toURL())
        }
    }

    private fun ExternalDependenciesResolver.provideDependencies(dependencies: List<String>) {
        runBlocking {
            for (dependency in dependencies) {
                info("Downloading dependency: $dependency")
                val dependenciesFiles = tryResolve(dependency) ?: emptyList()
                info("Downloaded dependencies: ${dependenciesFiles.map { it.nameWithoutExtension }}")

                insertDependencyAtClassLoader(dependenciesFiles)
            }
        }
    }
}
