package br.com.devsrsouza.bukkript.script.host.loader

import br.com.devsrsouza.bukkript.script.definition.BukkriptScript
import br.com.devsrsouza.bukkript.script.host.compiler.BukkriptCompiledScript
import br.com.devsrsouza.bukkript.script.host.loader.classloader.ScriptClassloader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import java.io.File
import kotlin.reflect.KClass

data class BukkriptLoadedScript(
    val script: BukkriptScript,
    val kclass: KClass<*>,
    val classLoader: ScriptClassloader,
    val compiledScript: BukkriptCompiledScript,
    val dataFolder: File
)