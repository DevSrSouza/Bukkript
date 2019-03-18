package br.com.devsrsouza.bukkript.script.annotations

@Target(AnnotationTarget.FILE)
@Retention(AnnotationRetention.SOURCE)
annotation class Script(
    val name: String = "",
    val version: String = "",
    val author: String = "",
    //val authors: Array<String> = [],
    val website: String = ""
    //val depend: Array<String> = [],
    //val pluginDepend: Array<String> = []
)

@Target(AnnotationTarget.FILE)
@Retention(AnnotationRetention.RUNTIME)
annotation class Import(
    vararg val script: String
)

@Target(AnnotationTarget.FILE)
@Retention(AnnotationRetention.RUNTIME)
annotation class DependPlugin(
    vararg val plugin: String
)