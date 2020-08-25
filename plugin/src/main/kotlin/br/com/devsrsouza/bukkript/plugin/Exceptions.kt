package br.com.devsrsouza.bukkript.plugin

const val SERVER_NOT_SUPPORTED_MESSAGE = "Bukkript do not support CraftBukkit RAW server, does try Spigot or any fork of it like PaperMc."

abstract class BukkriptException(message: String) : Exception(message)

class ServerNotSupportedException : BukkriptException(SERVER_NOT_SUPPORTED_MESSAGE)