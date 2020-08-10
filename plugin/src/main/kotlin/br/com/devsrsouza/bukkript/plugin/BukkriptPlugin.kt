package br.com.devsrsouza.bukkript.plugin

import br.com.devsrsouza.bukkript.plugin.manager.LoggingManagerImpl
import br.com.devsrsouza.bukkript.plugin.manager.ScriptManagerImpl
import br.com.devsrsouza.kotlinbukkitapi.architecture.KotlinPlugin

class BukkriptPlugin : KotlinPlugin() {

    val loggingManager = lifecycle(100) { LoggingManagerImpl(this) }
    val scriptManager = lifecycle(90) { ScriptManagerImpl(this) }

    override fun onPluginEnable() {
        registerCommands()
    }
}