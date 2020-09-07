package br.com.devsrsouza.bukkript.script.host.relocation

import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import java.io.IOException

object ScriptRelocate {
    fun relocate(
        className: String,
        byteCode: ByteArray
    ) = processClass(
        className,
        byteCode,
        mutableSetOf<Pair<String, String>>().apply {
            relocateKotlinBukkitAPI()
            relocateBukkript()
        }
    )

    @Throws(IOException::class)
    private fun processClass(
        name: String,
        byteCode: ByteArray,
        relocations: Set<Pair<String, String>>
    ): Pair<String, ByteArray> {
        val remapper = RelocatingRemapper(relocations.map { Relocation(it.first, it.second) })

        val classReader = ClassReader(byteCode.inputStream())
        val classWriter = ClassWriter(0)
        val classVisitor = RelocatingClassVisitor(classWriter, remapper, name)
        try {
            classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES)
        } catch (e: Throwable) {
            throw RuntimeException("Error processing class $name", e)
        }
        val renamedClass: ByteArray = classWriter.toByteArray()

        // Need to take the .class off for remapping evaluation
        val mappedName: String = remapper.map(name.substring(0, name.indexOf('.')))

        return mappedName to renamedClass
    }

    private fun MutableSet<Pair<String, String>>.relocateKotlinBukkitAPI() {
        fun kbapi(newPackage: String) = "br.com.devsrsouza.kotlinbukkitapi.libraries.${newPackage}"

        add("kotlin", kbapi("kotlin"))
        add("kotlinx", kbapi("kotlinx"))
        add("kotlin", kbapi("kotlin"))
        add("com.charleskorn.kaml", kbapi("kaml"))
        add("org.jetbrains", kbapi("jetbrains"))
        add("org.intellij", kbapi("intellij"))
        add("org.snakeyaml", kbapi("snakeyaml"))
        add("com.zaxxer.hikari", kbapi("hikari"))
        add("com.okkero", kbapi("okkero"))
    }

    private fun MutableSet<Pair<String, String>>.relocateBukkript() {
        fun bukkript(newPackage: String) = "br.com.devsrsouza.bukkript.libraries.$newPackage"

        add("org.objectweb", bukkript("objectweb"))
        add("org.apache", bukkript("apache"))
        add("gnu.trove", bukkript("trove"))
    }

    private fun MutableSet<Pair<String, String>>.add(
        first: String, seconds: String
    ) = add(Pair(first, seconds))
}