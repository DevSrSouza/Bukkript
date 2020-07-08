package br.com.devsrsouza.bukkript.script.host.cache

import br.com.devsrsouza.bukkript.script.definition.ScriptDescription
import java.io.File
import kotlin.script.experimental.host.FileScriptSource
import kotlin.script.experimental.jvm.impl.KJvmCompiledScript

data class CachedScript(
    val script: FileScriptSource,
    val cacheFile: File,
    val isValid: Boolean,
    val description: ScriptDescription,
    val compiledScript: KJvmCompiledScript<*>
)