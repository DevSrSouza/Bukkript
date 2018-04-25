package br.com.devsrsouza.bukkript.script

import br.com.devsrsouza.bukkript.Bukkript
import br.com.devsrsouza.bukkript.compiler.compileScripts
import org.bukkit.Bukkit
import java.io.File
import java.net.URLClassLoader
import java.util.logging.Level

object ScriptController {
    val scripts: MutableList<Script> = mutableListOf()

    fun <T : BukkriptScriptTemplate> getScript(fileName: String) =
        scripts.filter { it.enable }.firstOrNull { it.name.equals(fileName.capitalize()) }?.instance as T?

    fun loadScripts(plugin: Bukkript, folder: File) {

        scripts += compileScripts(folder)
            .filter { it.value.superclass.name.equals(BukkriptScriptTemplate::class.java.name) }
            .map { Script(it.key, it.value) } as ArrayList

        scripts.forEach { script ->
            val annotations = scriptAnnotations.get(script.name)
            annotations?.filter { it is Depend || it is SoftDepend }?.forEach {
                when (it) {
                    is Depend -> {
                        it.script.removeSuffix(".kts").capitalize().also {
                            if(it.isNotBlank()) script.depend.add(it)
                        }
                    }
                    is SoftDepend -> {
                        it.script.removeSuffix(".kts").capitalize().also {
                            if(it.isNotBlank()) script.softdepend.add(it)
                        }
                    }
                }
            }
        }

        val sorted = sortForRun(plugin, ArrayList(scripts))

        sorted.forEach {
            try {
                plugin.logger.info("Starting script: ${it.name}")
                it.instance = it.clazz.constructors[0].newInstance(plugin) as BukkriptScriptTemplate
                it.enable = true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

    fun sortForRun(plugin: Bukkript, scripts: ArrayList<Script>) : List<Script> {

        val loadedScripts = mutableListOf<Script>()
        val dependencies = scripts.associate { it.name to it.depend.toMutableList() }
            .filter { it.value.isNotEmpty() }
            .toMutableMap()
        val softDependencies = scripts.associate { it.name to it.depend.toMutableList() }
            .filter { it.value.isNotEmpty() }
            .toMutableMap()

        while (!scripts.isEmpty()) {
            var missingDependency = true
            var scriptsInterator = scripts.listIterator()

            while (scriptsInterator.hasNext()) {
                val script = scriptsInterator.next()

                if (dependencies.containsKey(script.name)) {
                    val dependencyIterator = dependencies.get(script.name)!!.listIterator()

                    while (dependencyIterator.hasNext()) {
                        val dependency = dependencyIterator.next()

                        if (loadedScripts.firstOrNull { it.name.equals(dependency) } != null) {
                            dependencyIterator.remove()
                        } else if (scripts.firstOrNull { it.name.equals(dependency) } === null) {
                            missingDependency = false
                            scriptsInterator.remove()
                            softDependencies.remove(script.name)
                            dependencies.remove(script.name)

                            plugin.logger.log(
                                Level.WARNING,
                                "Could not load ${script.name}: missing dependency $dependency"
                            )
                            break
                        }
                    }
                    if (dependencies.containsKey(script.name) && dependencies.get(script.name)?.isEmpty() == true) {
                        dependencies.remove(script.name)
                    }
                }
                if (softDependencies.containsKey(script.name)) {
                    val softDependenciesIterator = softDependencies.get(script.name)!!.listIterator()

                    while (softDependenciesIterator.hasNext()) {
                        val softDependency = softDependenciesIterator.next()

                        if (scripts.firstOrNull { it.name.equals(softDependency) } === null) {
                            softDependenciesIterator.remove()
                        }
                    }

                    if (softDependencies.get(script.name)!!.isEmpty()) {
                        softDependencies.remove(script.name)
                    }
                }
                if (!(dependencies.containsKey(script.name) || softDependencies.containsKey(script.name))
                    && scripts.contains(script)
                ) {

                    scriptsInterator.remove()
                    missingDependency = false


                    loadedScripts.add(script)
                    continue
                }
            }
            if (missingDependency) {
                scriptsInterator = scripts.listIterator()

                while (scriptsInterator.hasNext()) {
                    val script = scriptsInterator.next()

                    if (!dependencies.containsKey(script.name)) {
                        softDependencies.remove(script.name)
                        missingDependency = false

                        scriptsInterator.remove()

                        loadedScripts.add(script)
                        break
                    }
                }
                if (missingDependency) {
                    softDependencies.clear()
                    dependencies.clear()

                    val failedScriptIterator = scripts.listIterator()

                    while (failedScriptIterator.hasNext()) {
                        val script = failedScriptIterator.next()
                        failedScriptIterator.remove()
                        plugin.getLogger().log(
                            Level.SEVERE,
                            "Could not load ${script.name} in folder 'blalb': circular dependency detected"
                        )
                    }
                }
            }
        }

        return loadedScripts
    }
}



class Script(val name: String,
             val clazz: Class<*>) {
    var enable = false
    var depend = mutableListOf<String>()
    var softdepend = mutableListOf<String>()
    lateinit var instance: BukkriptScriptTemplate
}



@Target(AnnotationTarget.FILE)
@Retention(AnnotationRetention.SOURCE)
@Repeatable
annotation class Depend(val script: String)

@Target(AnnotationTarget.FILE)
@Retention(AnnotationRetention.SOURCE)
@Repeatable
annotation class SoftDepend(val script: String)


fun classpathFromBukkit(): List<File> =
    (Bukkit.getServer().pluginManager.plugins
        .map { it.javaClass.classLoader } + Bukkript::class.java.classLoader.parent)
        .mapNotNull { it as? URLClassLoader }
        .flatMap { it.urLs.toList() }.mapNotNull {
            try {
                File(it.toURI().schemeSpecificPart)
            } catch (e: java.net.URISyntaxException) {
                if (it.protocol != "file") null
                else File(it.file)
            }
        }
