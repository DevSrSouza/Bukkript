package br.com.devsrsouza.bukkript.plugin.manager.script

import br.com.devsrsouza.bukkript.script.definition.ScriptDescription
import br.com.devsrsouza.bukkript.script.host.compiler.BukkriptCompiledScript
import br.com.devsrsouza.bukkript.script.host.loader.BukkriptLoadedScript
import java.io.File

sealed class ScriptState(
    val scriptName: String
) {
    // when the script is know that exists but is not compiled
    class Discovered(
        scriptName: String
    ) : ScriptState(scriptName)

    // the first state when is loading a script and need to check if is no Valid cached script.
    class CheckingCache(
        scriptName: String
    ) : ScriptState(scriptName)

    class Compiling(
        scriptName: String,
        val scriptFile: File,
        val description: ScriptDescription
    ) : ScriptState(scriptName)

    class Loading(
        scriptName: String,
        val compiledScript: BukkriptCompiledScript
    ) : ScriptState(scriptName)

    class Loaded(
        scriptName: String,
        val loadedScript: BukkriptLoadedScript
    ) : ScriptState(scriptName)

    // when the script is compiled but not loaded
    class Unloaded(
        scriptName: String,
        val compiledScript: BukkriptCompiledScript
    ) : ScriptState(scriptName)

    // when is unloading the script
    class Unloading(
        scriptName: String,
        val compiledScript: BukkriptCompiledScript
    ) : ScriptState(scriptName)

    // when get a fail compilation
    class CompileFail(
        scriptName: String,
        val error: String
    ) : ScriptState(scriptName)

    // when catchs a exception while loading the script
    class LoadFail(
        scriptName: String,
        val error: String
    ) : ScriptState(scriptName)
}