package br.com.devsrsouza.bukkript.script.definition.api

import br.com.devsrsouza.bukkript.script.definition.BukkriptScript
import br.com.devsrsouza.kotlinbukkitapi.extensions.unregisterListener
import br.com.devsrsouza.kotlinbukkitapi.utility.collections.OnExpireCallback
import br.com.devsrsouza.kotlinbukkitapi.utility.collections.OnExpireMapCallback
import br.com.devsrsouza.kotlinbukkitapi.utility.collections.WhenPlayerQuitCollectionCallback
import br.com.devsrsouza.kotlinbukkitapi.utility.collections.WhenPlayerQuitMapCallback
import br.com.devsrsouza.kotlinbukkitapi.utility.collections.expirationListOf
import br.com.devsrsouza.kotlinbukkitapi.utility.collections.expirationMapOf
import br.com.devsrsouza.kotlinbukkitapi.utility.collections.onlinePlayerListOf
import br.com.devsrsouza.kotlinbukkitapi.utility.collections.onlinePlayerMapOf
import br.com.devsrsouza.kotlinbukkitapi.utility.collections.onlinePlayerSetOf
import org.bukkit.entity.Player

// Online player collections

fun BukkriptScript.onlinePlayerListOf() = plugin.onlinePlayerListOf()
    .also {
        onDisable { it.unregisterListener() }
    }

fun BukkriptScript.onlinePlayerListOf(vararg players: Player) = plugin.onlinePlayerListOf(*players).also {
    onDisable { it.unregisterListener() }
}

fun BukkriptScript.onlinePlayerListOf(vararg pair: Pair<Player, WhenPlayerQuitCollectionCallback>) =
    plugin.onlinePlayerListOf(*pair).also {
        onDisable { it.unregisterListener() }
    }

// Set

fun BukkriptScript.onlinePlayerSetOf() = plugin.onlinePlayerSetOf()
    .also {
        onDisable { it.unregisterListener() }
    }

fun BukkriptScript.onlinePlayerSetOf(vararg players: Player) = plugin.onlinePlayerSetOf(*players)
    .also {
        onDisable { it.unregisterListener() }
    }

fun BukkriptScript.onlinePlayerSetOf(vararg pair: Pair<Player, WhenPlayerQuitCollectionCallback>) =
    plugin.onlinePlayerSetOf(*pair)
        .also {
            onDisable { it.unregisterListener() }
        }

// Map

fun <V> BukkriptScript.onlinePlayerMapOf() = plugin.onlinePlayerMapOf<V>()
    .also {
        onDisable { it.unregisterListener() }
    }

fun <V> BukkriptScript.onlinePlayerMapOf(vararg pair: Pair<Player, V>) = plugin.onlinePlayerMapOf(*pair)
    .also {
        onDisable { it.unregisterListener() }
    }

fun <V> BukkriptScript.onlinePlayerMapOf(vararg triple: Triple<Player, V, WhenPlayerQuitMapCallback<V>>) =
    plugin.onlinePlayerMapOf(*triple)
        .also {
            onDisable { it.unregisterListener() }
        }

// Expiration List

fun <E> BukkriptScript.expirationListOf() = plugin.expirationListOf<E>()
    .also {
        onDisable { it.clear() }
    }

fun <E> BukkriptScript.expirationListOf(expireTime: Int, vararg elements: E) =
    plugin.expirationListOf(expireTime, *elements)
        .also {
            onDisable { it.clear() }
        }

fun <E> BukkriptScript.expirationListOf(expireTime: Int, vararg elements: Pair<E, OnExpireCallback<E>>) =
    plugin.expirationListOf(expireTime, *elements)
        .also {
            onDisable { it.clear() }
        }

// Expiration Map

fun <K, V> BukkriptScript.expirationMapOf() = plugin.expirationMapOf<K, V>()
    .also {
        onDisable { it.clear() }
    }

fun <K, V> BukkriptScript.expirationMapOf(expireTime: Long, vararg elements: Pair<K, V>) =
    plugin.expirationMapOf(expireTime, *elements)
        .also {
            onDisable { it.clear() }
        }

fun <K, V> BukkriptScript.expirationMapOf(expireTime: Long, vararg elements: Triple<K, V, OnExpireMapCallback<K, V>>) =
    plugin.expirationMapOf(expireTime, *elements)
        .also {
            onDisable { it.clear() }
        }
