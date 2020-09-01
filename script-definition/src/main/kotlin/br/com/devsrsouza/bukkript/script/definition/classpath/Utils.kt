package br.com.devsrsouza.bukkript.script.definition.classpath

import java.net.URL

internal fun URL.toFileOrNull() = try {
    java.io.File(toURI().schemeSpecificPart)
} catch (e: java.net.URISyntaxException) {
    if (protocol != "file") null
    else java.io.File(file)
}