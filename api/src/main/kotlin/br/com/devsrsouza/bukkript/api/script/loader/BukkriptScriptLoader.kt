package br.com.devsrsouza.bukkript.api.script.loader

import br.com.devsrsouza.bukkript.api.BukkriptAPI
import br.com.devsrsouza.bukkript.api.script.BukkriptLoadedScript

abstract class BukkriptScriptLoader(val plugin: BukkriptAPI) {

    val scripts = mutableMapOf<String, BukkriptLoadedScript>()

    abstract fun loadScript(scriptLoaded: BukkriptLoadedScript)
    abstract fun disableScript(script: String): Boolean
}