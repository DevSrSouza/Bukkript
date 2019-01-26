package br.com.devsrsouza.bukkript.host

import br.com.devsrsouza.bukkript.api.BukkriptAPI
import br.com.devsrsouza.kotlinbukkitapi.extensions.plugin.info
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

internal class FileBasedScriptCache(val plugin: Plugin, val api: BukkriptAPI) : CompiledJvmScriptsCache {

    fun getFile(script: FileScriptSource) = File(api.CACHE_DIR, script.file.scriptName(api))

    override fun get(
        script: SourceCode,
        scriptCompilationConfiguration: ScriptCompilationConfiguration
    ): CompiledScript<*>? {
        val file = getFile(script as FileScriptSource)

        val scriptModification = script.file.lastModified()
        val modification = file.lastModified()

        if (scriptModification != modification) {
            file.delete()
        }

        return if (!file.exists()) null else {
            plugin.info("Loading cache of ${file.scriptName(api)}.")
            file.readCompiledScript(scriptCompilationConfiguration)
        }
    }

    override fun store(
        compiledScript: CompiledScript<*>,
        script: SourceCode,
        scriptCompilationConfiguration: ScriptCompilationConfiguration
    ) {
        val file = getFile(script as FileScriptSource)
        file.outputStream().use { fs ->
            ObjectOutputStream(fs).use { os ->
                os.writeObject(compiledScript)
            }
        }
        file.setLastModified(script.file.lastModified())
    }
}

private fun File.readCompiledScript(scriptCompilationConfiguration: ScriptCompilationConfiguration): CompiledScript<*> {
    return inputStream().use { fs ->
        ObjectInputStream(fs).use { os ->
            (os.readObject() as KJvmCompiledScript<*>).apply {
                setCompilationConfiguration(scriptCompilationConfiguration)
            }
        }
    }
}
