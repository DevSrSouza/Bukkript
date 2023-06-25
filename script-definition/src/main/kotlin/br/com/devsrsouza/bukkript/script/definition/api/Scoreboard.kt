package br.com.devsrsouza.bukkript.script.definition.api

import br.com.devsrsouza.bukkript.script.definition.BukkriptScript
import br.com.devsrsouza.kotlinbukkitapi.scoreboard.ScoreboardDSLBuilder
import br.com.devsrsouza.kotlinbukkitapi.scoreboard.scoreboard
import br.com.devsrsouza.kotlinbukkitapi.scoreboard.ScoreboardDSLMarker

@ScoreboardDSLMarker
inline fun BukkriptScript.scoreboard(
    title: String,
    block: ScoreboardDSLBuilder.() -> Unit
) = plugin.scoreboard(title, block).also {
    onDisable {
        it.dispose()
    }
}