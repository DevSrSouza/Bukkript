package br.com.devsrsouza.bukkript.script.definition.api

import br.com.devsrsouza.bukkript.script.definition.BukkriptScript
import br.com.devsrsouza.kotlinbukkitapi.coroutines.BukkitDispatchers

val BukkriptScript.BukkitDispatchers get() = plugin.BukkitDispatchers
