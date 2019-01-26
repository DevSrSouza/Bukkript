package br.com.devsrsouza.bukkript.api.script.controllers

import br.com.devsrsouza.kotlinbukkitapi.dsl.event.unregisterAll
import org.bukkit.event.Listener

class EventScriptController : ScriptController() {
    val events = mutableListOf<Listener>()

    override fun disable() {
        events.forEach { it.unregisterAll() }
    }
}