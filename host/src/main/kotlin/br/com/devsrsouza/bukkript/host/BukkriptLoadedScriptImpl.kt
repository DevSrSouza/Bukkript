package br.com.devsrsouza.bukkript.host

import br.com.devsrsouza.bukkript.api.BukkriptAPI
import br.com.devsrsouza.bukkript.api.script.AbstractScript
import br.com.devsrsouza.bukkript.api.script.BukkriptLoadedScript
import br.com.devsrsouza.bukkript.api.script.scriptDataFolder
import br.com.devsrsouza.bukkript.api.script.scriptName
import br.com.devsrsouza.bukkript.host.loader.BukkriptScriptClassLoaderImpl
import kotlin.reflect.KClass

class BukkriptLoadedScriptImpl(
    plugin: BukkriptAPI,
    scriptClass: KClass<AbstractScript>,
    classLoader: BukkriptScriptClassLoaderImpl,
    bukkriptCompiledScript: BukkriptCompiledScriptImpl
) : BukkriptLoadedScript(plugin, scriptClass, classLoader, bukkriptCompiledScript) {

    override val instance by lazy {
        scriptClass.constructors.first()
            .call(plugin, bukkriptCompiledScript.scriptFile.scriptDataFolder(plugin))
    }
    override val scriptFilePath get() = bukkriptCompiledScript.scriptFile.scriptName(plugin)
}