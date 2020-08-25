package br.com.devsrsouza.bukkript.plugin.manager

import br.com.devsrsouza.bukkript.plugin.BukkriptPlugin
import br.com.devsrsouza.bukkript.plugin.manager.host.HostClassLoader
import br.com.devsrsouza.kotlinbukkitapi.architecture.lifecycle.LifecycleListener
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toCollection
import kotlinx.coroutines.runBlocking
import me.bristermitten.pdm.PDMBuilder
import me.bristermitten.pdmlibs.artifact.Artifact
import me.bristermitten.pdmlibs.artifact.SnapshotArtifact
import me.bristermitten.pdm.DependencyManager as PDMDependencyManager
import java.io.File
import java.util.concurrent.CompletableFuture

class DependencyManager(
    override val plugin: BukkriptPlugin
) : LifecycleListener<BukkriptPlugin> {

    private val isolatedDependency = "br.com.devsrsouza.bukkript:plugin-isolated:${plugin.description.version}"

    private val dependencies: List<File>
    private val hostClassLoader: HostClassLoader

    init {
        val pdm = PDMBuilder(plugin)
            .build()
        val dep = isolatedDependency.split(":")
        pdm.addRequiredDependency(SnapshotArtifact(dep[0], dep[1], dep[2]))

        val depsField = pdm::class.java.getDeclaredField("requiredDependencies").apply {
            isAccessible = true
        }

        val deps = depsField.get(pdm) as Set<Artifact>

        val managerField = pdm::class.java.getDeclaredField("manager").apply {
            isAccessible = true
        }

        val manager = managerField.get(pdm) as PDMDependencyManager

        val downloadMethod = PDMDependencyManager::class.java.getDeclaredMethod("download", Artifact::class.java).apply {
            isAccessible = true
        }

        dependencies = runBlocking {
            deps.asFlow().buffer(10)
                .map { downloadMethod.invoke(manager, it) as CompletableFuture<File> }
                .map { it.join() }
                .toCollection(mutableListOf())
        }//pdm.downloadAllDependencies().join()

        hostClassLoader = HostClassLoader(plugin, dependencies, plugin::class.java.classLoader)
    }
}
