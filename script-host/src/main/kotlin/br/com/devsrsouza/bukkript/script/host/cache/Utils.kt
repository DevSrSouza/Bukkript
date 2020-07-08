package br.com.devsrsouza.bukkript.script.host.cache

import br.com.devsrsouza.bukkript.script.definition.BUKKRIPT_EXTENSION
import java.io.File
import java.security.MessageDigest
import kotlin.script.experimental.api.SourceCode

fun SourceCode.generateMD5(
    //configuration: ScriptCompilationConfiguration
): String {
    val digest = MessageDigest.getInstance("MD5").apply {
        update(text.toByteArray())
    }

    return digest.digest().toHexString()
}

fun File.scriptName(cacheDir: File) = relativeTo(cacheDir).path.substringBeforeLast(".")
fun File.scriptSimpleName() = relativeTo(parentFile).path.substringBeforeLast(".")
//fun File.scriptDataFolder(cacheDir: File) = File(api.DATA_DIR, scriptName(api))

fun ByteArray.toHexString() : String {
    return this.joinToString("") {
        java.lang.String.format("%02x", it)
    }
}