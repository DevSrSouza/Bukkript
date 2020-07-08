package br.com.devsrsouza.bukkript.intellij

import com.intellij.ui.IconManager
import javax.swing.Icon


object BukkriptIcons {
    private fun load(path: String): Icon {
        return IconManager.getInstance().getIcon(path, BukkriptIcons::class.java)
    }

    val Bukkript = load("/icons/kotlinbukkitapi-logo.svg")
}