package br.com.devsrsouza.bukkript

import br.com.devsrsouza.bukkript.api.script.BukkriptLoadedScript
import br.com.devsrsouza.kotlinbukkitapi.dsl.command.Executor
import br.com.devsrsouza.kotlinbukkitapi.dsl.command.arguments.string
import br.com.devsrsouza.kotlinbukkitapi.dsl.command.exception
import br.com.devsrsouza.kotlinbukkitapi.extensions.text.color
import net.md_5.bungee.api.chat.BaseComponent
import org.bukkit.ChatColor

val MISSING_SCRIPT_PARAMETER = "Missing a script argument.".color(ChatColor.RED)
val SCRIPT_NOT_FOUND = "The script passed is not found.".color(ChatColor.RED)

fun Executor<*>.script(
    bukkript: Bukkript,
    index: Int,
    argMissing: BaseComponent = MISSING_SCRIPT_PARAMETER,
    scriptNoFound: BaseComponent = SCRIPT_NOT_FOUND
): BukkriptLoadedScript = bukkript.LOADER.scripts[string(index, argMissing)] ?: exception(scriptNoFound)