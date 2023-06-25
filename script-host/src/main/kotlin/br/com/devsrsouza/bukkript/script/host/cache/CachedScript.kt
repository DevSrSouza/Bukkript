package br.com.devsrsouza.bukkript.script.host.cache

import br.com.devsrsouza.bukkript.script.host.compiler.BukkriptCompiledScript
import java.io.File

data class CachedScript(
    val cacheFile: File,
    val isValid: Boolean,
    val compiled: BukkriptCompiledScript,
)
