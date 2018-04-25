package br.com.devsrsouza.bukkript.script

val bukkitImports = listOf(
    "org.bukkit.*",
    "org.bukkit.block.*",
    "org.bukkit.block.banner.*",
    "org.bukkit.command.*",
    "org.bukkit.configuration.*",
    "org.bukkit.configuration.file.*",
    "org.bukkit.configuration.serialization.*",
    "org.bukkit.enchantments.*",
    "org.bukkit.entity.*",
    "org.bukkit.entity.minecart.*",
    "org.bukkit.event.*",
    "org.bukkit.event.block.*",
    "org.bukkit.event.enchantment.*",
    "org.bukkit.event.entity.*",
    "org.bukkit.event.hanging.*",
    "org.bukkit.event.inventory.*",
    "org.bukkit.event.painting.*",
    "org.bukkit.event.player.*",
    "org.bukkit.event.server.*",
    "org.bukkit.event.weather.*",
    "org.bukkit.event.world.*",
    "org.bukkit.generator.*",
    "org.bukkit.help.*",
    "org.bukkit.inventory.*",
    "org.bukkit.inventory.meta.*",
    "org.bukkit.map.*",
    "org.bukkit.material.*",
    "org.bukkit.metadata.*",
    "org.bukkit.permissions.*",
    "org.bukkit.plugin.*",
    "org.bukkit.plugin.messaging.*",
    "org.bukkit.potion.*",
    "org.bukkit.projectiles.*",
    "org.bukkit.scheduler.*",
    "org.bukkit.scoreboard.*",
    "org.bukkit.util.*",
    "org.bukkit.util.io.*",
    "org.bukkit.util.noise.*",
    "org.bukkit.util.permissions.*"
)

val kotlinBukkitAPIImports = listOf(
    "br.com.devsrsouza.kotlinbukkitapi.dsl.command.*",
    "br.com.devsrsouza.kotlinbukkitapi.dsl.event.*",
    "br.com.devsrsouza.kotlinbukkitapi.dsl.item.*",
    "br.com.devsrsouza.kotlinbukkitapi.dsl.menu.*",
    "br.com.devsrsouza.kotlinbukkitapi.dsl.scheduler.*",
    "br.com.devsrsouza.kotlinbukkitapi.dsl.config.*",
    "br.com.devsrsouza.kotlinbukkitapi.extensions.*",
    "br.com.devsrsouza.kotlinbukkitapi.extensions.bungeecord.*",
    "br.com.devsrsouza.kotlinbukkitapi.plugins.placeholderapi.*",
    "br.com.devsrsouza.kotlinbukkitapi.plugins.vault.*",
    "br.com.devsrsouza.kotlinbukkitapi.extensions.text.*",
    "br.com.devsrsouza.kotlinbukkitapi.extensions.time.*"
)

val bukkriptImports = listOf(
    "br.com.devsrsouza.bukkript.Bukkript",
    "br.com.devsrsouza.bukkript.script.Depend",
    "br.com.devsrsouza.bukkript.script.SoftDepend"
)