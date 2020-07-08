package br.com.devsrsouza.bukkript.core

data class BukkriptScriptDescription(
    val name: String,
    val version: String,
    val author: String,
    val website: String,
    val pluginDependencies: Set<String>
)