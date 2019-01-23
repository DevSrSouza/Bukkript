package br.com.devsrsouza.bukkript.host

import br.com.devsrsouza.bukkript.api.ScriptDescription
import br.com.devsrsouza.bukkript.script.BukkriptScript
import java.io.File
import kotlin.script.experimental.api.CompiledScript

class BukkriptCompiledScript(
    val scriptFileName: String,
    val scriptFile: File,
    val compiledScript: CompiledScript<BukkriptScript>,
    val description: ScriptDescription
)