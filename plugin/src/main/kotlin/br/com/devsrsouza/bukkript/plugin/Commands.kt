package br.com.devsrsouza.bukkript.plugin

import br.com.devsrsouza.kotlinbukkitapi.dsl.command.command

fun BukkriptPlugin.registerCommands() = command("bukkript", "bkkts") {
    command("list") {

    }
    command("load") {

    }
    command("reload") {

    }
    command("unload") {

    }
    command("recompile") {

    }
    command("debug") {
        command("log") {
            command("lock") {

            }
            command("setlevel") {

            }
        }
        command("hotrecompile") {

        }
    }
}