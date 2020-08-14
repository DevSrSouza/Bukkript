package br.com.devsrsouza.bukkript.plugin

import br.com.devsrsouza.bukkript.plugin.exceptions.SERVER_NOT_SUPPORTED_MESSAGE
import br.com.devsrsouza.bukkript.plugin.exceptions.ServerNotSupportedException
import br.com.devsrsouza.bukkript.plugin.manager.DependencyManager
import br.com.devsrsouza.bukkript.plugin.manager.LoggingManagerImpl
import br.com.devsrsouza.bukkript.plugin.manager.ScriptManagerImpl
import br.com.devsrsouza.kotlinbukkitapi.architecture.KotlinPlugin
import br.com.devsrsouza.kotlinbukkitapi.extensions.text.plus
import org.bukkit.ChatColor

class BukkriptPlugin : KotlinPlugin() {

    val dependencyManager = lifecycle(110) { DependencyManager(this) }
    val loggingManager = lifecycle(100) { LoggingManagerImpl(this) }
    val scriptManager = lifecycle(90) { ScriptManagerImpl(this) }

    override fun onPluginEnable() {
        registerCommands()

        checkServerVersion()
    }

    private fun checkServerVersion() {
        if(server.version.contains("craftbukkit", ignoreCase = true)) {
            repeat(5) {
                error(ChatColor.RED + SERVER_NOT_SUPPORTED_MESSAGE)
            }

            server.pluginManager.disablePlugin(this)

            throw ServerNotSupportedException()
        }
    }
}