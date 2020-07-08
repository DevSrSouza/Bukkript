package br.com.devsrsouza.bukkript.plugin

import br.com.devsrsouza.bukkript.plugin.manager.ScriptManagerImpl
import br.com.devsrsouza.kotlinbukkitapi.architecture.KotlinPlugin

class BukkriptPlugin : KotlinPlugin() {

    val scriptManager = lifecycle { ScriptManagerImpl(this) }

    override fun onPluginEnable() {

    }
}