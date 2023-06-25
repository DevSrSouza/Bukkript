package br.com.devsrsouza.bukkript.script.definition

import br.com.devsrsouza.bukkript.script.definition.api.LogLevel
import br.com.devsrsouza.bukkript.script.definition.compiler.BukkriptScriptCompilationConfiguration
import br.com.devsrsouza.kotlinbukkitapi.architecture.KotlinPlugin
import java.io.File
import kotlin.script.experimental.annotations.KotlinScript

const val BUKKRIPT_EXTENSION = "bk.kts"

typealias RemoveRegistryFunction = () -> Unit

@KotlinScript(
    displayName = "Bukkript script",
    fileExtension = BUKKRIPT_EXTENSION,
    compilationConfiguration = BukkriptScriptCompilationConfiguration::class,
)
abstract class BukkriptScript(
    val plugin: KotlinPlugin,
    val description: ScriptDescription,
    val dataFolder: File,
    val scriptName: String,
    val log: (LogLevel, message: String) -> Unit,
) {

    private val _onDisableListeners = mutableListOf<() -> Unit>()
    val onDisableListeners: List<() -> Unit> = _onDisableListeners

    fun onDisable(callback: () -> Unit): RemoveRegistryFunction {
        _onDisableListeners.add(callback)

        return { _onDisableListeners.remove(callback) }
    }
}
