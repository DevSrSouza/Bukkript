package br.com.devsrsouza.bukkript.script.definition.api.architecture

import br.com.devsrsouza.bukkript.script.definition.BukkriptScript
import br.com.devsrsouza.bukkript.script.definition.api.architecture.impl.getOrInsertBukkriptCoroutineLifecycle
import kotlinx.coroutines.CoroutineScope
import org.bukkit.entity.Player

/**
 * A CoroutineScope that trigger in the Main Thread of Bukkit by default.
 *
 * This scope ensures that your task will be canceled when the script disable
 * removing the possibility of Job leaks.
 */
val BukkriptScript.scriptCoroutineScope: CoroutineScope
    get() = plugin.getOrInsertBukkriptCoroutineLifecycle().getScriptCoroutineScope(this)

/**
 * A CoroutineScope that trigger in the Main Thread of Bukkit by default.
 *
 * This scope ensures that your task will be canceled when the script disable
 * and when the [player] disconnect the server removing the possibility of Job leaks.
 */
fun BukkriptScript.playerCoroutineScope(player: Player): CoroutineScope {
    return plugin.getOrInsertBukkriptCoroutineLifecycle().getPlayerCoroutineScope(this, player)
}
