package br.com.devsrsouza.bukkript.plugin.isolated.manager

import br.com.devsrsouza.bukkript.plugin.BukkriptPlugin
import br.com.devsrsouza.bukkript.script.definition.api.LogLevel
import br.com.devsrsouza.kotlinbukkitapi.architecture.lifecycle.LifecycleListener
import org.bukkit.entity.Player

interface LoggingManager : LifecycleListener<BukkriptPlugin> {
    fun logScript(scriptName: String, level: LogLevel, message: String)

    /**
     * [interceptor] returns null if is to disable the logging
     */
    fun intercept(priority: Int, interceptor: (scriptName: String, LogLevel, message: String) -> String?)

    /**
     * Make the player receive the messages directly in their chat.
     */
    fun listenLog(player: Player, scriptName: String)

    fun unlistenLog(player: Player, scriptName: String)

    fun isListingLog(player: Player): Boolean
}