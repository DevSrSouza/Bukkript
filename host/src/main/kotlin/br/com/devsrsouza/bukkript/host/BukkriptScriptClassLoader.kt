package br.com.devsrsouza.bukkript.host

import br.com.devsrsouza.bukkript.script.BukkriptScript
import java.net.URLClassLoader
import kotlin.script.experimental.api.CompiledScript

class BukkriptScriptClassLoader(script: CompiledScript<BukkriptScript>) : URLClassLoader(emptyArray()) {
    init {
    }

    override fun findClass(name: String?): Class<*> {
        return super.findClass(name)
    }
}