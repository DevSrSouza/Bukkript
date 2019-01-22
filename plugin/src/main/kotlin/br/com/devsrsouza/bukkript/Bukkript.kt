package br.com.devsrsouza.bukkript

import org.bukkit.plugin.java.JavaPlugin

class Bukkript : JavaPlugin() {
    companion object {
        lateinit var INSTANCE: Bukkript
            private set
    }

    override fun onLoad() { INSTANCE = this }

    override fun onEnable() {

    }
}