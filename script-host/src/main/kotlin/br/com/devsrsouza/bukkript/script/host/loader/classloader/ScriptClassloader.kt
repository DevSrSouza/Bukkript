package br.com.devsrsouza.bukkript.script.host.loader.classloader

import java.net.URLClassLoader

typealias ClassProvider = (name: String) -> Class<*>?

class ScriptClassloader(
    val classProvider: ClassProvider,
    parent: ClassLoader
) : URLClassLoader(emptyArray(), parent) {

    override fun findClass(name: String): Class<*>? {
        return findClass(name, true)
    }

    fun findClass(name: String, checkGlobal: Boolean): Class<*>? {
        var clazz: Class<*>? = null

        if(checkGlobal) {
            clazz = classProvider(name)
        }

        if(clazz == null) {
            clazz = runCatching { super.findClass(name) }.getOrNull()
        }

        return clazz
    }

}