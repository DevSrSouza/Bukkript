package br.com.devsrsouza.bukkript.api.script.controllers

import br.com.devsrsouza.bukkript.api.script.AbstractScript

abstract class ScriptController {
    abstract fun disable()
}

inline fun <reified T : ScriptController> AbstractScript.controllerByType(): T? {
    return controllers.find { it::class == T::class } as? T
}