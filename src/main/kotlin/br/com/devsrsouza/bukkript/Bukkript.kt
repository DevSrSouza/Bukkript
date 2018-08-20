package br.com.devsrsouza.bukkript

import br.com.devsrsouza.bukkript.script.BukkriptScriptTemplate
import br.com.devsrsouza.bukkript.script.ScriptController
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.net.URL
import java.net.URLClassLoader

class Bukkript : JavaPlugin() {

    override fun onLoad() {
        INSTANCE = this
    }

    override fun onEnable() {
        val scriptsFolder = File(dataFolder, "scripts").apply { mkdirs() }

        val libsFolder = File(dataFolder, "libs").apply { mkdirs() }

        libsFolder.listFiles().filter { it.extension.equals("jar") }.forEach { file ->
            URLClassLoader::class.java.getDeclaredMethod("addURL", URL::class.java).apply {
                isAccessible = true
                invoke(Bukkript::class.java.classLoader, file.toURI().toURL())
            }
        }

        ScriptController.loadScripts(this, scriptsFolder)
    }

    companion object {
        private lateinit var INSTANCE: Bukkript
        fun <T : BukkriptScriptTemplate> getScript(fileName: String) = ScriptController.getScript<T>(fileName)
    }
}