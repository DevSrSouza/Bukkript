package br.com.devsrsouza.bukkript.script.definition.configuration

import br.com.devsrsouza.bukkript.script.definition.ScriptDescription
import br.com.devsrsouza.bukkript.script.definition.api.LogLevel
import kotlin.script.experimental.api.ScriptCompilationConfigurationKeys
import kotlin.script.experimental.util.PropertiesCollection

typealias CompilerKeys = ScriptCompilationConfigurationKeys

val CompilerKeys.info by PropertiesCollection.key<ScriptDescription>(
    ScriptDescription(
        "Unknown",
        "Unknown",
        "None",
        emptySet(),
        LogLevel.INFO,
        emptySet(),
    ),
)
