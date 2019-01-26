package br.com.devsrsouza.bukkript.api

interface DependecyImport {
    val name: String
    fun imports(): List<String>
}