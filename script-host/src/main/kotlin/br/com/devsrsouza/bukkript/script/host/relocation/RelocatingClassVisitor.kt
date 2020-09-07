package br.com.devsrsouza.bukkript.script.host.relocation

import org.objectweb.asm.ClassWriter
import org.objectweb.asm.commons.ClassRemapper

/**
 * A [ClassVisitor] that relocates types and names with a [RelocatingRemapper].
 */
internal class RelocatingClassVisitor(
    writer: ClassWriter,
    remapper: RelocatingRemapper,
    name: String
) : ClassRemapper(writer, remapper) {
    private val packageName: String = name.substring(0, name.lastIndexOf('/') + 1)

    override fun visitSource(source: String?, debug: String?) {
        if (source == null) {
            super.visitSource(null, debug)
            return
        }

        // visit source file name
        val name = packageName + source
        val mappedName: String = super.remapper.map(name)
        val mappedFileName = mappedName.substring(mappedName.lastIndexOf('/') + 1)
        super.visitSource(mappedFileName, debug)
    }

}