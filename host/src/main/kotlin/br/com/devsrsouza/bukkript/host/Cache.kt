package br.com.devsrsouza.bukkript.host

import br.com.devsrsouza.bukkript.api.BukkriptAPI
import br.com.devsrsouza.bukkript.api.ScriptDescription
import br.com.devsrsouza.bukkript.api.script.scriptName
import br.com.devsrsouza.bukkript.script.*
import br.com.devsrsouza.kotlinbukkitapi.extensions.plugin.info
import br.com.devsrsouza.kotlinbukkitapi.utils.whenErrorNull
import org.bukkit.plugin.Plugin
import java.io.File
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import kotlin.script.experimental.api.CompiledScript
import kotlin.script.experimental.api.ScriptCompilationConfiguration
import kotlin.script.experimental.api.SourceCode
import kotlin.script.experimental.host.FileScriptSource
import kotlin.script.experimental.jvmhost.CompiledJvmScriptsCache
import kotlin.script.experimental.jvmhost.impl.KJvmCompiledScript

internal class FileBasedScriptCache(
    val plugin: Plugin,
    val api: BukkriptAPI,
    description: ScriptDescription? = null
) : CompiledJvmScriptsCache {
    var description = description
        private set

    fun getFile(script: FileScriptSource) = File(api.CACHE_DIR, script.file.scriptName(api))

    fun readDescription(
        script: FileScriptSource
    ): ScriptDescription? = getFile(script).takeIf { it.exists() }?.inputStream().use { fs ->
        ObjectInputStream(fs).use { os ->
            val description = whenErrorNull { os.readObject() as ScriptDescription }
            whenErrorNull { os.readObject() }
            return@use description
        }
    }

    fun isValid(
        source: FileScriptSource
    ) = isValid(source.file, getFile(source))

    private fun isValid(
        sourceFile: File,
        cacheFile: File
    ): Boolean {
        val scriptModification = sourceFile.lastModified()
        val cacheModification = cacheFile.lastModified()

        return scriptModification == cacheModification
    }

    override fun get(
        script: SourceCode,
        scriptCompilationConfiguration: ScriptCompilationConfiguration
    ): CompiledScript<*>? {
        val file = getFile(script as FileScriptSource)

        if (!isValid(script.file, file)) {
            file.delete()
        }

        return if (!file.exists()) null else {
            plugin.info("Loading cache of ${script.file.scriptName(api)}.")
            file.readCompiledScript(scriptCompilationConfiguration)
        }
    }

    override fun store(
        compiledScript: CompiledScript<*>,
        script: SourceCode,
        configuration: ScriptCompilationConfiguration
    ) {
        val file = getFile(script as FileScriptSource)
        file.outputStream().use { fs ->
            ObjectOutputStream(fs).use { os ->
                os.writeObject(
                    description ?: ScriptDescription.DEFAULT
                )
                os.writeObject(compiledScript)
            }
        }
        file.setLastModified(script.file.lastModified())
    }

    private fun File.readCompiledScript(scriptCompilationConfiguration: ScriptCompilationConfiguration): CompiledScript<*> {
        return inputStream().use { fs ->
            ObjectInputStream(fs).use { os ->
                val description = os.readObject() as ScriptDescription
                this@FileBasedScriptCache.description = description
                (os.readObject() as KJvmCompiledScript<*>).apply {
                    setCompilationConfiguration(ScriptCompilationConfiguration(scriptCompilationConfiguration) {
                        set(ScriptCompilationConfiguration.description, description)
                    })
                }
            }
        }
    }
}
