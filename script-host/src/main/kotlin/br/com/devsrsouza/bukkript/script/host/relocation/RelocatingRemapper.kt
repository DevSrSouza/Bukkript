package br.com.devsrsouza.bukkript.script.host.relocation

import org.objectweb.asm.commons.Remapper
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Remaps class names and types using defined [Relocation] rules.
 */
internal class RelocatingRemapper(
    private val rules: Collection<Relocation>
) : Remapper() {

    override fun mapValue(value: Any): Any {
        if (value is String) {
            val relocatedName = relocate(value, true)
            if (relocatedName != null) {
                return relocatedName
            }
        }
        return super.mapValue(value)
    }

    override fun map(name: String): String {
        val relocatedName = relocate(name, false)
        return relocatedName ?: super.map(name)
    }

    private fun relocate(name: String, isClass: Boolean): String? {
        var name: String = name
        var prefix = ""
        var suffix = ""
        val m: Matcher = CLASS_PATTERN.matcher(name)
        if (m.matches()) {
            prefix = m.group(1).toString() + "L"
            suffix = ";"
            name = m.group(2)
        }
        for (r in rules) {
            if (isClass && r.canRelocateClass(name)) {
                return prefix + r.relocateClass(name) + suffix
            } else if (r.canRelocatePath(name)) {
                return prefix + r.relocatePath(name) + suffix
            }
        }
        return null
    }

    companion object {
        private val CLASS_PATTERN: Pattern = Pattern.compile("(\\[*)?L(.+);")
    }

}