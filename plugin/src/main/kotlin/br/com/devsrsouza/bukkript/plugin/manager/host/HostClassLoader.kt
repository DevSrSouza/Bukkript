package br.com.devsrsouza.bukkript.plugin.manager.host

import java.net.URLClassLoader

/**
 * Child-first ClassLoader
 *
 * ClassLoader usage to prevent libraries version conflicts (mostly Kotlin Reflect).
 */
class HostClassLoader(
    parent: ClassLoader
) : URLClassLoader(emptyArray(), parent) {

    override fun loadClass(name: String, resolve: Boolean): Class<*> {
        val loadedClass = findLoadedClass(name)
            ?: runCatching {
                findClass(name)
            }.getOrElse {
                super.loadClass(name, resolve)
            }

        if(resolve) {
            resolveClass(loadedClass)
        }

        return loadedClass
    }
}