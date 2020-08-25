package br.com.devsrsouza.bukkript.plugin.manager.host

import br.com.devsrsouza.bukkript.plugin.BukkriptPlugin
import java.io.File
import java.net.URLClassLoader

/**
 * Child-first ClassLoader
 *
 * ClassLoader usage to prevent libraries version conflicts (mostly Kotlin Reflect).
 */
class HostClassLoader(
    val bukkriptPlugin: BukkriptPlugin,
    parent: ClassLoader
) : URLClassLoader(
    emptyArray(),
    parent
) {

    private val isolatedLoaderClassName = "br.com.devsrsouza.bukkript.plugin.isolated.IsolationLoader"
    private val isolatedLoadMethodName = "load"

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

    fun initialize() {
        // loading
        val isolatedLoaderClass = Class.forName(isolatedLoaderClassName)
        val loadMethod = isolatedLoaderClass.getMethod(isolatedLoadMethodName, BukkriptPlugin::class.java)

        loadMethod.invoke(null, bukkriptPlugin)
    }
}