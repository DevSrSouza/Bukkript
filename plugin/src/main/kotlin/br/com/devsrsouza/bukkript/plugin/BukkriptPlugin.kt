package br.com.devsrsouza.bukkript.plugin

import br.com.devsrsouza.bukkript.plugin.manager.DependencyManager
import br.com.devsrsouza.kotlinbukkitapi.architecture.KotlinPlugin
import br.com.devsrsouza.kotlinbukkitapi.extensions.text.plus
import org.bukkit.ChatColor

class BukkriptPlugin : KotlinPlugin() {

    val dependencyManager = lifecycle(110) { DependencyManager(this) }

    override fun onPluginEnable() {
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