package br.com.devsrsouza.bukkript.script.host.cache

import java.security.MessageDigest
import kotlin.script.experimental.api.SourceCode

fun SourceCode.generateMD5(): String {
    val digest = MessageDigest.getInstance("MD5").apply {
        update(text.toByteArray())
    }

    return digest.digest().toHexString()
}

fun ByteArray.toHexString() : String {
    return this.joinToString("") {
        java.lang.String.format("%02x", it)
    }
}