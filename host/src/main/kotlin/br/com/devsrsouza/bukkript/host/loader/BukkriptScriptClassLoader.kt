package br.com.devsrsouza.bukkript.host.loader

import br.com.devsrsouza.bukkript.api.ScriptDescription
import java.net.URLClassLoader

class BukkriptScriptClassLoader(
    val loader: BukkriptScriptLoader,
    parent: ClassLoader,
    val description: ScriptDescription
) : URLClassLoader(emptyArray(), parent) {

    override fun findClass(name: String?): Class<*> {
        return super.findClass(name)
    }
}