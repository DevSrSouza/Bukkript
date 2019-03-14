package br.com.devsrsouza.bukkript.host.loader

import br.com.devsrsouza.bukkript.api.BukkriptAPI
import br.com.devsrsouza.bukkript.api.ScriptDescription
import br.com.devsrsouza.bukkript.api.script.*
import br.com.devsrsouza.bukkript.api.script.loader.BukkriptScriptLoader
import br.com.devsrsouza.kotlinbukkitapi.dsl.scheduler.scheduler
import br.com.devsrsouza.kotlinbukkitapi.dsl.scheduler.task
import br.com.devsrsouza.kotlinbukkitapi.extensions.plugin.info
import org.bukkit.plugin.Plugin
import java.io.File

class BukkriptScriptLoaderImpl(api: BukkriptAPI) : BukkriptScriptLoader(api) {

    override fun getScript(file: File): AbstractScript? {
        return getScript(file.scriptName(api))
    }

    override fun getScript(name: String): AbstractScript? {
        return scripts[name]?.instance
    }

    override fun getScriptDescription(name: String): ScriptDescription? {
        return scripts[name]?.bukkriptCompiledScript?.description
    }

    override fun loadScript(scriptLoaded: BukkriptLoadedScript) {
        scripts.put(scriptLoaded.scriptFilePath, scriptLoaded)
        scheduler { scriptLoaded.instance }.runTask(api as Plugin) // LOADING SCRIPT
    }

    override fun disableScript(script: File, force: Boolean): ScriptDisableResult {
        return disableScript(script.scriptName(api), force)
    }

    override fun disableScript(scriptName: String, force: Boolean): ScriptDisableResult {
        val script = scripts.remove(scriptName)

        val dependencies = mutableListOf<File>()

        if(script != null) {
            val depend = scripts.entries.filter { it.value.bukkriptCompiledScript.description.depend.any { it == "$scriptName.$scriptExtension" } }
            if(depend.isNotEmpty()) {
                if (force) {
                    dependencies += depend.map { it.value.bukkriptCompiledScript.scriptFile }
                    dependencies += depend.flatMap { disableScript(it.key, force).dependencies }
                } else return ScriptDisableResult.Failure(
                    script.bukkriptCompiledScript.scriptFile,
                    depend.map { it.value.bukkriptCompiledScript.scriptFile },
                    FailReason.HAVE_DEPENDENCIES
                )
            }

            val bukkriptScript = script.instance

            val controllers = bukkriptScript.run { getControllers() }
            controllers.forEach { it.disable() }

            (api as Plugin).info("Script ${script.scriptFilePath} disabled")
            return ScriptDisableResult.Sucess(script.bukkriptCompiledScript.scriptFile, dependencies)
        } else return ScriptDisableResult.Failure(
            File(api.SCRIPT_DIR, scriptName),
            emptyList(),
            FailReason.NOT_FOUND
        )
    }

    override fun getClassByName(name: String): Class<*>? {

        var clazz: Class<*>? = null

        for (current in scripts.keys) {
            val loader = scripts.get(current)!!

            clazz = loader.classLoader.findClass(name, false)

            if (clazz != null) {
                return clazz
            }
        }

        return null
    }

}