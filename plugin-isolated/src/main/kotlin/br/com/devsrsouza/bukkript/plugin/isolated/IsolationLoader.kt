package br.com.devsrsouza.bukkript.plugin.isolated

import br.com.devsrsouza.bukkript.plugin.BukkriptPlugin
import br.com.devsrsouza.bukkript.plugin.isolated.manager.LoggingManagerImpl
import br.com.devsrsouza.bukkript.plugin.isolated.manager.ScriptManagerImpl

object IsolationLoader {

    @JvmStatic // easy java reflection
    fun load(plugin: BukkriptPlugin) = with(plugin) {
        val loggingManager = lifecycle(100) { LoggingManagerImpl(this) }
        val scriptManager = lifecycle(90) { ScriptManagerImpl(this, loggingManager) }

        registerCommands(scriptManager, loggingManager)
    }
}