package br.com.devsrsouza.bukkript.script.definition.api

import br.com.devsrsouza.bukkript.script.definition.BukkriptScript
import br.com.devsrsouza.kotlinbukkitapi.dsl.scoreboard.ScoreboardDSLBuilder
import br.com.devsrsouza.kotlinbukkitapi.dsl.scoreboard.ScoreboardDSLMarker
import br.com.devsrsouza.kotlinbukkitapi.dsl.scoreboard.scoreboard

@ScoreboardDSLMarker
inline fun BukkriptScript.scoreboard(
    title: String,
    block: ScoreboardDSLBuilder.() -> Unit
) = plugin.scoreboard(title, block).also {
    onDisable {
        it.dispose()
    }
}