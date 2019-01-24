package br.com.devsrsouza.bukkript.script

import br.com.devsrsouza.bukkript.Bukkript
import br.com.devsrsouza.bukkript.host.loader.BukkriptScriptLoader
import br.com.devsrsouza.kotlinbukkitapi.dsl.command.KCommand
import org.bukkit.event.Listener
import org.bukkit.scheduler.BukkitTask
import kotlin.script.experimental.annotations.KotlinScript

typealias DisableBlock = () -> Unit

const val scriptExtension = "bkts"

@KotlinScript("Bukkript script", scriptExtension, BukkriptScriptConfiguration::class)
abstract class BukkriptScript(val plugin: Bukkript) : Listener {
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