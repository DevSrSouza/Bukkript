package br.com.devsrsouza.bukkript.intellij

import br.com.devsrsouza.bukkript.script.definition.BukkriptScript
import org.jetbrains.kotlin.utils.PathUtil
import java.io.File
import kotlin.script.experimental.intellij.ScriptDefinitionsProvider

class BukkriptScriptDefinitionProvider : ScriptDefinitionsProvider {

    companion object {
        const val ID = "BukkriptScriptDefinition"
    }

    override val id: String
        get() = ID

    override fun getDefinitionClasses(): Iterable<String> {
        return listOf("br.com.devsrsouza.bukkript.script.definition.BukkriptScript")
    }

    override fun getDefinitionsClassPath(): Iterable<File> {
        val jarFile = PathUtil.getResourcePathForClass(BukkriptScript::class.java)

        return listOf(jarFile)
    }

    override fun useDiscovery(): Boolean {
        return false
    }


}