package br.com.devsrsouza.bukkript.host

import br.com.devsrsouza.bukkript.api.ScriptDescription
import br.com.devsrsouza.bukkript.api.script.BukkriptCompiledScript
import br.com.devsrsouza.bukkript.script.BukkriptScript
import java.io.File
import kotlin.script.experimental.api.CompiledScript

class BukkriptCompiledScriptImpl(
    scriptFileName: String,
    scriptFile: File,
    compiledScript: CompiledScript<BukkriptScript>,
    description: ScriptDescription
) : BukkriptCompiledScript(
    scriptFileName,
    scriptFile,
    compiledScript,
    description
)