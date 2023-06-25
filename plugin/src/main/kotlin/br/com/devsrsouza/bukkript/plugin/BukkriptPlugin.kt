package br.com.devsrsouza.bukkript.plugin

import br.com.devsrsouza.bukkript.plugin.manager.LoggingManagerImpl
import br.com.devsrsouza.bukkript.plugin.manager.ScriptManagerImpl
import br.com.devsrsouza.kotlinbukkitapi.architecture.KotlinPlugin
import br.com.devsrsouza.kotlinbukkitapi.extensions.plus
import org.bstats.bukkit.Metrics
import org.bukkit.ChatColor

const val BSTATS_PLUGIN_ID = 8819

class BukkriptPlugin : KotlinPlugin() {

    val loggingManager = lifecycle(100) { LoggingManagerImpl(this) }
    val scriptManager = lifecycle(90) { ScriptManagerImpl(this) }

    internal lateinit var metrics: Metrics private set

    override fun onPluginEnable() {
        registerCommands()

        checkServerVersion()

        // setup metrics
        metrics = Metrics(this, BSTATS_PLUGIN_ID)
    }

    private fun checkServerVersion() {
        if (server.version.contains("craftbukkit", ignoreCase = true)) {
            repeat(5) {
                error(ChatColor.RED + SERVER_NOT_SUPPORTED_MESSAGE)
            }

            server.pluginManager.disablePlugin(this)

            throw ServerNotSupportedException()
        }
    }
}
