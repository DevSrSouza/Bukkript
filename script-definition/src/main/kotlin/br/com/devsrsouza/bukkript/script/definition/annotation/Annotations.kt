package br.com.devsrsouza.bukkript.script.definition.annotation

import br.com.devsrsouza.bukkript.script.definition.api.LogLevel

/**
 * Define the description of the Script
 */
@Target(AnnotationTarget.FILE)
@Retention(AnnotationRetention.SOURCE)
annotation class Script(
    val version: String = "",
    val logLevel: LogLevel = LogLevel.INFO,
    val author: String = "",
    //val authors: Array<String> = [],
    val website: String = ""
    //val depend: Array<String> = [],
    //val pluginDepend: Array<String> = []
)

/**
 * Usage to Import and Depend of other Script.
 */
@Target(AnnotationTarget.FILE)
@Retention(AnnotationRetention.RUNTIME)
annotation class Import(
    vararg val script: String
)

/**
 * Usage to Depend of a specific plugin.
 */
@Target(AnnotationTarget.FILE)
@Retention(AnnotationRetention.RUNTIME)
annotation class DependPlugin(
    vararg val plugin: String
)

/**
 * Usage to Add a external dependency in your script.
 */
@Target(AnnotationTarget.FILE)
@Retention(AnnotationRetention.SOURCE)
@Repeatable
annotation class Maven(
    val dependency: String
)

/**
 * Usage to Add a new maven repository to be resolved when using [Maven] annotation.
 */
@Target(AnnotationTarget.FILE)
@Retention(AnnotationRetention.SOURCE)
@Repeatable
annotation class MavenRepository(
    val url: String
)