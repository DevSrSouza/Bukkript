package br.com.devsrsouza.bukkript.host

import br.com.devsrsouza.bukkript.api.BukkriptAPI
import br.com.devsrsouza.kotlinbukkitapi.extensions.plugin.severe
import org.bukkit.plugin.Plugin
import java.io.File
import kotlin.script.experimental.api.ResultWithDiagnostics
import kotlin.script.experimental.api.resultOrNull

fun <R> ResultWithDiagnostics<R>.resultOrSeveral(plugin: Plugin, api: BukkriptAPI): R? {
    return resultOrNull() ?: run {
        for (diag in reports) {
            if(diag.exception != null) {
                diag.exception?.printStackTrace()
            } else {
                plugin.severe(diag.message)
            }
            diag.sourcePath?.also { plugin.severe(File(it).relativeTo(api.SCRIPT_DIR).path) }
        }
        null
    }
}

fun File.scriptName(api: BukkriptAPI) = relativeTo(api.SCRIPT_DIR).path.substringBeforeLast(".")