package br.com.devsrsouza.bukkript.script.host.compiler

import br.com.devsrsouza.bukkript.script.definition.ScriptDescription
import kotlin.script.experimental.host.FileScriptSource
import kotlin.script.experimental.jvm.impl.KJvmCompiledScript

data class BukkriptCompiledScript(
    val path: String,
    val source: FileScriptSource,
    val compiled: KJvmCompiledScript<*>,
    val description: ScriptDescription
) {
    val pathWithoutExtension get() = path.removeSuffix("bk.kts")
}