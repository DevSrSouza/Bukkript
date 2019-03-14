package br.com.devsrsouza.bukkript

import br.com.devsrsouza.bukkript.api.script.FailReason
import br.com.devsrsouza.bukkript.api.script.ScriptDisableResult
import br.com.devsrsouza.bukkript.host.compileScripts
import br.com.devsrsouza.kotlinbukkitapi.dsl.item.meta
import br.com.devsrsouza.kotlinbukkitapi.dsl.menu.*
import br.com.devsrsouza.kotlinbukkitapi.extensions.player.playSound
import br.com.devsrsouza.kotlinbukkitapi.extensions.text.msg
import br.com.devsrsouza.kotlinbukkitapi.extensions.text.plus
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

private var _forceReloadMenu: Menu? = null
val Bukkript.forceReloadMenu: Menu get() {
    if (_forceReloadMenu == null)
        _forceReloadMenu = menu("Force reload script", 3, true) {
            slot(2, 3) {
                item = ItemStack(Material.WOOL).apply {
                    data.data = 5
                    meta<ItemMeta> {
                        displayName = ChatColor.GREEN + "Force reload"
                    }
                }
                onClick {
                    val script = getPlayerData("script") as? String
                    if(script != null) {
                        val result = reloadScript(script, true, player) {
                            player.msg(ChatColor.GREEN + "Reload finish")
                            player.playSound(Sound.LEVEL_UP, 1f, 1f)
                        }
                        when(result) {
                            ReloadScriptCause.SUCESS -> close()
                            ReloadScriptCause.NOT_FOUND -> close()
                            ReloadScriptCause.HAVE_DEPENDENCIES -> close()
                        }
                    } else close()
                }
            }
            slot(2, 7) {
                item = ItemStack(Material.WOOL).apply {
                    data.data = 14
                    meta<ItemMeta> {
                        displayName = ChatColor.RED + "Cancel"
                    }
                }
                onClick { close() }
            }
        }

    return _forceReloadMenu!!
}