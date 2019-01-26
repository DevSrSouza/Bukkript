package br.com.devsrsouza.bukkript.api.script.controllers

import br.com.devsrsouza.kotlinbukkitapi.dsl.command.*

class CommandScriptController : ScriptController() {
    val commands = mutableListOf<KCommand>()

    override fun disable() {
        commands.forEach { it.unregister() }
    }
}