package br.com.devsrsouza.bukkript.host

import br.com.devsrsouza.bukkript.api.BukkriptAPI
import br.com.devsrsouza.bukkript.api.script.AbstractScript
import br.com.devsrsouza.bukkript.api.script.BukkriptLoadedScript
import br.com.devsrsouza.bukkript.host.loader.BukkriptScriptClassLoaderImpl
import kotlin.reflect.KClass

class BukkriptLoadedScriptImpl(
    plugin: BukkriptAPI,
    clazz: KClass<AbstractScript>,
    classLoader: BukkriptScriptClassLoaderImpl,
    bukkriptCompiledScript: BukkriptCompiledScriptImpl
) : BukkriptLoadedScript(plugin, clazz, classLoader, bukkriptCompiledScript) {

    override val instance by lazy { clazz.constructors.first().call(plugin) }
    override val scriptFilePath get() = bukkriptCompiledScript.scriptFile.scriptName(plugin)
}