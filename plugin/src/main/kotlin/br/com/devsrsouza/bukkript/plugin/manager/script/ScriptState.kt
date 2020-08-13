package br.com.devsrsouza.bukkript.plugin.manager.script

import br.com.devsrsouza.bukkript.script.definition.ScriptDescription
import br.com.devsrsouza.bukkript.script.host.compiler.BukkriptCompiledScript
import br.com.devsrsouza.bukkript.script.host.loader.BukkriptLoadedScript
import br.com.devsrsouza.kotlinbukkitapi.extensions.text.translateColor
import java.io.File

sealed class ScriptState(
    val scriptName: String
) {

    abstract fun stateDisplayName(): String

    // when the script is know that exists but is not compiled
    class Discovered(
        scriptName: String
    ) : ScriptState(scriptName) {
        override fun stateDisplayName() = "&7Discovered".translateColor()
    }

    // the first state when is loading a script and need to check if is no Valid cached script.
    class CheckingCache(
        scriptName: String
    ) : ScriptState(scriptName) {
        override fun stateDisplayName() = "&bChecking Cache".translateColor()
    }

    class Compiling(
        scriptName: String,
        val scriptFile: File,
        val description: ScriptDescription
    ) : ScriptState(scriptName) {
        override fun stateDisplayName() = "&bCompiling".translateColor()
    }

    class Loading(
        scriptName: String,
        val compiledScript: BukkriptCompiledScript
    ) : ScriptState(scriptName) {
        override fun stateDisplayName() = "&bLoading".translateColor()
    }

    class Loaded(
        scriptName: String,
        val loadedScript: BukkriptLoadedScript
    ) : ScriptState(scriptName) {
        override fun stateDisplayName() = "&aLoaded".translateColor()
    }

    // when the script is compiled but not loaded
    class Unloaded(
        scriptName: String,
        val compiledScript: BukkriptCompiledScript
    ) : ScriptState(scriptName) {
        override fun stateDisplayName() = "&eUnloaded".translateColor()
    }

    // when is unloading the script
    class Unloading(
        scriptName: String,
        val compiledScript: BukkriptCompiledScript
    ) : ScriptState(scriptName) {
        override fun stateDisplayName() = "&bUnloading".translateColor()
    }

    // when get a fail compilation
    class CompileFail(
        scriptName: String,
        val error: String
    ) : ScriptState(scriptName) {
        override fun stateDisplayName() = "&4Compile File".translateColor()
    }

    // when catchs a exception while loading the script
    class LoadFail(
        scriptName: String,
        val error: String
    ) : ScriptState(scriptName) {
        override fun stateDisplayName() = "&4Load Fail".translateColor()
    }
}