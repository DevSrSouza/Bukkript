package br.com.devsrsouza.bukkript.script.definition.compiler

import br.com.devsrsouza.bukkript.script.definition.*
import br.com.devsrsouza.bukkript.script.definition.annotation.*
import br.com.devsrsouza.bukkript.script.definition.classpath.wholeClassloaderByExcluding
import br.com.devsrsouza.bukkript.script.definition.dependencies.ignoredPluginDependencies
import br.com.devsrsouza.bukkript.script.definition.resolver.resolveScriptAnnotation
import br.com.devsrsouza.bukkript.script.definition.resolver.resolveScriptStaticDependencies
import java.io.File
import kotlin.script.experimental.api.*
import kotlin.script.experimental.dependencies.DependsOn
import kotlin.script.experimental.dependencies.Repository
import kotlin.script.experimental.jvm.dependenciesFromClassContext
import kotlin.script.experimental.jvm.jvm
import kotlin.script.experimental.jvm.updateClasspath
import kotlin.script.experimental.jvm.util.scriptCompilationClasspathFromContext

class BukkriptScriptCompilationConfiguration : ScriptCompilationConfiguration({
    defaultImports(bukkitImports + bukkriptImports + kotlinBukkitAPICoreImports
            + kotlinBukkitAPIExposedImports + kotlinBukkitAPIPluginsImports
            + kotlinImports + javaImports + kotlinCoroutinesImports + scriptingImports
    )
    jvm {
        updateClasspath(wholeClassloaderByExcluding())
        compilerOptions(
            "-Xopt-in=kotlin.time.ExperimentalTime,kotlin.ExperimentalStdlibApi,kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-jvm-target", "1.8",
        )
    }
    refineConfiguration {
        beforeCompiling(handler = ::resolveScriptStaticDependencies)
        onAnnotations(
            listOf(Script::class, DependPlugin::class, Import::class, DependsOn::class, Repository::class),
            handler = ::resolveScriptAnnotation
        )
    }
    ide {
        acceptedLocations(ScriptAcceptedLocation.Everywhere)
    }
})

