package br.com.devsrsouza.bukkript.script.definition.api

import br.com.devsrsouza.bukkript.script.definition.BukkriptScript
import br.com.devsrsouza.kotlinbukkitapi.extensions.event.event
import br.com.devsrsouza.kotlinbukkitapi.extensions.event.events
import br.com.devsrsouza.kotlinbukkitapi.extensions.event.unregisterListener
import br.com.devsrsouza.kotlinbukkitapi.flow.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onCompletion
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import kotlin.reflect.KClass

inline fun <reified T : Event> BukkriptScript.event(
    priority: EventPriority = EventPriority.NORMAL,
    ignoreCancelled: Boolean = false,
    noinline block: T.() -> Unit
) = event(T::class, priority, ignoreCancelled, block)

fun <T : Event> BukkriptScript.event(
    type: KClass<T>,
    priority: EventPriority = EventPriority.NORMAL,
    ignoreCancelled: Boolean = false,
    block: T.() -> Unit
) = plugin.events { event(plugin, type, priority, ignoreCancelled, block) }

// event flow

inline fun <reified T : Event> BukkriptScript.eventFlow(
    assign: Player? = null,
    priority: EventPriority = EventPriority.NORMAL,
    ignoreCancelled: Boolean = false
): Flow<T> = eventFlow<T>(T::class, assign, priority, ignoreCancelled)

fun <T : Event> BukkriptScript.eventFlow(
    type: KClass<T>,
    assign: Player? = null,
    priority: EventPriority = EventPriority.NORMAL,
    ignoreCancelled: Boolean = false
): Flow<T> {
    val channel = Channel<T>(Channel.CONFLATED)
    val listener = plugin.events {}
    val assignListener = plugin.events {}

    val unregister = onDisable {
        listener.unregisterListener()
        assignListener.unregisterListener()
        channel.close()
    }

    val flow = eventFlow(
        type,
        plugin,
        assign,
        priority,
        ignoreCancelled,
        channel,
        listener,
        assignListener
    ).onCompletion {
        listener.unregisterListener()
        assignListener.unregisterListener()
        unregister()
    }

    return flow
}

