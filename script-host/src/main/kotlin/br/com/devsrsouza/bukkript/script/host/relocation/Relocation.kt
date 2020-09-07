package br.com.devsrsouza.bukkript.script.host.relocation

import java.util.*


/**
 * A relocation rule
 */
class Relocation @JvmOverloads constructor(
    pattern: String,
    relocatedPattern: String,
    private var includes: MutableCollection<String>? = null,
    private var excludes: MutableCollection<String>? = null
) {
    private val pattern: String = pattern.replace('/', '.')
    private val relocatedPattern: String = relocatedPattern.replace('/', '.')
    private val pathPattern: String = pattern.replace('.', '/')
    private val relocatedPathPattern: String = relocatedPattern.replace('.', '/')

    private fun isIncluded(path: String): Boolean {
        if (includes == null) {
            return true
        }
        for (include in includes!!) {
            if (SelectorUtils.matchPath(include, path, true)) {
                return true
            }
        }
        return false
    }

    private fun isExcluded(path: String): Boolean {
        if (excludes == null) {
            return false
        }
        for (exclude in excludes!!) {
            if (SelectorUtils.matchPath(exclude, path, true)) {
                return true
            }
        }
        return false
    }

    fun canRelocatePath(path: String): Boolean {
        var path = path
        if (path.endsWith(".class")) {
            path = path.substring(0, path.length - 6)
        }
        return if (!isIncluded(path) || isExcluded(path)) {
            false
        } else path.startsWith(pathPattern) || path.startsWith("/" + pathPattern)
    }

    fun canRelocateClass(clazz: String): Boolean {
        return clazz.indexOf('/') == -1 && canRelocatePath(clazz.replace('.', '/'))
    }

    fun relocatePath(path: String): String {
        return path.replaceFirst(pathPattern.toRegex(), relocatedPathPattern)
    }

    fun relocateClass(clazz: String): String {
        return clazz.replaceFirst(pattern.toRegex(), relocatedPattern)
    }

    companion object {
        private fun normalizePatterns(patterns: Collection<String>): MutableSet<String> {
            val normalized: MutableSet<String> = LinkedHashSet()
            for (pattern in patterns) {
                val classPattern = pattern.replace('.', '/')
                normalized.add(classPattern)
                if (classPattern.endsWith("/*")) {
                    val packagePattern = classPattern.substring(0, classPattern.lastIndexOf('/'))
                    normalized.add(packagePattern)
                }
            }
            return normalized
        }
    }
    /**
     * Creates a new relocation
     *
     * @param pattern the pattern to match
     * @param relocatedPattern the pattern to relocate to
     * @param includes a collection of patterns which this rule should specifically include
     * @param excludes a collection of patterns which this rule should specifically exclude
     */
    /**
     * Creates a new relocation with no specific includes or excludes
     *
     * @param pattern the pattern to match
     * @param relocatedPattern the pattern to relocate to
     */
    init {
        if (includes != null && !includes!!.isEmpty()) {
            this.includes = normalizePatterns(includes!!)
            this.includes!!.addAll(includes!!)
        } else {
            this.includes = null
        }
        if (excludes != null && !excludes!!.isEmpty()) {
            this.excludes = normalizePatterns(excludes!!)
            this.excludes!!.addAll(excludes!!)
        } else {
            this.excludes = null
        }
    }
}