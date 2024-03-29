package br.com.devsrsouza.bukkript.script.definition

import br.com.devsrsouza.bukkript.script.definition.api.LogLevel
import java.io.Serializable

data class ScriptDescription(
    val version: String,
    val author: String,
    val website: String,
    val pluginDependencies: Set<String>,
    var logLevel: LogLevel, // Mutable for tooling!
    val dependenciesFiles: Set<String>,
) : Serializable
