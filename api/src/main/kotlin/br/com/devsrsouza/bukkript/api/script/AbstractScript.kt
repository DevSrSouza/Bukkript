package br.com.devsrsouza.bukkript.api.script

import br.com.devsrsouza.bukkript.api.BukkriptAPI
import br.com.devsrsouza.bukkript.api.script.loader.BukkriptScriptLoader
import br.com.devsrsouza.kotlinbukkitapi.dsl.command.KCommand
import org.bukkit.event.Listener
import org.bukkit.scheduler.BukkitTask

typealias DisableBlock = () -> Unit

abstract class AbstractScript(val api: BukkriptAPI) : Listener {

    private val controller = BukkriptScriptController()

    fun onDisable(block: DisableBlock) {
        controller.disable = block
    }

    fun BukkriptScriptLoader.getController() = controller
}


class BukkriptScriptController {
    val events = mutableListOf<Listener>()
    val commands = mutableListOf<KCommand>()
    val tasks = mutableListOf<BukkitTask>()

    var disable: DisableBlock? = null
}