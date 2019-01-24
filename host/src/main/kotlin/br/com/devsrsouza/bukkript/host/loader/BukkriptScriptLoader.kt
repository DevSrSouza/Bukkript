package br.com.devsrsouza.bukkript.host.loader

import br.com.devsrsouza.bukkript.Bukkript
import br.com.devsrsouza.bukkript.host.BukkriptLoadedScript
import br.com.devsrsouza.kotlinbukkitapi.dsl.command.unregister
import br.com.devsrsouza.kotlinbukkitapi.dsl.event.unregisterAll
import br.com.devsrsouza.kotlinbukkitapi.extensions.plugin.info

class BukkriptScriptLoader(val plugin: Bukkript) {

    val scripts = mutableMapOf<String, BukkriptLoadedScript>()

    fun loadScript(scriptLoaded: BukkriptLoadedScript) {
        scripts.put(scriptLoaded.scriptFilePath, scriptLoaded)
        scriptLoaded.instance // LOADING SCRIPT
    }

    fun disableScript(script: String): Boolean {
        val script = scripts.remove(script)

        if(script != null) {
            val bukkriptScript = script.instance

            val controller = bukkriptScript.run { getController() }

            bukkriptScript.unregisterAll()
            controller.events.forEach { it.unregisterAll() }
            controller.commands.forEach { it.unregister() }
            controller.tasks.forEach { it.cancel() }

            plugin.info("Script ${script.scriptFilePath} disabled")
            return true
        } else return false
    }
}