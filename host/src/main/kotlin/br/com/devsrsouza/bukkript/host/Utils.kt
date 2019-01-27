package br.com.devsrsouza.bukkript.host

import br.com.devsrsouza.bukkript.api.BukkriptAPI
import br.com.devsrsouza.bukkript.api.script.scriptName
import br.com.devsrsouza.kotlinbukkitapi.extensions.plugin.severe
import br.com.devsrsouza.kotlinbukkitapi.extensions.text.plus
import org.bukkit.ChatColor.*
import org.bukkit.plugin.Plugin
import java.io.File
import kotlin.script.experimental.api.ResultWithDiagnostics
import kotlin.script.experimental.api.resultOrNull

fun <R> ResultWithDiagnostics<R>.resultOrSeveral(plugin: Plugin, api: BukkriptAPI): R? {
    return resultOrNull() ?: run {
        for (diag in reports) {
            val file = diag.sourcePath?.let { File(it) }?.scriptName(api) ?: ""
            val line = diag.location?.start?.line ?: ""
            plugin.severe(YELLOW + diag.severity.toString() + ": $RESET$file($line)" + diag.message)
            if(diag.exception != null) {
                diag.exception?.printStackTrace()
            }
        }
        null
    }
}