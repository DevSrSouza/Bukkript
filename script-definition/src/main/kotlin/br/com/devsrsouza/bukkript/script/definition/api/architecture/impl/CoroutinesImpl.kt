package br.com.devsrsouza.bukkript.script.definition.api.architecture.impl

import br.com.devsrsouza.bukkript.script.definition.BukkriptScript
import br.com.devsrsouza.kotlinbukkitapi.architecture.KotlinPlugin
import br.com.devsrsouza.kotlinbukkitapi.architecture.lifecycle.LifecycleListener
import br.com.devsrsouza.kotlinbukkitapi.architecture.lifecycle.getOrInsertGenericLifecycle
import br.com.devsrsouza.kotlinbukkitapi.collections.onlinePlayerMapOf
import br.com.devsrsouza.kotlinbukkitapi.extensions.skedule.BukkitDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import org.bukkit.entity.Player

internal fun KotlinPlugin.getOrInsertBukkriptCoroutineLifecycle(): BukkriptCoroutineLifecycle {
    return getOrInsertGenericLifecycle(Int.MIN_VALUE) {
        BukkriptCoroutineLifecycle(this)
    }
}


internal class BukkriptCoroutineLifecycle(
    override val plugin: KotlinPlugin
) : LifecycleListener<KotlinPlugin> {

    inner class ScriptCoroutineScope(
        val job: Job,
        val coroutineScope: CoroutineScope
    ) {
        fun cancelJobs() = job.cancel()
    }

    val coroutineScopes = hashMapOf<BukkriptScript, ScriptCoroutineScope>()

    private val playersCoroutineScope by lazy {
        onlinePlayerMapOf<HashMap<BukkriptScript, ScriptCoroutineScope>>()
    }

    override fun onPluginEnable() {}

    override fun onPluginDisable() {
        for (scope in coroutineScopes.values) {
            scope.cancelJobs()
        }
        for (scope in playersCoroutineScope.values) {
            scope.values.forEach { it.cancelJobs() }
        }
    }

    fun getScriptCoroutineScope(script: BukkriptScript): CoroutineScope {
        return coroutineScopes[script]?.coroutineScope
            ?: newCoroutineScope().also {
                coroutineScopes.put(script, it)
            }.also {
                script.onDisable {
                    coroutineScopes.remove(script)?.cancelJobs()
                }

            }.coroutineScope
    }

    fun getPlayerCoroutineScope(script: BukkriptScript, player: Player): CoroutineScope {
        return playersCoroutineScope[player]?.get(script)?.coroutineScope
            ?: newCoroutineScope().also {
                playersCoroutineScope.put(player, hashMapOf(script to it)) { map ->
                    map.values.forEach { it.cancelJobs() }
                }
            }.also {
                script.onDisable {
                    for ((_, map) in playersCoroutineScope) {
                        map.remove(script)?.cancelJobs()
                    }
                }
            }.coroutineScope
    }

    private fun newCoroutineScope(): ScriptCoroutineScope {
        val job = Job()
        return ScriptCoroutineScope(
            job,
            CoroutineScope(BukkitDispatchers.SYNC + job)
        )
    }
}