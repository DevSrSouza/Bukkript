package br.com.devsrsouza.bukkript.script.definition.configuration

import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import java.io.File
import java.net.URL
import java.net.URLClassLoader
import kotlin.script.experimental.jvm.util.classpathFromClass

private fun URL.toFileOrNull() = try {
    java.io.File(toURI().schemeSpecificPart)
} catch (e: java.net.URISyntaxException) {
    if (protocol != "file") null
    else java.io.File(file)
}

private fun ClassLoader.urlsOrEmpty(): Array<URL> {
    return (javaClass.classLoader as? URLClassLoader)?.urLs ?: emptyArray()
}

fun ClassLoader.classpathFiles(): List<File> {
    return urlsOrEmpty().mapNotNull {
        it.toFileOrNull()
    }
}

fun Plugin.classpath(): List<File> {
    return javaClass.classLoader.classpathFiles()
}

fun classpathFromBukkit(): List<File> {
    return Plugin::class.java.classLoader.classpathFiles()
}

fun classpathFromPlugins(): List<File> {
    return Bukkit.getServer().pluginManager.plugins.flatMap {
        classpathFromClass(it::class) ?: emptyList()
    }
}