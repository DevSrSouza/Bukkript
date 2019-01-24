package br.com.devsrsouza.bukkript.script

@Target(AnnotationTarget.FILE)
@Retention(AnnotationRetention.SOURCE)
@Repeatable
annotation class Script(
    val name: String = "",
    val version: String = "",
    val author: String = "",
    val authors: Array<String> = emptyArray(),
    val website: String = "",
    val depend: Array<String> = emptyArray(),
    val pluginDepend: Array<String> = emptyArray()
)