package br.com.devsrsouza.bukkript.host

import br.com.devsrsouza.bukkript.api.BukkriptAPI
import br.com.devsrsouza.kotlinbukkitapi.extensions.plugin.severe
import org.bukkit.plugin.Plugin
import java.io.File
import kotlin.script.experimental.api.ResultWithDiagnostics
import kotlin.script.experimental.api.resultOrNull

fun <R> ResultWithDiagnostics<R>.resultOrSeveral(plugin: Plugin): R? {
    return resultOrNull() ?: run {
        for (diag in reports) { plugin.severe(diag.toString()) }
        null
    }
}

fun File.scriptName(plugin: BukkriptAPI) = relativeTo(plugin.SCRIPT_DIR).path.substringBeforeLast(".")