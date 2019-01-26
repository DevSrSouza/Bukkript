package br.com.devsrsouza.bukkript.api

import br.com.devsrsouza.bukkript.api.script.loader.BukkriptScriptLoader
import java.io.File

interface BukkriptAPI {
    val SCRIPT_DIR: File
    val CACHE_DIR: File
    val LOADER: BukkriptScriptLoader
}