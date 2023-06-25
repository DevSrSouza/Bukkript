package br.com.devsrsouza.bukkript.plugin

import java.io.File

const val SERVER_NOT_SUPPORTED_MESSAGE = "Bukkript do not support CraftBukkit RAW server, does try Spigot or any fork of it like PaperMc."

abstract class BukkriptException(message: String) : Exception(message)

class ServerNotSupportedException : BukkriptException(SERVER_NOT_SUPPORTED_MESSAGE)

class ScriptNotFoundException(message: String, val scriptName: String) : BukkriptException(message)

class ScriptInvalidStateException(message: String, val state: String) : BukkriptException(message)

class RetrieveScriptDefinitionException(message: String, val scriptName: String) : BukkriptException(message)

class ScriptFileDoesNotExistException(message: String, val scriptName: String, val file: File) : BukkriptException(message)
