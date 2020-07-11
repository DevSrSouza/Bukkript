package br.com.devsrsouza.bukkript.script.definition

import br.com.devsrsouza.bukkript.script.definition.compiler.BukkriptScriptCompilationConfiguration
import kotlinx.coroutines.CoroutineScope
import org.bukkit.plugin.Plugin
import java.io.File
import kotlin.script.experimental.annotations.KotlinScript

const val BUKKRIPT_EXTENSION = "bk.kts"

typealias RemoveRegistryFunction = () -> Unit

@KotlinScript(
    displayName = "Bukkript script",
    fileExtension = BUKKRIPT_EXTENSION,
    compilationConfiguration = BukkriptScriptCompilationConfiguration::class
)
abstract class BukkriptScript(
    val plugin: Plugin,
    val description: ScriptDescription,
    val dataFolder: File,
    val coroutineScope: CoroutineScope
) {

    private val _onDisableListeners = mutableListOf<() -> Unit>()
    val onDisableListeners: List<() -> Unit> = _onDisableListeners

    fun onDisable(callback: () -> Unit): RemoveRegistryFunction {
        _onDisableListeners.add(callback)

        return { _onDisableListeners.remove(callback) }
    }
}
