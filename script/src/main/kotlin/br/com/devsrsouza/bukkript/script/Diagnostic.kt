package br.com.devsrsouza.bukkript.script

import kotlin.script.dependencies.ScriptContents
import kotlin.script.dependencies.ScriptDependenciesResolver
import kotlin.script.experimental.api.ScriptConfigurationRefinementContext
import kotlin.script.experimental.api.ScriptDiagnostic
import kotlin.script.experimental.jvm.compat.mapLegacyDiagnosticSeverity
import kotlin.script.experimental.jvm.compat.mapLegacyScriptPosition

class ReportFunction(val context: ScriptConfigurationRefinementContext, val diagnostics: MutableList<ScriptDiagnostic>) : (ScriptDependenciesResolver.ReportSeverity, String, ScriptContents.Position?) -> Unit {
    override fun invoke(severity: ScriptDependenciesResolver.ReportSeverity,
                        message: String,
                        position: ScriptContents.Position?) {
        diagnostics.add(
            ScriptDiagnostic(
                message,
                mapLegacyDiagnosticSeverity(severity),
                context.script.locationId,
                mapLegacyScriptPosition(position)
            )
        )
    }
}