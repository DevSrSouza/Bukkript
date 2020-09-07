package br.com.devsrsouza.bukkript.script.host.relocation

import java.io.File
import java.util.*

/**
 * This is a stripped down version of org.codehaus.plexus.util.SelectorUtils for
 * use in [Relocation].
 *
 * @author Arnout J. Kuiper [ajkuiper@wxs.nl](mailto:ajkuiper@wxs.nl)
 * @author Magesh Umasankar
 * @author [Bruce Atherton](mailto:bruce@callenish.com)
 */
internal object SelectorUtils {
    private const val PATTERN_HANDLER_PREFIX = "["
    private const val PATTERN_HANDLER_SUFFIX = "]"
    private const val REGEX_HANDLER_PREFIX = "%regex" + PATTERN_HANDLER_PREFIX
    private const val ANT_HANDLER_PREFIX = "%ant" + PATTERN_HANDLER_PREFIX
    private fun isAntPrefixedPattern(pattern: String): Boolean {
        return pattern.length > ANT_HANDLER_PREFIX.length + PATTERN_HANDLER_SUFFIX.length + 1 && pattern.startsWith(
            ANT_HANDLER_PREFIX
        ) && pattern.endsWith(PATTERN_HANDLER_SUFFIX)
    }

    // When str starts with a File.separator, pattern has to start with a File.separator.
    // When pattern starts with a File.separator, str has to start with a File.separator.
    private fun separatorPatternStartSlashMismatch(pattern: String, str: String, separator: String): Boolean {
        return str.startsWith(separator) != pattern.startsWith(separator)
    }

    fun matchPath(pattern: String, str: String, isCaseSensitive: Boolean): Boolean {
        return matchPath(pattern, str, File.separator, isCaseSensitive)
    }

    private fun matchPath(pattern: String, str: String, separator: String, isCaseSensitive: Boolean): Boolean {
        var pattern = pattern
        return if (isRegexPrefixedPattern(pattern)) {
            pattern = pattern.substring(REGEX_HANDLER_PREFIX.length, pattern.length - PATTERN_HANDLER_SUFFIX.length)
            str.matches(pattern.toRegex())
        } else {
            if (isAntPrefixedPattern(pattern)) {
                pattern = pattern.substring(ANT_HANDLER_PREFIX.length, pattern.length - PATTERN_HANDLER_SUFFIX.length)
            }
            matchAntPathPattern(pattern, str, separator, isCaseSensitive)
        }
    }

    private fun isRegexPrefixedPattern(pattern: String): Boolean {
        return pattern.length > REGEX_HANDLER_PREFIX.length + PATTERN_HANDLER_SUFFIX.length + 1 && pattern.startsWith(
            REGEX_HANDLER_PREFIX
        ) && pattern.endsWith(PATTERN_HANDLER_SUFFIX)
    }

    private fun matchAntPathPattern(
        pattern: String,
        str: String,
        separator: String,
        isCaseSensitive: Boolean
    ): Boolean {
        if (separatorPatternStartSlashMismatch(pattern, str, separator)) {
            return false
        }
        val patDirs = tokenizePathToString(pattern, separator)
        val strDirs = tokenizePathToString(str, separator)
        return matchAntPathPattern(patDirs, strDirs, isCaseSensitive)
    }

    private fun matchAntPathPattern(patDirs: Array<String>, strDirs: Array<String>, isCaseSensitive: Boolean): Boolean {
        var patIdxStart = 0
        var patIdxEnd = patDirs.size - 1
        var strIdxStart = 0
        var strIdxEnd = strDirs.size - 1

        // up to first '**'
        while (patIdxStart <= patIdxEnd && strIdxStart <= strIdxEnd) {
            val patDir = patDirs[patIdxStart]
            if (patDir == "**") {
                break
            }
            if (!match(patDir, strDirs[strIdxStart], isCaseSensitive)) {
                return false
            }
            patIdxStart++
            strIdxStart++
        }
        if (strIdxStart > strIdxEnd) {
            // String is exhausted
            for (i in patIdxStart..patIdxEnd) {
                if (patDirs[i] != "**") {
                    return false
                }
            }
            return true
        } else {
            if (patIdxStart > patIdxEnd) {
                // String not exhausted, but pattern is. Failure.
                return false
            }
        }

        // up to last '**'
        while (patIdxStart <= patIdxEnd && strIdxStart <= strIdxEnd) {
            val patDir = patDirs[patIdxEnd]
            if (patDir == "**") {
                break
            }
            if (!match(patDir, strDirs[strIdxEnd], isCaseSensitive)) {
                return false
            }
            patIdxEnd--
            strIdxEnd--
        }
        if (strIdxStart > strIdxEnd) {
            // String is exhausted
            for (i in patIdxStart..patIdxEnd) {
                if (patDirs[i] != "**") {
                    return false
                }
            }
            return true
        }
        while (patIdxStart != patIdxEnd && strIdxStart <= strIdxEnd) {
            var patIdxTmp = -1
            for (i in patIdxStart + 1..patIdxEnd) {
                if (patDirs[i] == "**") {
                    patIdxTmp = i
                    break
                }
            }
            if (patIdxTmp == patIdxStart + 1) {
                // '**/**' situation, so skip one
                patIdxStart++
                continue
            }
            // Find the pattern between padIdxStart & padIdxTmp in str between
            // strIdxStart & strIdxEnd
            val patLength = patIdxTmp - patIdxStart - 1
            val strLength = strIdxEnd - strIdxStart + 1
            var foundIdx = -1
            strLoop@ for (i in 0..strLength - patLength) {
                for (j in 0 until patLength) {
                    val subPat = patDirs[patIdxStart + j + 1]
                    val subStr = strDirs[strIdxStart + i + j]
                    if (!match(subPat, subStr, isCaseSensitive)) {
                        continue@strLoop
                    }
                }
                foundIdx = strIdxStart + i
                break
            }
            if (foundIdx == -1) {
                return false
            }
            patIdxStart = patIdxTmp
            strIdxStart = foundIdx + patLength
        }
        for (i in patIdxStart..patIdxEnd) {
            if (patDirs[i] != "**") {
                return false
            }
        }
        return true
    }

    private fun match(pattern: String, str: String, isCaseSensitive: Boolean): Boolean {
        val patArr = pattern.toCharArray()
        val strArr = str.toCharArray()
        return match(patArr, strArr, isCaseSensitive)
    }

    private fun match(patArr: CharArray, strArr: CharArray, isCaseSensitive: Boolean): Boolean {
        var patIdxStart = 0
        var patIdxEnd = patArr.size - 1
        var strIdxStart = 0
        var strIdxEnd = strArr.size - 1
        var ch: Char
        var containsStar = false
        for (aPatArr in patArr) {
            if (aPatArr == '*') {
                containsStar = true
                break
            }
        }
        if (!containsStar) {
            // No '*'s, so we make a shortcut
            if (patIdxEnd != strIdxEnd) {
                return false // Pattern and string do not have the same size
            }
            for (i in 0..patIdxEnd) {
                ch = patArr[i]
                if (ch != '?' && !equals(ch, strArr[i], isCaseSensitive)) {
                    return false // Character mismatch
                }
            }
            return true // String matches against pattern
        }
        if (patIdxEnd == 0) {
            return true // Pattern contains only '*', which matches anything
        }

        // Process characters before first star
        while (patArr[patIdxStart].also { ch = it } != '*' && strIdxStart <= strIdxEnd) {
            if (ch != '?' && !equals(ch, strArr[strIdxStart], isCaseSensitive)) {
                return false // Character mismatch
            }
            patIdxStart++
            strIdxStart++
        }
        if (strIdxStart > strIdxEnd) {
            // All characters in the string are used. Check if only '*'s are
            // left in the pattern. If so, we succeeded. Otherwise failure.
            for (i in patIdxStart..patIdxEnd) {
                if (patArr[i] != '*') {
                    return false
                }
            }
            return true
        }

        // Process characters after last star
        while (patArr[patIdxEnd].also { ch = it } != '*' && strIdxStart <= strIdxEnd) {
            if (ch != '?' && !equals(ch, strArr[strIdxEnd], isCaseSensitive)) {
                return false // Character mismatch
            }
            patIdxEnd--
            strIdxEnd--
        }
        if (strIdxStart > strIdxEnd) {
            // All characters in the string are used. Check if only '*'s are
            // left in the pattern. If so, we succeeded. Otherwise failure.
            for (i in patIdxStart..patIdxEnd) {
                if (patArr[i] != '*') {
                    return false
                }
            }
            return true
        }

        // process pattern between stars. padIdxStart and patIdxEnd point
        // always to a '*'.
        while (patIdxStart != patIdxEnd && strIdxStart <= strIdxEnd) {
            var patIdxTmp = -1
            for (i in patIdxStart + 1..patIdxEnd) {
                if (patArr[i] == '*') {
                    patIdxTmp = i
                    break
                }
            }
            if (patIdxTmp == patIdxStart + 1) {
                // Two stars next to each other, skip the first one.
                patIdxStart++
                continue
            }
            // Find the pattern between padIdxStart & padIdxTmp in str between
            // strIdxStart & strIdxEnd
            val patLength = patIdxTmp - patIdxStart - 1
            val strLength = strIdxEnd - strIdxStart + 1
            var foundIdx = -1
            strLoop@ for (i in 0..strLength - patLength) {
                for (j in 0 until patLength) {
                    ch = patArr[patIdxStart + j + 1]
                    if (ch != '?' && !equals(ch, strArr[strIdxStart + i + j], isCaseSensitive)) {
                        continue@strLoop
                    }
                }
                foundIdx = strIdxStart + i
                break
            }
            if (foundIdx == -1) {
                return false
            }
            patIdxStart = patIdxTmp
            strIdxStart = foundIdx + patLength
        }

        // All characters in the string are used. Check if only '*'s are left
        // in the pattern. If so, we succeeded. Otherwise failure.
        for (i in patIdxStart..patIdxEnd) {
            if (patArr[i] != '*') {
                return false
            }
        }
        return true
    }

    /**
     * Tests whether two characters are equal.
     */
    private fun equals(c1: Char, c2: Char, isCaseSensitive: Boolean): Boolean {
        if (c1 == c2) {
            return true
        }
        if (!isCaseSensitive) {
            // NOTE: Try both upper case and lower case as done by String.equalsIgnoreCase()
            if (Character.toUpperCase(c1) == Character.toUpperCase(c2)
                || Character.toLowerCase(c1) == Character.toLowerCase(c2)
            ) {
                return true
            }
        }
        return false
    }

    private fun tokenizePathToString(path: String, separator: String): Array<String> {
        val ret: MutableList<String> = ArrayList()
        val st = StringTokenizer(path, separator)
        while (st.hasMoreTokens()) {
            ret.add(st.nextToken())
        }
        return ret.toTypedArray()
    }
}
