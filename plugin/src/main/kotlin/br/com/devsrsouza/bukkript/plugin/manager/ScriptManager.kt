package br.com.devsrsouza.bukkript.plugin.manager

import br.com.devsrsouza.bukkript.plugin.BukkriptPlugin
import br.com.devsrsouza.bukkript.plugin.manager.script.ScriptState
import br.com.devsrsouza.bukkript.script.definition.api.LogLevel
import br.com.devsrsouza.kotlinbukkitapi.architecture.lifecycle.LifecycleListener
import kotlinx.coroutines.Job
import org.bukkit.entity.Player
import java.util.concurrent.ConcurrentHashMap

interface ScriptManager : LifecycleListener<BukkriptPlugin> {

    val scripts: ConcurrentHashMap<String, ScriptState>

    fun compile(scriptName: String): Job

    fun load(scriptName: String)

    fun forceLoad(scriptName: String)

    fun isLoaded(scriptName: String): Boolean

    fun unload(scriptName: String)

    fun reload(scriptName: String)

    fun recompile(scriptName: String)

    fun lockLog(player: Player, scriptName: String)

    fun hotRecompile(scriptName: String)

    fun disableHotRecompile(scriptName: String)

    fun isHotRecompileEnable(scriptName: String): Boolean

    fun discoveryAllScripts()

    fun updateLogLevel(scriptName: String, logLevel: LogLevel)
}
