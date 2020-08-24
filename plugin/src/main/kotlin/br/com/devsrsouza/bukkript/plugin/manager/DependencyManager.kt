package br.com.devsrsouza.bukkript.plugin.manager

import br.com.devsrsouza.bukkript.plugin.BukkriptPlugin
import br.com.devsrsouza.bukkript.plugin.manager.host.HostClassLoader
import br.com.devsrsouza.kotlinbukkitapi.architecture.lifecycle.LifecycleListener
import me.bristermitten.pdm.PDMBuilder

class DependencyManager(
    override val plugin: BukkriptPlugin
) : LifecycleListener<BukkriptPlugin> {

    val hostClassLoader = HostClassLoader(plugin::class.java.classLoader)

    init {
        val pdm = PDMBuilder(plugin)
            .classLoader(hostClassLoader)
            .build()

        pdm.loadAllDependencies().join()
    }
}
