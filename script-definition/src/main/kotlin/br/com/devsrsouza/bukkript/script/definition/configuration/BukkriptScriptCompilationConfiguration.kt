package br.com.devsrsouza.bukkript.script.definition.compiler

import br.com.devsrsouza.bukkript.script.definition.*
import br.com.devsrsouza.bukkript.script.definition.annotation.*
import br.com.devsrsouza.bukkript.script.definition.classpath.classpathFromPlugins
import br.com.devsrsouza.bukkript.script.definition.classpath.wholeClassloader
import br.com.devsrsouza.bukkript.script.definition.classpath.wholeClassloaderByExcluding
import br.com.devsrsouza.bukkript.script.definition.dependencies.SPIGOT_DEPENDENCY
import br.com.devsrsouza.bukkript.script.definition.dependencies.ignoredPluginDependencies
import br.com.devsrsouza.bukkript.script.definition.resolver.isPackageAvailable
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
        updateClasspath(
            wholeClassloader() + if(isPackageAvailable(SPIGOT_DEPENDENCY.fqnPackage)) classpathFromPlugins() else emptyList<File>()
        )
        compilerOptions(
            "-Xopt-in=kotlin.time.ExperimentalTime,kotlin.ExperimentalStdlibApi,kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-jvm-target", "1.8",
        )
    }
    refineConfiguration {
        beforeParsing {
            println(it.compilationConfiguration[ScriptCompilationConfiguration.dependencies])
            it.compilationConfiguration.asSuccess()
        }
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

