package br.com.devsrsouza.bukkript.script.definition.compiler

import br.com.devsrsouza.bukkript.script.definition.*
import br.com.devsrsouza.bukkript.script.definition.annotation.*
import br.com.devsrsouza.bukkript.script.definition.resolver.resolveScriptAnnotation
import br.com.devsrsouza.bukkript.script.definition.resolver.resolveScriptStaticDependencies
import kotlin.script.experimental.api.*
import kotlin.script.experimental.jvm.dependenciesFromClassContext
import kotlin.script.experimental.jvm.jvm

object BukkriptScriptCompilationConfiguration : ScriptCompilationConfiguration({
    defaultImports(bukkitImports + bukkriptImports + kotlinBukkitAPICoreImports
            + kotlinBukkitAPIAttributeStorageImports + kotlinBukkitAPIPluginsImports)
    jvm {
        dependenciesFromClassContext(BukkriptScriptCompilationConfiguration::class, wholeClasspath = true)
        compilerOptions("-jvm-target", "1.8")
    }
    refineConfiguration {
        beforeCompiling(handler = ::resolveScriptStaticDependencies)
        onAnnotations(
            listOf(Script::class, DependPlugin::class, Import::class, Maven::class, MavenRepository::class),
            handler = ::resolveScriptAnnotation
        )
    }
    ide {
        acceptedLocations(ScriptAcceptedLocation.Everywhere)
    }
})

