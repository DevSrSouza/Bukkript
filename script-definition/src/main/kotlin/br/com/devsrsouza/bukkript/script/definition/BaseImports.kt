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
    "org.bukkit.util.permissions.*",
)

// TODO: update it
val kotlinBukkitAPICoreImports = listOf(
    /* Architecture package not needed */
    "br.com.devsrsouza.kotlinbukkitapi.utility.collections.*",
    "br.com.devsrsouza.kotlinbukkitapi.utility.extensions.*",
    "br.com.devsrsouza.kotlinbukkitapi.utility.types.*",
    "br.com.devsrsouza.kotlinbukkitapi.utility.collections.*",
    "br.com.devsrsouza.kotlinbukkitapi.utility.utils.*",
    /* Controller package not needed */
    "br.com.devsrsouza.kotlinbukkitapi.command.*",
    "br.com.devsrsouza.kotlinbukkitapi.command.arguments.*",
    "br.com.devsrsouza.kotlinbukkitapi.command.KCommand",
    "br.com.devsrsouza.kotlinbukkitapi.command.ExecutorBlock",
    "br.com.devsrsouza.kotlinbukkitapi.command.Executor",
    "br.com.devsrsouza.kotlinbukkitapi.command.CommandException",
    "br.com.devsrsouza.kotlinbukkitapi.command.TabCompleter",
    "br.com.devsrsouza.kotlinbukkitapi.command.TabCompleterBlock",
    "br.com.devsrsouza.kotlinbukkitapi.command.ExecutorPlayerBlock",

    "br.com.devsrsouza.kotlinbukkitapi.menu.*",
    "br.com.devsrsouza.kotlinbukkitapi.menu.slot.*",
    "br.com.devsrsouza.kotlinbukkitapi.dsl.menu.*",
    "br.com.devsrsouza.kotlinbukkitapi.dsl.menu.pagination.*",
    "br.com.devsrsouza.kotlinbukkitapi.dsl.menu.pagination.slot.*",
    "br.com.devsrsouza.kotlinbukkitapi.dsl.menu.slot.*",

    "br.com.devsrsouza.kotlinbukkitapi.scoreboard.*",

    "br.com.devsrsouza.kotlinbukkitapi.extensions.*",

    "br.com.devsrsouza.kotlinbukkitapi.coroutines.extensions.*",

    /* Flow Event ignored because already have an implementation at Bukkript */
)

val kotlinBukkitAPIPluginsImports = listOf(
    "br.com.devsrsouza.kotlinbukkitapi.plugins.placeholderapi.*",
    "br.com.devsrsouza.kotlinbukkitapi.plugins.vault.*",
    "br.com.devsrsouza.kotlinbukkitapi.plugins.bossbarapi.*",
    "br.com.devsrsouza.kotlinbukkitapi.plugins.hologramapi.*",
    "br.com.devsrsouza.kotlinbukkitapi.plugins.dvdwplaceholderapi.*",
    "br.com.devsrsouza.kotlinbukkitapi.plugins.worldedit.*",
    "br.com.devsrsouza.kotlinbukkitapi.plugins.viaversion.*",
)

val kotlinBukkitAPIExposedImports = listOf(
    "br.com.devsrsouza.kotlinbukkitapi.exposed.*",
    "br.com.devsrsouza.kotlinbukkitapi.exposed.delegate.*",
)

val bukkriptImports = listOf(
    "br.com.devsrsouza.bukkript.script.definition.annotation.*",
    "br.com.devsrsouza.bukkript.script.definition.api.*",
    "br.com.devsrsouza.bukkript.script.definition.api.architecture.*",
)

val kotlinImports = listOf(
    "kotlin.time.*",
    "kotlin.math.*",
)

val javaImports = listOf(
    "java.util.*",
    "java.util.concurrent.*",
    "java.io.*",
    // "java.lang.*",
)

val kotlinCoroutinesImports = listOf(
    "kotlinx.coroutines.*",
    "kotlinx.coroutines.flow.*",
    "kotlinx.coroutines.channels.*",
    "kotlinx.coroutines.selects.*",
)

val scriptingImports = listOf(
    "kotlin.script.experimental.dependencies.DependsOn",
    "kotlin.script.experimental.dependencies.Repository",
)
