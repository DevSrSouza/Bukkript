package br.com.devsrsouza.bukkript.plugin.manager

import br.com.devsrsouza.bukkript.plugin.BukkriptPlugin
import br.com.devsrsouza.bukkript.plugin.manager.logging.LogToFileInterceptor
import br.com.devsrsouza.bukkript.plugin.manager.logging.LogToPlayerInterceptor
import br.com.devsrsouza.bukkript.plugin.manager.logging.logFormatterInterceptor
import br.com.devsrsouza.bukkript.script.definition.api.LogLevel
import br.com.devsrsouza.kotlinbukkitapi.extensions.info
import br.com.devsrsouza.kotlinbukkitapi.utility.collections.onlinePlayerMapOf
import org.bukkit.entity.Player
import java.io.File

class LoggingManagerImpl(override val plugin: BukkriptPlugin) : LoggingManager {

    private data class Interceptor(
        val priority: Int,
        val interceptor: (scriptName: String, LogLevel, message: String) -> String?,
    ) : Comparable<Interceptor> {
        override fun compareTo(
            other: Interceptor,
        ): Int = other.priority.compareTo(priority)
    }

    private val interceptors = mutableListOf<Interceptor>()

    override fun logScript(scriptName: String, level: LogLevel, message: String) {
        val newMessage = interceptors.sortedDescending().fold(message as String?) { current, interceptor ->
            if (current != null) {
                interceptor.interceptor(scriptName, level, current)
            } else {
                null
            }
        }

        if (newMessage != null) {
            plugin.info(newMessage)
        }
    }

    override fun intercept(priority: Int, interceptor: (scriptName: String, LogLevel, message: String) -> String?) {
        interceptors.add(Interceptor(priority, interceptor))
    }

    override fun listenLog(player: Player, scriptName: String) {
        logToPlayerInterceptor.players.getOrPut(player, { mutableListOf() })
            .add(scriptName)
    }

    override fun unlistenLog(player: Player, scriptName: String) {
        logToPlayerInterceptor.players.get(player)
            ?.remove(scriptName)
    }

    override fun isListingLog(player: Player): Boolean {
        return logToPlayerInterceptor.players[player] != null
    }

    val scriptLogFolder = File(plugin.dataFolder, "logs")

    private val logToFileInterceptor = LogToFileInterceptor(scriptLogFolder)
    private val logToPlayerInterceptor by lazy { LogToPlayerInterceptor(onlinePlayerMapOf()) }

    override fun onPluginEnable() {
        intercept(Int.MIN_VALUE, ::logFormatterInterceptor)
        intercept(1, logToFileInterceptor::interceptor)
        intercept(2, logToPlayerInterceptor::interceptor)
    }
}
