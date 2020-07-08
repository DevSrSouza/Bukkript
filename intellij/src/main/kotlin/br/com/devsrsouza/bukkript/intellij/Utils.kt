package br.com.devsrsouza.bukkript.intellij

import br.com.devsrsouza.bukkript.script.definition.BUKKRIPT_EXTENSION

fun String.isBukkriptScript() = endsWith(".$BUKKRIPT_EXTENSION", true)