package br.com.devsrsouza.bukkript

import br.com.devsrsouza.bukkript.api.DependecyImport
import br.com.devsrsouza.bukkript.api.PluginDependencyImport
import br.com.devsrsouza.bukkript.host.loader.BukkriptScriptLoader
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class Bukkript : JavaPlugin() {

    companion object {
        private lateinit var INSTANCE: Bukkript
        fun importsInstall(plugin: Plugin, imports: List<String>, module: String = "") {
            if(imports.isEmpty()) error("Plugin imports can't be empty")

            INSTANCE.addImport(PluginDependencyImport(plugin, imports, module))
        }
    }

    val SCRIPT_DIR by lazy { File(dataFolder, "scripts/").apply { mkdirs() } }
    val CACHE_DIR by lazy { File(dataFolder, ".cache/").apply { mkdirs() } }

    val LOADER by lazy { BukkriptScriptLoader(this) } // TODO

    private val imports = mutableListOf<DependecyImport>()

    override fun onLoad() { INSTANCE = this }

    override fun onEnable() {
        // TODO add KotlinBukkitAPI imports
    }

    fun addImport(imports: DependecyImport) {
        if(this.imports.find { it.name.equals(imports.name, true) } != null)
            error("This plugin or module was a import registred.")

        this.imports.add(imports)
    }
}