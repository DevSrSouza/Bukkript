package br.com.devsrsouza.bukkript

import br.com.devsrsouza.bukkript.api.script.FailReason
import br.com.devsrsouza.bukkript.api.script.ScriptDisableResult
import br.com.devsrsouza.bukkript.host.compileScripts
import br.com.devsrsouza.kotlinbukkitapi.extensions.player.playSound
import br.com.devsrsouza.kotlinbukkitapi.extensions.text.msg
import br.com.devsrsouza.kotlinbukkitapi.extensions.text.plus
import org.bukkit.ChatColor
import org.bukkit.Sound
import org.bukkit.command.CommandSender

enum class ReloadScriptCause { SUCESS, HAVE_DEPENDENCIES, NOT_FOUND }

fun Bukkript.reloadScript(script: String, force: Boolean, sender: CommandSender, afterCompile: () -> Unit = {}): ReloadScriptCause {
    val result = LOADER.disableScript(script, force)
    when(result) {
        is ScriptDisableResult.Sucess -> {
            sender.msg(ChatColor.GREEN + "Starting reloading scripts")
            compileScripts(this@reloadScript, result.dependencies + result.script, sender, afterCompile)
            return ReloadScriptCause.SUCESS
        }
        is ScriptDisableResult.Failure -> {
            when(result.reason) {
                FailReason.HAVE_DEPENDENCIES -> return ReloadScriptCause.HAVE_DEPENDENCIES
                FailReason.NOT_FOUND -> return ReloadScriptCause.NOT_FOUND
            }
        }
    }
}