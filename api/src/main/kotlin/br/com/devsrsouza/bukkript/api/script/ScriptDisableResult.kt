package br.com.devsrsouza.bukkript.api.script

import java.io.File

sealed class ScriptDisableResult(val script: File, val dependencies: List<File>) {

    class Sucess(script: File, dependencies: List<File>)
        : ScriptDisableResult(script, dependencies)

    class Failure(script: File, dependencies: List<File>, val reason: FailReason)
        : ScriptDisableResult(script, dependencies)
}

enum class FailReason { NOT_FOUND, HAVE_DEPENDENCIES }
