package br.com.devsrsouza.bukkript.intellij

import org.jetbrains.kotlin.utils.KotlinPaths
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
        val jarFile = File(
            "${System.getenv("project-dir")}/script-definition-embedded/build/libs/script-definition-embedded-0.1.0-SNAPSHOT.jar"
        )

        return listOf(jarFile)
    }

    override fun useDiscovery(): Boolean {
        return false
    }


}