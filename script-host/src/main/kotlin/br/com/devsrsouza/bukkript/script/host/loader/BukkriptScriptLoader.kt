package br.com.devsrsouza.bukkript.script.host.loader

import br.com.devsrsouza.bukkript.script.host.compiler.BukkriptCompiledScript
import br.com.devsrsouza.bukkript.script.host.exception.BukkriptLoadException

typealias ScriptPath = String

interface BukkriptScriptLoader {
    @Throws(BukkriptLoadException::class)
    suspend fun load(compiledScript: BukkriptCompiledScript): BukkriptLoadedScript
}