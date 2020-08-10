package br.com.devsrsouza.bukkript.plugin

import br.com.devsrsouza.bukkript.script.definition.BukkriptScript
import br.com.devsrsouza.bukkript.script.host.loader.BukkriptLoadedScript

internal fun BukkriptLoadedScript.disable() {
    script.disable()
}


internal fun BukkriptScript.disable() {
    onDisableListeners.forEach { it() }
}
