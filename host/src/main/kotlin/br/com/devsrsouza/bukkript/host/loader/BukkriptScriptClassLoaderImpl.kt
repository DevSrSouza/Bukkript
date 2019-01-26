package br.com.devsrsouza.bukkript.host.loader

import br.com.devsrsouza.bukkript.api.script.BukkriptCompiledScript
import br.com.devsrsouza.bukkript.api.script.loader.BukkriptScriptClassLoader
import br.com.devsrsouza.bukkript.api.script.loader.BukkriptScriptLoader

class BukkriptScriptClassLoaderImpl(
    loader: BukkriptScriptLoader,
    parent: ClassLoader,
    bukkriptCompiledScript: BukkriptCompiledScript
) : BukkriptScriptClassLoader(loader, parent, bukkriptCompiledScript) {

    override fun findClass(name: String?): Class<*> {
        return super.findClass(name)
    }
}