package br.com.devsrsouza.bukkript.plugin.manager

import br.com.devsrsouza.bukkript.plugin.BukkriptPlugin
import br.com.devsrsouza.bukkript.plugin.result.LoadResult
import br.com.devsrsouza.bukkript.script.host.loader.BukkriptLoadedScript
import br.com.devsrsouza.kotlinbukkitapi.architecture.lifecycle.LifecycleListener
import kotlinx.coroutines.Deferred
import org.bukkit.entity.Player
import java.util.concurrent.ConcurrentHashMap

interface ScriptManager : LifecycleListener<BukkriptPlugin> {

    val loadedScripts: ConcurrentHashMap<String, BukkriptLoadedScript>

    fun load(scriptName: String): Deferred<LoadResult>

    fun isLoaded(scriptName: String): Boolean

    fun unload(scriptName: String)

    fun reload(scriptName: String)

    fun recompile(scriptName: String)

    fun lockLog(player: Player, scriptName: String)

    fun hotRecompile(scriptName: String)

    fun listScriptsFromFolder(): Set<String>
}