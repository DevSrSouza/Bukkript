package br.com.devsrsouza.bukkript.script.host.cache

import br.com.devsrsouza.bukkript.script.definition.ScriptDescription
import br.com.devsrsouza.bukkript.script.definition.bukkritNameRelative
import br.com.devsrsouza.bukkript.script.host.compiler.BukkriptCompiledScript
import java.io.File
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import kotlin.script.experimental.api.CompiledScript
import kotlin.script.experimental.api.ScriptCompilationConfiguration
import kotlin.script.experimental.api.SourceCode
import kotlin.script.experimental.host.FileScriptSource
import kotlin.script.experimental.jvm.CompiledJvmScriptsCache
import kotlin.script.experimental.jvm.impl.KJvmCompiledScript

internal class FileBasedScriptCache(
    val scriptDir: File,
    val cacheDir: File,
    scriptDescription: ScriptDescription?
) : CompiledJvmScriptsCache {
    var scriptInfo = scriptDescription
        private set

    override fun get(
        script: SourceCode,
        scriptCompilationConfiguration: ScriptCompilationConfiguration
    ): CompiledScript<*>? {
        val fileSource = script as FileScriptSource

        val cached = findCacheScript(fileSource) ?: return null

        if(!cached.isValid) {
            cached.cacheFile.delete()
            return null
        } else {
            // TODO: migrate to a proper log system
            //println("Loading cache of ${script.file.scriptName(cacheDir)}.")
            return cached.compiled.compiled
        }
    }

    override fun store(
        compiledScript: CompiledScript<*>,
        script: SourceCode,
        configuration: ScriptCompilationConfiguration
    ) {
        val file = getCacheFileForScript(script as FileScriptSource)
        file.parentFile.mkdirs()
        file.outputStream().use { fs ->
            ObjectOutputStream(fs).use { os ->
                os.writeUTF(script.generateMD5())
                os.writeObject(scriptInfo)
                os.writeObject(compiledScript)
            }
        }

    }

    fun findCacheScript(
        script: FileScriptSource
    ): CachedScript? {
        val cacheFile = getCacheFileForScript(script).takeIf { it.exists() }
            ?: return null

        val (md5, description, jvmScript) = cacheFile.inputStream().use { fs ->
            ObjectInputStream(fs).use { os ->

                val md5 = os.readUTF()
                val info = os.readObject() as ScriptDescription
                val jvmScript = os.readObject() as KJvmCompiledScript<*>

                Triple(md5, info, jvmScript)
            }
        }

        return CachedScript(
            cacheFile,
            isValid(script, md5),
            BukkriptCompiledScript(
                script,
                jvmScript,
                description
            )
        )
    }

    private fun getCacheFileForScript(script: FileScriptSource) = File(
        cacheDir,
        script.bukkritNameRelative(scriptDir).replace("/", ".")
    )

    private fun isValid(
        script: SourceCode,
        md5: String
    ): Boolean {
        return script.generateMD5() == md5
    }
}
