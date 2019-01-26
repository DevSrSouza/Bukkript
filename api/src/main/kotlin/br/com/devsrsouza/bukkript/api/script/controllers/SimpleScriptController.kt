package br.com.devsrsouza.bukkript.api.script.controllers

import br.com.devsrsouza.bukkript.api.script.DisableBlock

class SimpleScriptController : ScriptController() {

    var disable: DisableBlock? = null

    override fun disable() {
        disable()
    }
}