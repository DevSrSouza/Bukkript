package br.com.devsrsouza.bukkript.script.definition.resolver

import java.io.File
import kotlin.script.experimental.api.ExternalSourceCode
import kotlin.script.experimental.api.SourceCode
import kotlin.script.experimental.host.FileBasedScriptSource

fun isPackageAvailable(fqnPackage: String) = Package.getPackage(fqnPackage) != null

val SourceCode.finalFile: File
    get() = when (this) {
        is FileBasedScriptSource -> file
        is ExternalSourceCode -> File(externalLocation.file)
        else -> throw RuntimeException("this type of script is not supported, use a File based Script.")
    }
