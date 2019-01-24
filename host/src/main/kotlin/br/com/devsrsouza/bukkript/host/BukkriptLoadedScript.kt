package br.com.devsrsouza.bukkript.host

import br.com.devsrsouza.bukkript.Bukkript
import br.com.devsrsouza.bukkript.host.loader.BukkriptScriptClassLoader
import br.com.devsrsouza.bukkript.script.BukkriptScript
import kotlin.reflect.KClass

class BukkriptLoadedScript(
    val plugin: Bukkript,
    val clazz: KClass<BukkriptScript>,
    val classLoader: BukkriptScriptClassLoader,
    val bukkriptCompiledScript: BukkriptCompiledScript) {

    val instance by lazy { clazz.constructors.first().call(plugin) }
    val scriptFilePath get() = bukkriptCompiledScript.scriptFile.scriptName(plugin)
}