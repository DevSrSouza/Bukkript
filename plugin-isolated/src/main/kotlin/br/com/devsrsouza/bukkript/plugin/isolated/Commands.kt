package br.com.devsrsouza.bukkript.plugin.isolated

import br.com.devsrsouza.bukkript.plugin.*
import br.com.devsrsouza.bukkript.plugin.isolated.manager.LoggingManager
import br.com.devsrsouza.bukkript.plugin.isolated.manager.ScriptManager
import br.com.devsrsouza.bukkript.plugin.isolated.manager.ScriptManagerImpl.Companion.MINIMUM_MODIFY_TIME_TO_RECOMPILE_SECONDS
import br.com.devsrsouza.bukkript.script.definition.api.LogLevel
import br.com.devsrsouza.kotlinbukkitapi.dsl.command.*
import br.com.devsrsouza.kotlinbukkitapi.dsl.command.arguments.enum
import br.com.devsrsouza.kotlinbukkitapi.dsl.command.arguments.string
import br.com.devsrsouza.kotlinbukkitapi.extensions.text.*
import org.bukkit.ChatColor

fun BukkriptPlugin.registerCommands(
    scriptManager: ScriptManager,
    loggingManager: LoggingManager
) = command("bukkript", "bkkts") {
    permission = PERMISSION_BASE

    // utils
    fun Executor<*>.checkScriptLoaded(scriptName: String) {
        if(!scriptManager.isLoaded(scriptName))
            fail("$BUKKRIPT_PREFIX &cThe specifed script file is not loaded or not exist.".translateColor())
    }

    executor {
        sendSubCommandsList()
    }

    errorHandler {
        when(it) {
            is BukkriptException -> sender.msg("$BUKKRIPT_PREFIX &4ERROR &c${it.message}".translateColor())
            else -> defaultErrorHandler(it)
        }
    }

    command("list") {
        permission = PERMISSION_CMD_LIST
        description = "List all available scripts and there status."

        executor {
            // discovering new scripts
            scriptManager.discoveryAllScripts()

            sender.msg("$BUKKRIPT_PREFIX &aAll available scripts.".translateColor())

            val scripts = scriptManager.scripts.values.map {
                val msg = "&l➥ &b${it.scriptName} ${it.stateDisplayName()}".translateColor()
                    .showText("Click".color(ChatColor.AQUA))
                    .suggestCommand("/bukkript SUB-COMMAND ${it.scriptName}")

                if(scriptManager.isHotRecompileEnable(it.scriptName))
                    msg + "*".color(ChatColor.DARK_RED).bold()
                        .showText("Hot recompilation enable".color(ChatColor.RED))
                else
                    msg
            }

            sender.msg(scripts.joinToText(textOf("\n")))

            sender.msg("&b---------------------------------------------".translateColor())
        }
    }
    command("load") {
        permission = PERMISSION_CMD_LOAD
        description = "Loads a script"

        executor {
            val script = scriptName(0)

            scriptManager.load(script)

            sender.msg("$BUKKRIPT_PREFIX &eStart reload the script &a$script&e.".translateColor())
            // TODO: use a Flow to listen for the completion or the fail of the load.
        }
    }
    command("reload") {
        permission = PERMISSION_CMD_RELOAD
        description = "Reloads a script that was unloaded or configured to not always load."

        executor {
            val script = scriptName(0)

            scriptManager.reload(script)

            sender.msg("$BUKKRIPT_PREFIX &eStart reload the script &a$script&e.".translateColor())
            // TODO: use a Flow to listen for the completion or the fail of the reload.
        }
    }
    command("unload") {
        permission = PERMISSION_CMD_UNLOAD
        description = "Unload a script"

        executor {
            val script = scriptName(0)

            scriptManager.unload(script)

            sender.msg("$BUKKRIPT_PREFIX &eStart unload the script &a$script&e.".translateColor())
            // TODO: use a Flow to listen for the completion or the fail of the unload.
        }
    }
    command("recompile") {
        permission = PERMISSION_CMD_RECOMPILE
        description = "Recompile and load a script"

        executor {
            val script = scriptName(0)

            scriptManager.recompile(script)

            sender.msg("$BUKKRIPT_PREFIX &eStart recompiling the script &a$script, &ethis can take some seconds.".translateColor())
            // TODO: use a Flow to listen for the completion or the fail of the recompilation.
        }
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

                executorPlayer {
                    val script = scriptName(0)

                    checkScriptLoaded(script)

                    if(loggingManager.isListingLog(sender)) {
                        sender.msg("$BUKKRIPT_PREFIX &eDisable the script &a$script &elog into your chat.".translateColor())
                        loggingManager.unlistenLog(sender, script)
                    } else {
                        sender.msg("$BUKKRIPT_PREFIX &eEnable the script &a$script &elog into your chat.".translateColor())
                        loggingManager.listenLog(sender, script)
                    }
                }
            }
            command("setlevel") {
                permission = PERMISSION_CMD_LOG_SETLEVEL
                description = "Changes the log level from the current script"

                executor {
                    val script = scriptName(0)
                    val logLevel = enum<LogLevel>(
                        1,
                        argMissing = textOf("$BUKKRIPT_PREFIX &cPlease, inform the Log Level &a${LogLevel.values().toList()}&c.".translateColor()),
                        notFound = textOf("$BUKKRIPT_PREFIX &cThe Log Level specified was not found, try one of this &a${LogLevel.values().toList()}&c.".translateColor())
                    )

                    checkScriptLoaded(script)

                    scriptManager.updateLogLevel(script, logLevel)

                    sender.msg("$BUKKRIPT_PREFIX &eYou update the script &a$script &elog level to &a$logLevel&e.".translateColor())
                }
            }
        }
        command("hotrecompile") {
            permission = PERMISSION_CMD_HOTRECOMPILE
            description = "Auto recompiles and load the script when the file got change."

            executor {
                val script = scriptName(0)

                scriptManager.hotRecompile(script)
                // TODO: also disable the hot recompilation

                sender.msg("$BUKKRIPT_PREFIX &eYou enable the hot recompile for &a$script&e, when your script got change, it will recompile in $MINIMUM_MODIFY_TIME_TO_RECOMPILE_SECONDS second.".translateColor())
            }
        }
    }
}

private fun Executor<*>.scriptName(index: Int): String = string(
    index,
    textOf("$BUKKRIPT_PREFIX &cPlease, inform the script name (case sensitive).".translateColor())
)