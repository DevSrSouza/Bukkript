package br.com.devsrsouza.bukkript.api

data class ScriptDescription(
    val name: String,
    val version: String,
    val author: String,
    val authors: List<String>,
    val website: String,
    val depend: List<String>,
    val pluginDepend: List<String>
)