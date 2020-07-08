package br.com.devsrsouza.bukkript.script.definition.api

import br.com.devsrsouza.bukkript.script.definition.BukkriptScript
import br.com.devsrsouza.kotlinbukkitapi.extensions.skedule.BukkitDispatchers
import com.okkero.skedule.BukkitSchedulerController
import com.okkero.skedule.SynchronizationContext
import com.okkero.skedule.schedule

fun BukkriptScript.schedule(
    initialContext: SynchronizationContext = SynchronizationContext.SYNC,
    co: suspend BukkitSchedulerController.() -> Unit
) = plugin.schedule(initialContext, co).also {
    onDisable {
        it.cancel()
    }
}

val BukkriptScript.BukkitDispatchers get() =  plugin.BukkitDispatchers