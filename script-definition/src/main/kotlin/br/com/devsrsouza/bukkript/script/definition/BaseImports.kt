package br.com.devsrsouza.bukkript.script.definition

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
val kotlinBukkitAPICoreImports = listOf(
    /* Architecture package not needed */
    "br.com.devsrsouza.kotlinbukkitapi.collections.*",
    /* Controller package not needed */
    "br.com.devsrsouza.kotlinbukkitapi.dsl.command.*",
    "br.com.devsrsouza.kotlinbukkitapi.dsl.command.arguments.*",
    "br.com.devsrsouza.kotlinbukkitapi.dsl.command.KCommand",
    "br.com.devsrsouza.kotlinbukkitapi.dsl.command.ExecutorBlock",
    "br.com.devsrsouza.kotlinbukkitapi.dsl.command.Executor",
    "br.com.devsrsouza.kotlinbukkitapi.dsl.command.CommandException",
    "br.com.devsrsouza.kotlinbukkitapi.dsl.command.TabCompleter",
    "br.com.devsrsouza.kotlinbukkitapi.dsl.command.TabCompleterBlock",
    "br.com.devsrsouza.kotlinbukkitapi.dsl.command.ExecutorPlayerBlock",

    "br.com.devsrsouza.kotlinbukkitapi.dsl.menu.*",
    "br.com.devsrsouza.kotlinbukkitapi.dsl.menu.pagination.*",
    "br.com.devsrsouza.kotlinbukkitapi.dsl.menu.pagination.slot.*",
    "br.com.devsrsouza.kotlinbukkitapi.dsl.menu.slot.*",

    "br.com.devsrsouza.kotlinbukkitapi.dsl.scoreboard.*",

    "br.com.devsrsouza.kotlinbukkitapi.extensions.*",
    "br.com.devsrsouza.kotlinbukkitapi.extensions.block.*",
    "br.com.devsrsouza.kotlinbukkitapi.extensions.bukkit.*",
    "br.com.devsrsouza.kotlinbukkitapi.extensions.bungeecord.*",
    "br.com.devsrsouza.kotlinbukkitapi.extensions.command.*",
    "br.com.devsrsouza.kotlinbukkitapi.extensions.configuration.*",
    "br.com.devsrsouza.kotlinbukkitapi.extensions.entity.*",
    "br.com.devsrsouza.kotlinbukkitapi.extensions.event.*",
    "br.com.devsrsouza.kotlinbukkitapi.extensions.inventory.*",
    "br.com.devsrsouza.kotlinbukkitapi.extensions.item.*",
    "br.com.devsrsouza.kotlinbukkitapi.extensions.location.*",
    "br.com.devsrsouza.kotlinbukkitapi.extensions.permission.*",
    "br.com.devsrsouza.kotlinbukkitapi.extensions.player.*",
    "br.com.devsrsouza.kotlinbukkitapi.extensions.plugin.*",
    "br.com.devsrsouza.kotlinbukkitapi.extensions.scheduler.*",
    "br.com.devsrsouza.kotlinbukkitapi.extensions.skedule.*",
    "br.com.devsrsouza.kotlinbukkitapi.extensions.text.*",
    "br.com.devsrsouza.kotlinbukkitapi.extensions.world.*",

    /* Flow Event ignored because already have a implementation at Bukkript */

    "br.com.devsrsouza.kotlinbukkitapi.menu.*",
    "br.com.devsrsouza.kotlinbukkitapi.menu.slot.*",

    "br.com.devsrsouza.kotlinbukkitapi.utils.*",
    "br.com.devsrsouza.kotlinbukkitapi.utils.player.*",
    "br.com.devsrsouza.kotlinbukkitapi.utils.time.*"
)

val kotlinBukkitAPIPluginsImports = listOf(
    "br.com.devsrsouza.kotlinbukkitapi.plugins.placeholderapi.*",
    "br.com.devsrsouza.kotlinbukkitapi.plugins.vault.*",
    "br.com.devsrsouza.kotlinbukkitapi.plugins.bossbarapi.*",
    "br.com.devsrsouza.kotlinbukkitapi.plugins.hologramapi.*",
    "br.com.devsrsouza.kotlinbukkitapi.plugins.dvdwplaceholderapi.*",
    "br.com.devsrsouza.kotlinbukkitapi.plugins.worldedit.*",
    "br.com.devsrsouza.kotlinbukkitapi.plugins.viaversion.*"
)

val kotlinBukkitAPIExposedImports = listOf(
    "br.com.devsrsouza.kotlinbukkitapi.exposed.*",
    "br.com.devsrsouza.kotlinbukkitapi.exposed.delegate.*"
)

val bukkriptImports = listOf(
    "br.com.devsrsouza.bukkript.script.definition.annotation.*",
    "br.com.devsrsouza.bukkript.script.definition.api.*",
    "br.com.devsrsouza.bukkript.script.definition.api.architecture.*"
)
