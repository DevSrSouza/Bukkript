package br.com.devsrsouza.bukkript.script

import br.com.devsrsouza.bukkript.api.BukkriptAPI
import br.com.devsrsouza.bukkript.api.script.AbstractScript
import br.com.devsrsouza.bukkript.api.script.scriptExtension
import java.io.File
import kotlin.script.experimental.annotations.KotlinScript

@KotlinScript("Bukkript script", scriptExtension, BukkriptScriptConfiguration::class)
abstract class BukkriptScript(api: BukkriptAPI, dataFolder: File) : AbstractScript(api, dataFolder)