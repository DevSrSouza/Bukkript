package br.com.devsrsouza.bukkript.host

import br.com.devsrsouza.bukkript.api.BukkriptAPI
import br.com.devsrsouza.bukkript.api.script.scriptName
import br.com.devsrsouza.kotlinbukkitapi.extensions.plugin.severe
import br.com.devsrsouza.kotlinbukkitapi.extensions.text.msg
import br.com.devsrsouza.kotlinbukkitapi.extensions.text.plus
import org.bukkit.ChatColor.*
import org.bukkit.command.CommandSender
import org.bukkit.plugin.Plugin
import java.io.File
import kotlin.script.experimental.api.ResultWithDiagnostics
import kotlin.script.experimental.api.resultOrNull

fun <R> ResultWithDiagnostics<R>.resultOrSeveral(plugin: Plugin, api: BukkriptAPI, sender: CommandSender? = null): R? {
    return resultOrNull() ?: run {
        for (diag in reports) {
            val file = diag.sourcePath?.let { File(it) }?.scriptName(api)
            val line = diag.location?.start?.line
            val column = diag.location?.start?.col
            val sourceLocation = if(file != null && line != null)
                "$file(line: $line ${if(column != null) ", column: $column" else ""})"
            else ""
            val message = YELLOW + diag.severity.toString() + ": $RESET$sourceLocation" + diag.message
            plugin.severe(message)
            sender?.msg(message)
            if(diag.exception != null) {
                diag.exception?.printStackTrace()
            }
        }
        null
    }
}