package br.com.devsrsouza.bukkript.script.definition.compiler

import br.com.devsrsouza.bukkript.script.definition.annotation.DependPlugin
import br.com.devsrsouza.bukkript.script.definition.annotation.Import
import br.com.devsrsouza.bukkript.script.definition.annotation.Script
import br.com.devsrsouza.bukkript.script.definition.bukkitImports
import br.com.devsrsouza.bukkript.script.definition.bukkriptImports
import br.com.devsrsouza.bukkript.script.definition.javaImports
import br.com.devsrsouza.bukkript.script.definition.kotlinBukkitAPICoreImports
import br.com.devsrsouza.bukkript.script.definition.kotlinBukkitAPIExposedImports
import br.com.devsrsouza.bukkript.script.definition.kotlinBukkitAPIPluginsImports
import br.com.devsrsouza.bukkript.script.definition.kotlinCoroutinesImports
import br.com.devsrsouza.bukkript.script.definition.kotlinImports
import br.com.devsrsouza.bukkript.script.definition.resolver.resolveScriptAnnotation
import br.com.devsrsouza.bukkript.script.definition.resolver.resolveScriptStaticDependencies
import br.com.devsrsouza.bukkript.script.definition.scriptingImports
import java.io.File
import kotlin.script.experimental.api.ScriptAcceptedLocation
import kotlin.script.experimental.api.ScriptCompilationConfiguration
import kotlin.script.experimental.api.acceptedLocations
import kotlin.script.experimental.api.compilerOptions
import kotlin.script.experimental.api.defaultImports
import kotlin.script.experimental.api.ide
import kotlin.script.experimental.api.refineConfiguration
import kotlin.script.experimental.dependencies.DependsOn
import kotlin.script.experimental.dependencies.Repository
import kotlin.script.experimental.jvm.dependenciesFromClassContext
import kotlin.script.experimental.jvm.jdkHome
import kotlin.script.experimental.jvm.jvm

class BukkriptScriptCompilationConfiguration : ScriptCompilationConfiguration({
    defaultImports(
        bukkitImports + bukkriptImports + kotlinBukkitAPICoreImports +
            kotlinBukkitAPIExposedImports + kotlinBukkitAPIPluginsImports +
            kotlinImports + javaImports + kotlinCoroutinesImports + scriptingImports,
    )
    jvm {
        jdkHome(File("/Users/gabriel/.asdf/installs/java/zulu-8.70.0.23"))
        // jdkHome(File("/Users/gabriel/.asdf/installs/java/temurin-11.0.19+7"))

        dependenciesFromClassContext(BukkriptScriptCompilationConfiguration::class, wholeClasspath = true)
        compilerOptions(
            // "-Xopt-in=kotlin.time.ExperimentalTime,kotlin.ExperimentalStdlibApi,kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-jvm-target",
            "17",
            "-api-version",
            "1.8",
        )
    }
    refineConfiguration {
        beforeCompiling(handler = ::resolveScriptStaticDependencies)
        onAnnotations(
            listOf(Script::class, DependPlugin::class, Import::class, DependsOn::class, Repository::class),
            handler = ::resolveScriptAnnotation,
        )
    }
    ide {
        acceptedLocations(ScriptAcceptedLocation.Everywhere)
    }
})
