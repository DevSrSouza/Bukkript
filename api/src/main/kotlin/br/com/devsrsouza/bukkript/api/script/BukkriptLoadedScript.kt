package br.com.devsrsouza.bukkript.api.script

import br.com.devsrsouza.bukkript.api.BukkriptAPI
import br.com.devsrsouza.bukkript.api.script.AbstractScript
import br.com.devsrsouza.bukkript.api.script.loader.BukkriptScriptClassLoader
import kotlin.reflect.KClass

abstract class BukkriptLoadedScript(
    val plugin: BukkriptAPI,
    val scriptClass: KClass<AbstractScript>,
    val classLoader: BukkriptScriptClassLoader,
    val bukkriptCompiledScript: BukkriptCompiledScript) {

    abstract val instance: AbstractScript
    abstract val scriptFilePath: String
}