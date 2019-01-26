package br.com.devsrsouza.bukkript.host.loader

import br.com.devsrsouza.bukkript.api.BukkriptAPI
import br.com.devsrsouza.bukkript.api.script.BukkriptLoadedScript
import br.com.devsrsouza.bukkript.api.script.loader.BukkriptScriptLoader
import br.com.devsrsouza.kotlinbukkitapi.dsl.event.unregisterAll
import br.com.devsrsouza.kotlinbukkitapi.extensions.plugin.info
import br.com.devsrsouza.kotlinbukkitapi.dsl.command.unregister
import org.bukkit.plugin.Plugin

class BukkriptScriptLoaderImpl(plugin: BukkriptAPI) : BukkriptScriptLoader(plugin) {

    override fun loadScript(scriptLoaded: BukkriptLoadedScript) {
        scripts.put(scriptLoaded.scriptFilePath, scriptLoaded)
        scriptLoaded.instance // LOADING SCRIPT
    }

    override fun disableScript(script: String): Boolean {
        val script = scripts.remove(script)

        if(script != null) {
            val bukkriptScript = script.instance

            val controller = bukkriptScript.run { getController() }

            bukkriptScript.unregisterAll()
            controller.events.forEach { it.unregisterAll() }
            controller.commands.forEach { it.unregister() }
            controller.tasks.forEach { it.cancel() }

            (plugin as Plugin).info("Script ${script.scriptFilePath} disabled")
            return true
        } else return false
    }
}