package br.com.devsrsouza.bukkript.api

import java.io.Serializable

data class ScriptDescription(
    val name: String,
    val version: String,
    val author: String,
    val authors: List<String>,
    val website: String,
    val depend: List<String>,
    val pluginDepend: List<String>
) : Serializable {
    companion object {
        val DEFAULT get() = ScriptDescription(
            "None",
            "None",
            "Unknown",
            emptyList(),
            "None",
            emptyList(),
            emptyList()
        )
    }
}