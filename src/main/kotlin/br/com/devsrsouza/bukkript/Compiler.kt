package br.com.devsrsouza.bukkript.compiler

import br.com.devsrsouza.bukkript.Bukkript
import br.com.devsrsouza.bukkript.script.BukkriptScriptDefinition
import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.messages.MessageRenderer
import org.jetbrains.kotlin.cli.common.messages.PrintingMessageCollector
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinToJVMBytecodeCompiler
import org.jetbrains.kotlin.codegen.GeneratedClassLoader
import org.jetbrains.kotlin.codegen.getClassFiles
import org.jetbrains.kotlin.com.intellij.openapi.util.Disposer
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.JVMConfigurationKeys
import org.jetbrains.kotlin.config.JvmTarget
import org.jetbrains.kotlin.config.addKotlinSourceRoot
import org.jetbrains.kotlin.name.FqNameUnsafe
import org.jetbrains.kotlin.resolve.BindingContext
import java.io.File
import kotlin.reflect.jvm.jvmName

fun compileScripts(folder: File) : Map<String, Class<*>> {

    val disposal = Disposer.newDisposable()
    val configuration = CompilerConfiguration().apply {
        add(JVMConfigurationKeys.SCRIPT_DEFINITIONS, BukkriptScriptDefinition)
        addKotlinSourceRoot(folder.absolutePath)
        put(JVMConfigurationKeys.JVM_TARGET, JvmTarget.JVM_1_8)
        put(JVMConfigurationKeys.RETAIN_OUTPUT_IN_MEMORY, true)
        put(
            CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY,
            PrintingMessageCollector(System.err, MessageRenderer.WITHOUT_PATHS, false)
        )
    }

    val environment = KotlinCoreEnvironment.createForProduction(disposal,
        configuration, EnvironmentConfigFiles.JVM_CONFIG_FILES)

    val finalState = KotlinToJVMBytecodeCompiler.analyzeAndGenerate(environment)
    if (finalState === null) {
        return emptyMap()
    }

    val compilerClassLoader = GeneratedClassLoader(finalState.factory, Bukkript::class.java.classLoader)

    return finalState.factory.getClassFiles().toList()
        .map { it.relativePath.removeSuffix(".class").replace("/", ".") }
        .mapNotNull { finalState.bindingContext.get(BindingContext.FQNAME_TO_CLASS_DESCRIPTOR, FqNameUnsafe(it.replace("$", "."))) }
        .associateBy { finalState.typeMapper.mapClass(it).className }
        .map { compilerClassLoader.loadClass(it.key) }
        .associate { it.simpleName to it }
}