package br.com.devsrsouza.bukkript.api

import org.bukkit.plugin.Plugin

class PluginDependencyImport(val plugin: Plugin, private val imports: List<String>, val module: String = "") : DependecyImport {
    override val name = "${plugin.name}${if(module.isNotEmpty()) ":" else "" }$module"
    override fun imports() = imports
}