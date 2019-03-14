package br.com.devsrsouza.bukkript.api.script.controllers

import br.com.devsrsouza.kotlinbukkitapi.dsl.command.*
import br.com.devsrsouza.kotlinbukkitapi.extensions.command.unregister

class CommandScriptController : ScriptController() {
    val commands = mutableListOf<CommandDSL>()

    override fun disable() {
        commands.forEach { it.unregister() }
    }
}