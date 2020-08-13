package br.com.devsrsouza.bukkript.plugin

import br.com.devsrsouza.kotlinbukkitapi.dsl.command.command
import br.com.devsrsouza.kotlinbukkitapi.dsl.command.sendSubCommandsList
import br.com.devsrsouza.kotlinbukkitapi.extensions.text.*
import org.bukkit.ChatColor

fun BukkriptPlugin.registerCommands() = command("bukkript", "bkkts") {
    permission = PERMISSION_BASE

    executor {
        sendSubCommandsList()
    }

    command("list") {
        permission = PERMISSION_CMD_LIST
        description = "List all available scripts and there status."

        executor {
            // discovering new scripts
            scriptManager.discoveryAllScripts()

            sender.msg("$BUKKRIPT_PREFIX &aAll available scripts.".translateColor())

            val scripts = scriptManager.scripts.values.map {
                "&l➥ &b${it.scriptName} ${it.stateDisplayName()}".translateColor()
                    .showText("Click".color(ChatColor.AQUA))
                    .suggestCommand("/bukkript SUB-COMMAND ${it.scriptName}")
            }

            sender.msg(scripts.joinToText(textOf("\n")))

            sender.msg("&b---------------------------------------------".translateColor())
        }
    }
    command("load") {
        permission = PERMISSION_CMD_LOAD
        description = "Loads a script"
    }
    command("reload") {
        permission = PERMISSION_CMD_RELOAD
        description = "Reloads a script that was unloaded or configured to not always load."
    }
    command("unload") {
        permission = PERMISSION_CMD_UNLOAD
        description = "Unload a script"
    }
    command("recompile") {
        permission = PERMISSION_CMD_RECOMPILE
        description = "Recompile and load a script"
    }
    command("debug") {
        description = "Script Debug sub commands"

        executor {
            sendSubCommandsList()
        }

        command("log") {
            description = "Handles the log from the script"

            executor {
                sendSubCommandsList()
            }

            command("lock") {
                permission = PERMISSION_CMD_LOG_LOCK
                description = "Lock the script log into your chat."
            }
            command("setlevel") {
                permission = PERMISSION_CMD_LOG_SETLEVEL
                description = "Changes the log level from the current script"
            }
        }
        command("hotrecompile") {
            permission = PERMISSION_CMD_HOTRECOMPILE
            description = "Auto recompiles and load the script when the file got change."
        }
    }
}