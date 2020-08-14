package br.com.devsrsouza.bukkript.plugin.exceptions

const val SERVER_NOT_SUPPORTED_MESSAGE = "Bukkript do not support CraftBukkit RAW server, tries Spigot or any fork of it like PaperMc."

class ServerNotSupportedException : Exception(SERVER_NOT_SUPPORTED_MESSAGE)