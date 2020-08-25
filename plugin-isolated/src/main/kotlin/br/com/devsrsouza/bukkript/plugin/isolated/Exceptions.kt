package br.com.devsrsouza.bukkript.plugin.isolated

import br.com.devsrsouza.bukkript.plugin.BukkriptException
import java.io.File

class ScriptNotFoundException(message: String, val scriptName: String) : BukkriptException(message)

class ScriptInvalidStateException(message: String, val state: String) : BukkriptException(message)

class RetrieveScriptDefinitionException(message: String, val scriptName: String) : BukkriptException(message)

class ScriptFileDoesNotExistException(message: String, val scriptName: String, val file: File) : BukkriptException(message)