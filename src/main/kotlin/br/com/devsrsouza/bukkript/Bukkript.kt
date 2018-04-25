package br.com.devsrsouza.bukkript

import br.com.devsrsouza.bukkript.script.BukkriptScriptTemplate
import br.com.devsrsouza.bukkript.script.Depend
import br.com.devsrsouza.bukkript.script.ScriptController
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class Bukkript : JavaPlugin() {

    override fun onLoad() {
        INSTANCE = this
    }
    override fun onEnable() {
        val scriptsFolder = File(dataFolder, "scripts").apply { mkdirs() }

        ScriptController.loadScripts(this, scriptsFolder)
    }

    companion object {
        private lateinit var INSTANCE: Bukkript
        fun <T : BukkriptScriptTemplate> getScript(fileName: String) = ScriptController.getScript<T>(fileName)
    }
}