package br.com.devsrsouza.bukkript.script.definition.api

import br.com.devsrsouza.bukkript.script.definition.BukkriptScript
import br.com.devsrsouza.kotlinbukkitapi.menu.dsl.MenuDSL
import br.com.devsrsouza.kotlinbukkitapi.menu.dsl.menu

inline fun BukkriptScript.menu(
    displayName: String,
    lines: Int,
    cancelOnClick: Boolean = true,
    block: MenuDSL.() -> Unit,
): MenuDSL = plugin.menu(displayName, lines, cancelOnClick, block).also {
    onDisable {
        for (player in it.viewers.keys)
            it.close(player, closeInventory = true)
    }
}
