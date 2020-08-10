package br.com.devsrsouza.bukkript.plugin.manager.logging

import br.com.devsrsouza.bukkript.script.definition.api.LogLevel
fun logFormatterInterceptor(scriptName: String, level: LogLevel, message: String): String? {
    return "$scriptName [$level]: $message"
}