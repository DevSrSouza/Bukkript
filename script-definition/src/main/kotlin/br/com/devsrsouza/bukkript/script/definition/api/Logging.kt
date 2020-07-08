package br.com.devsrsouza.bukkript.script.definition.api

import br.com.devsrsouza.bukkript.script.definition.BukkriptScript

enum class LogLevel(val level: Int) {
    NONE(0),
    ERROR(1),
    WARN(2),
    INFO(3),
    DEBUG(4),
    VERBOSE(5)
}

inline fun BukkriptScript.info(message: () -> String) {
    if(description.logLevel.level >= LogLevel.INFO.level)
        message() // TODO: send message logic
}

inline fun BukkriptScript.warn(message: () -> String) {
    if(description.logLevel.level >= LogLevel.WARN.level)
        message() // TODO: send message logic
}

inline fun BukkriptScript.debug(message: () -> String) {
    if(description.logLevel.level >= LogLevel.DEBUG.level)
        message() // TODO: send message logic
}

inline fun BukkriptScript.verbose(message: () -> String) {
    if(description.logLevel.level >= LogLevel.VERBOSE.level)
        message() // TODO: send message logic
}

inline fun BukkriptScript.error(error: () -> Throwable) {
    if(description.logLevel.level >= LogLevel.ERROR.level)
        error() // TODO: send message logic
}
