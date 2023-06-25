package br.com.devsrsouza.bukkript.script.host.compiler

import br.com.devsrsouza.bukkript.script.definition.ScriptDescription
import br.com.devsrsouza.bukkript.script.host.cache.CachedScript
import br.com.devsrsouza.bukkript.script.host.exception.BukkriptCompilationException
import java.io.File

interface BukkriptScriptCompiler {

    suspend fun retrieveDescriptor(scriptFile: File): ScriptDescription?

    @Throws(BukkriptCompilationException::class)
    suspend fun compile(scriptFile: File, description: ScriptDescription): BukkriptCompiledScript

    suspend fun getCachedScript(scriptFile: File): CachedScript?
}
