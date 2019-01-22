package br.com.devsrsouza.bukkript.script

import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin
import org.jetbrains.kotlin.name.NameUtils
import org.jetbrains.kotlin.script.KotlinScriptDefinition
import org.jetbrains.kotlin.utils.addToStdlib.ifNotEmpty
import kotlin.reflect.KClass
import kotlin.script.dependencies.Environment
import kotlin.script.dependencies.ScriptContents
import kotlin.script.experimental.dependencies.DependenciesResolver
import kotlin.script.experimental.dependencies.ScriptDependencies
import kotlin.script.experimental.dependencies.asSuccess
import kotlin.script.templates.AcceptedAnnotations

typealias Runnable = () -> Unit

object BukkriptScriptDefinition : KotlinScriptDefinition(BukkriptScriptTemplate::class) {
    override val dependencyResolver: DependenciesResolver
        get() = object : BukkriptScriptResolver {}

    override val acceptedAnnotations: List<KClass<out Annotation>>
        get() = listOf(Depend::class, SoftDepend::class)

    /*override fun getScriptName(script: KtScript): Name {
        return Name.identifier("br.com.devsrsouza.bukkript.scripts.${super.getScriptName(script).asString()}")
    }*/

}

abstract class BukkriptScriptTemplate(val plugin: Plugin) : Listener {

    private var disable: Runnable? = null

    fun disable(onDisable: Runnable) {
        disable = onDisable
    }
}

internal val scriptAnnotations: MutableMap<String, List<Annotation>> = mutableMapOf()

interface BukkriptScriptResolver : DependenciesResolver {

    @AcceptedAnnotations(Depend::class, SoftDepend::class, Lib::class)
    override fun resolve(scriptContents: ScriptContents
                         ,environment: Environment) : DependenciesResolver.ResolveResult {
        if(scriptContents.file != null) {
            scriptContents.annotations.filter { it is Depend || it is SoftDepend || it is Lib }.toList().ifNotEmpty {
                scriptAnnotations.put(NameUtils.getScriptNameForFile(scriptContents.file!!.name).asString(), this)
            }
        }

        return ScriptDependencies(
            classpath = classpathFromBukkit(),
            imports = bukkitImports + kotlinBukkitAPICoreImports
                    + kotlinBukkitAPIAttributeStorageImports
                    + kotlinBukkitAPIPluginsImports + bukkriptImports
        ).asSuccess()
    }
}
