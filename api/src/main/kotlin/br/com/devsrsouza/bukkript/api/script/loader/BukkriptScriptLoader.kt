package br.com.devsrsouza.bukkript.api.script.loader

import br.com.devsrsouza.bukkript.api.BukkriptAPI
import br.com.devsrsouza.bukkript.api.ScriptDescription
import br.com.devsrsouza.bukkript.api.script.AbstractScript
import br.com.devsrsouza.bukkript.api.script.BukkriptLoadedScript
import br.com.devsrsouza.bukkript.api.script.ScriptDisableResult
import java.io.File

abstract class BukkriptScriptLoader(val api: BukkriptAPI) {

    val scripts = mutableMapOf<String, BukkriptLoadedScript>()

    abstract fun getScript(file: File): AbstractScript?
    abstract fun getScript(name: String): AbstractScript?
    abstract fun getScriptDescription(name: String): ScriptDescription?
    abstract fun loadScript(scriptLoaded: BukkriptLoadedScript)
    abstract fun disableScript(script: File, force: Boolean): ScriptDisableResult
    abstract fun disableScript(script: String, force: Boolean): ScriptDisableResult

    abstract fun getClassByName(name: String): Class<*>?
}