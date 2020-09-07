package br.com.devsrsouza.bukkript.script.host.compiler.scripting

import br.com.devsrsouza.bukkript.script.host.relocation.ScriptRelocate
import org.jetbrains.kotlin.scripting.compiler.plugin.ScriptCompilerProxy
import org.jetbrains.kotlin.scripting.compiler.plugin.impl.ScriptJvmCompilerIsolated
import java.io.File
import kotlin.script.experimental.api.*
import kotlin.script.experimental.host.ScriptingHostConfiguration
import kotlin.script.experimental.host.withDefaultsFrom
import kotlin.script.experimental.jvm.defaultJvmScriptingHostConfiguration
import kotlin.script.experimental.jvm.impl.KJvmCompiledModuleInMemory
import kotlin.script.experimental.jvm.impl.KJvmCompiledScript

open class BukkriptJvmScriptCompiler(
    baseHostConfiguration: ScriptingHostConfiguration = defaultJvmScriptingHostConfiguration
) : ScriptCompiler {

    val hostConfiguration = baseHostConfiguration.withDefaultsFrom(defaultJvmScriptingHostConfiguration)

    val compilerProxy: ScriptCompilerProxy = ScriptJvmCompilerIsolated(hostConfiguration)

    override suspend operator fun invoke(
        script: SourceCode,
        scriptCompilationConfiguration: ScriptCompilationConfiguration
    ): ResultWithDiagnostics<CompiledScript> {
        val compiled = compilerProxy.compile(
            script,
            scriptCompilationConfiguration.with {
                hostConfiguration.update { it.withDefaultsFrom(this@BukkriptJvmScriptCompiler.hostConfiguration) }
            }
        )

        return compiled.onSuccess {
            val compiled = it as KJvmCompiledScript

            val inMemory = compiled.getCompiledModule() as KJvmCompiledModuleInMemory

            val classesBytecode = inMemory.compilerOutputFiles as MutableMap<String, ByteArray>

            for ((className, byteCode) in classesBytecode) {
                classesBytecode.remove(className)

                val (newClassName, newByteCode) = ScriptRelocate.relocate(className, byteCode)

                // TODO: remove, debug propose
                File(newClassName.replace("/", ".")).writeBytes(newByteCode)

                classesBytecode.put(newClassName, newByteCode)
            }

            it.asSuccess()
        }
    }

}

