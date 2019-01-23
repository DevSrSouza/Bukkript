package br.com.devsrsouza.bukkript.script

import kotlin.script.experimental.api.ScriptCompilationConfigurationKeys
import kotlin.script.experimental.util.PropertiesCollection

typealias CompilerKeys = ScriptCompilationConfigurationKeys

val CompilerKeys.name by PropertiesCollection.key<String>("None")

val CompilerKeys.version by PropertiesCollection.key<String>("None")

val CompilerKeys.author by PropertiesCollection.key<String>("None")

val CompilerKeys.authors by PropertiesCollection.key<List<String>>(emptyList())

val CompilerKeys.website by PropertiesCollection.key<String>("None")

val CompilerKeys.dependScripts by PropertiesCollection.key<List<String>>(emptyList())

val CompilerKeys.softDependScripts by PropertiesCollection.key<List<String>>(emptyList())

val CompilerKeys.dependPlugins by PropertiesCollection.key<List<String>>(emptyList())

val CompilerKeys.softDependPlugins by PropertiesCollection.key<List<String>>(emptyList())