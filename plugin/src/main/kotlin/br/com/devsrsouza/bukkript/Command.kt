package br.com.devsrsouza.bukkript

import br.com.devsrsouza.bukkript.api.script.FailReason
import br.com.devsrsouza.bukkript.api.script.ScriptDisableResult
import br.com.devsrsouza.kotlinbukkitapi.dsl.command.arguments.string
import br.com.devsrsouza.kotlinbukkitapi.dsl.command.command
import br.com.devsrsouza.kotlinbukkitapi.dsl.command.exception
import br.com.devsrsouza.kotlinbukkitapi.extensions.player.playSound
import br.com.devsrsouza.kotlinbukkitapi.extensions.text.color
import br.com.devsrsouza.kotlinbukkitapi.extensions.text.msg
import br.com.devsrsouza.kotlinbukkitapi.extensions.text.plus
import br.com.devsrsouza.kotlinbukkitapi.extensions.text.textOf
import org.bukkit.ChatColor
import org.bukkit.Sound
import org.bukkit.entity.Player

fun Bukkript.bukkriptCommands() {
    command("bukkript") {
        permission = ""
        permissionMessage = ""
        command("script") {
            permission += ".$name"
            command("load") {
                permission += ".$name"
            }
            command("reload") {
                permission += ".$name"
                executor {
                    val script = string(0) // TODO configurable
                    val result = reloadScript(script, false, sender) {
                        sender.msg(ChatColor.GREEN + "Reload finish")
                        (sender as? Player)?.playSound(Sound.LEVEL_UP, 1f, 1f)
                    }
                    when(result) {
                        ReloadScriptCause.SUCESS -> {}
                        ReloadScriptCause.NOT_FOUND -> exception("Script not found".color(ChatColor.RED))
                        ReloadScriptCause.HAVE_DEPENDENCIES -> {
                            (sender as? Player)?.also {
                                forceReloadMenu.playerData.put(it, mutableMapOf("script" to script))
                                forceReloadMenu.openToPlayer(it)
                            } ?: exception("To reload this script need to force, try: /bukkript reload [script] force".color(ChatColor.YELLOW))
                        }
                    }
                }
            }
            command("list") {
                permission += ".$name"
            }
            command("unload") {
                permission += ".$name"
            }
            command("info") {
                permission += ".$name"
            }
        }
    }
}