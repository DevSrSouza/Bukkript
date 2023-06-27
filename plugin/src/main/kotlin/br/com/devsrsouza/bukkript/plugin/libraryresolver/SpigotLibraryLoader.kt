package br.com.devsrsouza.bukkript.plugin.libraryresolver

import br.com.devsrsouza.bukkript.libraryresolver.BukkriptMavenLibraryResolver
import br.com.devsrsouza.bukkript.libraryresolver.URLClassLoaderAccess
import java.net.URLClassLoader

object SpigotLibraryLoader {
    /**
     * Loads the libraries into Spigot URLClassLoader if is not Running on Paper.
     */
    fun loadOrIgnore() {
        val isRunningOnPaper = runCatching { Class.forName("io.papermc.paper.plugin.entrypoint.classloader.PaperPluginClassLoader") }
            .getOrNull() != null

        if (isRunningOnPaper.not()) {
            val libraries = BukkriptMavenLibraryResolver()
                .shouldExcludeKotlin(true)
                .download()
            val classLoader = this::class.java.classLoader as URLClassLoader
            val access = URLClassLoaderAccess.create(classLoader)
            libraries.forEach { file ->
                access.addURL(file.toURI().toURL())
            }
        }
    }
}
