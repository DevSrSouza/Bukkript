package br.com.devsrsouza.bukkript.host.loader

import br.com.devsrsouza.bukkript.api.script.BukkriptCompiledScript
import br.com.devsrsouza.bukkript.api.script.loader.BukkriptScriptClassLoader
import br.com.devsrsouza.bukkript.api.script.loader.BukkriptScriptLoader
import br.com.devsrsouza.kotlinbukkitapi.utils.whenErrorNull
import org.jetbrains.kotlin.codegen.BytesUrlUtils
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.net.URL
import java.util.*

class BukkriptScriptClassLoaderImpl(
    loader: BukkriptScriptLoader,
    parent: ClassLoader,
    bukkriptCompiledScript: BukkriptCompiledScript
) : BukkriptScriptClassLoader(loader, parent, bukkriptCompiledScript) {

    lateinit var script: Map<String, ByteArray>
    val classes: MutableMap<String, Class<*>> = mutableMapOf()

    override fun findClass(name: String): Class<*>? {
        return findClass(name, true)
    }

    override fun findClass(name: String, checkGlobal: Boolean): Class<*>? {
        classes.get(name)?.let { return@findClass it }

        var clazz: Class<*>? = null

        val classPathName = name.replace('.', '/') + ".class"
        val classBytes = script[classPathName]

        if(classBytes != null) {
            clazz = defineClass(name, classBytes, 0, classBytes.size)
        }

        if(checkGlobal && clazz == null) {
            clazz = loader.getClassByName(name)
        }

        if(clazz == null) {
            clazz = whenErrorNull { super.findClass(name) }
        }

        if(clazz != null) {
            classes.put(name, clazz)
        }

        return clazz
    }

    override fun getResourceAsStream(name: String): InputStream? =
        script[name]?.let { ByteArrayInputStream(it) } ?: super.getResourceAsStream(name)

    override fun findResources(name: String?): Enumeration<URL>? {
        val fromParent = super.findResources(name)

        val url = script[name]?.let { BytesUrlUtils.createBytesUrl(it) } ?: return fromParent

        return Collections.enumeration(listOf(url) + fromParent.asSequence())
    }

    override fun findResource(name: String?): URL? =
        script[name]?.let { BytesUrlUtils.createBytesUrl(it) } ?: super.findResource(name)
}