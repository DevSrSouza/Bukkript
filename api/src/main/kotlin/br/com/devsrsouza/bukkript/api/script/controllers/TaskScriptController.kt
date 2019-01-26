package br.com.devsrsouza.bukkript.api.script.controllers

import org.bukkit.scheduler.BukkitTask

class TaskScriptController : ScriptController() {
    val tasks = mutableListOf<BukkitTask>()

    override fun disable() {
        tasks.forEach { it.cancel() }
    }
}