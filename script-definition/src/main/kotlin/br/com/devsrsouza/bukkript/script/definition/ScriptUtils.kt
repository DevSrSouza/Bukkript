package br.com.devsrsouza.bukkript.script.definition

import java.io.File

val File.isBukkriptScript get() = path.endsWith(".$BUKKRIPT_EXTENSION", true)

fun File.bukkriptRelative(scriptDir: File) = relativeTo(scriptDir)

val File.bukkriptName get() = path.removeSuffix(".$BUKKRIPT_EXTENSION")

