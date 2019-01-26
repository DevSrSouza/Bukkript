package br.com.devsrsouza.bukkript.api.script

import br.com.devsrsouza.bukkript.api.ScriptDescription
import java.io.File
import kotlin.script.experimental.api.CompiledScript

abstract class BukkriptCompiledScript(
    val scriptFileName: String,
    val scriptFile: File,
    val compiledScript: CompiledScript<AbstractScript>,
    val description: ScriptDescription
)