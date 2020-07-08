package br.com.devsrsouza.bukkript.script.definition.error

import kotlin.script.experimental.api.ResultWithDiagnostics
import kotlin.script.experimental.api.ScriptDiagnostic

sealed class DiagnosticResult<T> {
    class Success<T>(val value: T) : DiagnosticResult<T>()
    class Fail<T>(val diagnostic: ScriptDiagnostic) : DiagnosticResult<T>()
}

inline fun <T> T.requireOrFail(
    message: (T) -> String = { "Script fail diagnostic" },
    requirement: (T) -> Boolean
): DiagnosticResult<T> {
    return if(requirement(this))
        DiagnosticResult.Success(this)
    else
        DiagnosticResult.Fail(
            ScriptDiagnostic( // TODO: find a way to get the current source location!
                 // CODE?
                message(this),
                ScriptDiagnostic.Severity.FATAL
            )
        )
}
