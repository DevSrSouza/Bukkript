package br.com.devsrsouza.bukkript.script.definition

import java.io.File
import kotlin.script.experimental.host.FileScriptSource

val File.isBukkriptScript get() = path.endsWith(".$BUKKRIPT_EXTENSION", true)

private fun File.bukkriptRelative(scriptDir: File) = relativeTo(scriptDir)

val File.bukkriptName get() = path.removeSuffix(".$BUKKRIPT_EXTENSION")

fun File.bukkriptNameRelative(scriptDir: File) = bukkriptRelative(scriptDir).bukkriptName

fun FileScriptSource.bukkritNameRelative(scriptDir: File) = file.bukkriptNameRelative(scriptDir)

fun File.isJar() = extension == "jar"

fun File.findParentPluginFolder(depth: Int): File? {
    var current: File? = parentFile
    for(i in 0 until depth) {
        if(current == null) return null

        if(
            current.name == "plugins" &&
            current.list()?.any { it.contains("Bukkript") } == true
        ) {
            return current
        } else {
            current = current.parentFile
        }
    }

    return null
}

