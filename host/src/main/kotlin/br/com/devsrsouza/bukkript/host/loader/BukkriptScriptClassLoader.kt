package br.com.devsrsouza.bukkript.host.loader

import br.com.devsrsouza.bukkript.api.ScriptDescription
import br.com.devsrsouza.bukkript.host.BukkriptCompiledScript
import br.com.devsrsouza.bukkript.script.BukkriptScript
import java.net.URLClassLoader
import kotlin.reflect.KClass

class BukkriptScriptClassLoader(
    val loader: BukkriptScriptLoader,
    parent: ClassLoader,
    val bukkriptCompiledScript: BukkriptCompiledScript
) : URLClassLoader(emptyArray(), parent) {

    val description: ScriptDescription get() = bukkriptCompiledScript.description
    lateinit var scriptClass: KClass<BukkriptScript>

    override fun findClass(name: String?): Class<*> {
        return super.findClass(name)
    }
}