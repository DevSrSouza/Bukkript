package br.com.devsrsouza.bukkript.script.definition

import java.io.File
import kotlin.script.experimental.host.FileScriptSource

val File.isBukkriptScript get() = path.endsWith(".$BUKKRIPT_EXTENSION", true)

fun File.bukkriptRelative(scriptDir: File) = relativeTo(scriptDir)

val File.bukkriptName get() = path.removeSuffix(".$BUKKRIPT_EXTENSION")

fun File.bukkriptNameRelative(scriptDir: File) = bukkriptRelative(scriptDir).bukkriptName

fun FileScriptSource.bukkritRelative(scriptDir: File) = file.bukkriptRelative(scriptDir)

fun FileScriptSource.bukkritNameRelative(scriptDir: File) = file.bukkriptNameRelative(scriptDir)

