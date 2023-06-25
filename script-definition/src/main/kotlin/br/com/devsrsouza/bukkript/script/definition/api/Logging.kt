package br.com.devsrsouza.bukkript.script.definition.api

import br.com.devsrsouza.bukkript.script.definition.BukkriptScript

enum class LogLevel(val level: Int) {
    DISABLED(0),
    ERROR(1),
    WARN(2),
    INFO(3),
    DEBUG(4),
    VERBOSE(5),
}

inline fun BukkriptScript.info(message: () -> String) {
    if (description.logLevel.level >= LogLevel.INFO.level) {
        log(LogLevel.INFO, message())
    }
}

inline fun BukkriptScript.warn(message: () -> String) {
    if (description.logLevel.level >= LogLevel.WARN.level) {
        log(LogLevel.WARN, message())
    }
}

inline fun BukkriptScript.debug(message: () -> String) {
    if (description.logLevel.level >= LogLevel.DEBUG.level) {
        log(LogLevel.DEBUG, message())
    }
}

inline fun BukkriptScript.verbose(message: () -> String) {
    if (description.logLevel.level >= LogLevel.VERBOSE.level) {
        log(LogLevel.VERBOSE, message())
    }
}

inline fun BukkriptScript.error(error: () -> Throwable) {
    if (description.logLevel.level >= LogLevel.ERROR.level) {
        log(LogLevel.ERROR, error().toString())
    }
}
