package br.com.devsrsouza.bukkript.script.definition.classpath

import br.com.devsrsouza.bukkript.script.definition.compiler.BukkriptScriptCompilationConfiguration
import br.com.devsrsouza.bukkript.script.definition.dependencies.ignoredPluginDependencies
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import java.io.File
import java.net.URL
import java.net.URLClassLoader
import kotlin.script.experimental.jvm.util.classpathFromClass
import kotlin.script.experimental.jvm.util.scriptCompilationClasspathFromContext

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

fun classpathFromPluginsByExcluding(): List<File> {
    return classpathFromPlugins()
        .filter(::isDependencyIgnored)
}

fun wholeClassloaderByExcluding(): List<File> {
    return scriptCompilationClasspathFromContext(
        classLoader = BukkriptScriptCompilationConfiguration::class.java.classLoader,
        wholeClasspath = true
    ).filter(::isDependencyIgnored)
}

private fun isDependencyIgnored(file: File): Boolean = ignoredPluginDependencies.any { file.name.contains(it) }
