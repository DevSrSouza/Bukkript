package br.com.devsrsouza.bukkript.script

import br.com.devsrsouza.bukkript.api.ScriptDescription
import kotlin.script.experimental.api.ScriptCompilationConfigurationKeys
import kotlin.script.experimental.util.PropertiesCollection

typealias CompilerKeys = ScriptCompilationConfigurationKeys

val CompilerKeys.description by PropertiesCollection.key<ScriptDescription>(
    ScriptDescription(
        "None",
        "None",
        "Unknown",
        emptyList(),
        "None",
        emptyList(),
        emptyList()
    )
)

val CompilerKeys.name by PropertiesCollection.key<String>("None")

val CompilerKeys.version by PropertiesCollection.key<String>("None")

val CompilerKeys.author by PropertiesCollection.key<String>("None")

val CompilerKeys.authors by PropertiesCollection.key<List<String>>(emptyList())

val CompilerKeys.website by PropertiesCollection.key<String>("None")

val CompilerKeys.dependScripts by PropertiesCollection.key<List<String>>(emptyList())

val CompilerKeys.softDependScripts by PropertiesCollection.key<List<String>>(emptyList())

val CompilerKeys.dependPlugins by PropertiesCollection.key<List<String>>(emptyList())