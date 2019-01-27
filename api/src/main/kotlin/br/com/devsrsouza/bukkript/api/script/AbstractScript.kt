package br.com.devsrsouza.bukkript.api.script

import br.com.devsrsouza.bukkript.api.BukkriptAPI
import br.com.devsrsouza.bukkript.api.script.controllers.*
import br.com.devsrsouza.bukkript.api.script.loader.BukkriptScriptLoader
import br.com.devsrsouza.kotlinbukkitapi.dsl.command.CommandMaker
import br.com.devsrsouza.kotlinbukkitapi.dsl.command.ExecutorBlock
import br.com.devsrsouza.kotlinbukkitapi.dsl.command.KCommand
import br.com.devsrsouza.kotlinbukkitapi.dsl.command.simpleCommand
import br.com.devsrsouza.kotlinbukkitapi.dsl.command.command
import br.com.devsrsouza.kotlinbukkitapi.dsl.event.event
import br.com.devsrsouza.kotlinbukkitapi.dsl.event.registerEvents
import br.com.devsrsouza.kotlinbukkitapi.dsl.scheduler.task
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitRunnable
import java.io.File

typealias DisableBlock = () -> Unit

const val scriptExtension = "bkts"

val File.isScriptFile get() = extension.equals(scriptExtension, true)
fun File.scriptName(api: BukkriptAPI) = relativeTo(api.SCRIPT_DIR).path.substringBeforeLast(".")

abstract class AbstractScript(val api: BukkriptAPI) : Listener {
    val controllers = mutableSetOf<ScriptController>()

    init {
        controllers.add(EventScriptController().apply { events.add(this@AbstractScript) })
    }

    fun onDisable(block: DisableBlock) {
        controllers.add(SimpleScriptController().apply { disable = block })
    }

    fun BukkriptScriptLoader.getControllers() = controllers

    // implementations

    // command
    fun simpleCommand(
        name: String, vararg aliases: String = arrayOf(),
        description: String = "",
        block: ExecutorBlock
    ) = simpleCommand(name, *aliases, description = description, plugin = api as Plugin, block = block).apply {
        val c = controllerByType() ?: CommandScriptController().also {
            controllers.add(it)
        }
        c.commands.add(this)
    }

    fun command(
        name: String,
        vararg aliases: String = arrayOf(),
        block: CommandMaker
    ) = command(name, *aliases, plugin = api as Plugin, block = block)

    fun KCommand.unregister() {
        controllerByType<CommandScriptController>()?.commands?.remove(this)
    }

    // events
    inline fun <reified T : Event> event(
        priority: EventPriority = EventPriority.NORMAL,
        ignoreCancelled: Boolean = true,
        crossinline block: T.() -> Unit
    ) {
        event(priority, ignoreCancelled, api as Plugin, block)
    }

    fun Listener.unregisterAll() {
        controllerByType<EventScriptController>()?.events?.remove(this)
        HandlerList.unregisterAll(this)
    }

    fun Listener.registerEvents() {
        registerEvents(api as Plugin)
        controllerByType<EventScriptController>()?.events?.add(this)
    }

    // tasks
    inline fun task(
        delayToRun: Long = 0,
        repeatDelay: Long = -1,
        crossinline runnable: BukkitRunnable.() -> Unit
    ) = task(delayToRun, repeatDelay, false, runnable)

    inline fun taskAsync(
        delayToRun: Long = 0,
        repeatDelay: Long = -1,
        crossinline runnable: BukkitRunnable.() -> Unit
    ) = task(delayToRun, repeatDelay, true, runnable)

    inline fun task(
        delayToRun: Long,
        repeatDelay: Long = -1,
        async: Boolean,
        crossinline runnable: BukkitRunnable.() -> Unit
    ) = task(delayToRun, repeatDelay, async, api as Plugin, runnable).also {
        val c = controllerByType() ?: TaskScriptController().also {
            controllers.add(it)
        }
        c.tasks.add(it)
    }

}