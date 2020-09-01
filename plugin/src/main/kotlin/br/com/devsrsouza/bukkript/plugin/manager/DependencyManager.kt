package br.com.devsrsouza.bukkript.plugin.manager

import br.com.devsrsouza.bukkript.plugin.BukkriptPlugin
import br.com.devsrsouza.kotlinbukkitapi.architecture.lifecycle.LifecycleListener
import me.bristermitten.pdm.PDMBuilder

class DependencyManager(
    override val plugin: BukkriptPlugin
) : LifecycleListener<BukkriptPlugin> {

    init {
        val pdm = PDMBuilder(plugin).build()

        pdm.loadAllDependencies().join()
    }
}
