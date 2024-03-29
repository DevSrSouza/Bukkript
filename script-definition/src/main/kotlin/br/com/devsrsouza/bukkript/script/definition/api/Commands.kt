package br.com.devsrsouza.bukkript.script.definition.api

import br.com.devsrsouza.bukkript.script.definition.BukkriptScript
import br.com.devsrsouza.kotlinbukkitapi.command.CommandBuilderBlock
import br.com.devsrsouza.kotlinbukkitapi.command.CommandDSL
import br.com.devsrsouza.kotlinbukkitapi.command.ExecutorBlock
import br.com.devsrsouza.kotlinbukkitapi.command.command
import br.com.devsrsouza.kotlinbukkitapi.command.simpleCommand
import br.com.devsrsouza.kotlinbukkitapi.extensions.unregister
import org.bukkit.command.CommandSender

fun BukkriptScript.simpleCommand(
    name: String,
    vararg aliases: String = arrayOf(),
    description: String = "",
    block: ExecutorBlock<CommandSender>,
) = plugin.simpleCommand(name, *aliases, description = description, block = block).also {
    unregisterOnDisable(it)
}

fun BukkriptScript.command(
    name: String,
    vararg aliases: String = arrayOf(),
    block: CommandBuilderBlock,
) = plugin.command(name, *aliases, block = block).also {
    unregisterOnDisable(it)
}

private fun BukkriptScript.unregisterOnDisable(it: CommandDSL) {
    onDisable {
        it.job.cancel()
        it.unregister()
    }
}
