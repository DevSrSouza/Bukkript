package br.com.devsrsouza.bukkript.script.definition.configuration

import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import java.io.File
import java.net.URL
import java.net.URLClassLoader

private fun URL.toFileOrNull() = try {
    java.io.File(toURI().schemeSpecificPart)
} catch (e: java.net.URISyntaxException) {
    if (protocol != "file") {
        null
    } else java.io.File(file)
}

private fun ClassLoader.urlsOrEmpty(): Array<URL> {
    return (this as? URLClassLoader)?.urLs ?: emptyArray()
}

fun ClassLoader.classpathFiles(): List<File> {
    return urlsOrEmpty().mapNotNull {
        it.toFileOrNull()
    }
}

val Plugin.classLoader: ClassLoader
    get() = this.javaClass.classLoader

fun Plugin.classpath(): List<File> {
    return classLoader.classpathFiles()
}

fun classpathFromBukkit(): List<File> {
    return Plugin::class.java.classLoader.classpathFiles()
}

fun classpathFromPlugins(): List<File> = Bukkit.getServer().pluginManager.plugins
    .flatMap { it.classpath() }
