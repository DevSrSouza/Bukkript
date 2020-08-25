package br.com.devsrsouza.bukkript.plugin.manager

import br.com.devsrsouza.bukkript.plugin.BukkriptPlugin
import br.com.devsrsouza.bukkript.plugin.manager.host.HostClassLoader
import br.com.devsrsouza.kotlinbukkitapi.architecture.lifecycle.LifecycleListener
import me.bristermitten.pdm.PDMBuilder
import java.io.File

class DependencyManager(
    override val plugin: BukkriptPlugin
) : LifecycleListener<BukkriptPlugin> {

    private val dependencies: List<File>
    private val hostClassLoader: HostClassLoader

    init {
        val pdm = PDMBuilder(plugin)
            .build()

        dependencies = pdm.downloadAllDependencies().join()

        hostClassLoader = HostClassLoader(plugin, dependencies, plugin::class.java.classLoader)
    }
}
