package br.com.devsrsouza.bukkript.api.script.controllers

import com.okkero.skedule.CoroutineTask

class TaskScriptController : ScriptController() {
    val tasks = mutableListOf<CoroutineTask>()

    override fun disable() {
        tasks.forEach { it.cancel() }
    }
}