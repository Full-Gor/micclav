package com.micclav.keyboard.autocorrect

import com.micclav.keyboard.core.KeyboardLanguage

/**
 * Fuzzy string matching engine that handles:
 * - Edit distance (Levenshtein)
 * - Keyboard proximity (adjacent key typos)
 * - Common transpositions
 */
class FuzzyMatcher {

    fun normalizedSimilarity(a: String, b: String): Float {
        if (a.isEmpty() && b.isEmpty()) return 1f
        val distance = levenshtein(a, b)
        val maxLen = maxOf(a.length, b.length)
        return 1f - (distance.toFloat() / maxLen)
    }

    /**
     * Score based on keyboard key proximity.
     * Higher score if differences are on adjacent keys (likely typos).
     */
    fun keyboardProximityScore(input: String, candidate: String, language: KeyboardLanguage): Float {
        if (input.length != candidate.length) {
            return normalizedSimilarity(input, candidate) * 0.5f
        }

        val adjacencyMap = getAdjacencyMap(language)
        var matches = 0
        var proximityMatches = 0

        for (i in input.indices) {
            when {
                input[i] == candidate[i] -> {
                    matches++
                    proximityMatches++
                }
                adjacencyMap[input[i]]?.contains(candidate[i]) == true -> {
                    proximityMatches++
                }
            }
        }

        val exactScore = matches.toFloat() / input.length
        val proximityBonus = (proximityMatches - matches).toFloat() / input.length * 0.5f
        return exactScore + proximityBonus
    }

    private fun getAdjacencyMap(language: KeyboardLanguage): Map<Char, Set<Char>> = when (language) {
        KeyboardLanguage.FRENCH -> AZERTY_ADJACENCY
        KeyboardLanguage.ENGLISH -> QWERTY_ADJACENCY
        else -> QWERTY_ADJACENCY // fallback
    }

    private fun levenshtein(a: String, b: String): Int {
        val dp = Array(a.length + 1) { IntArray(b.length + 1) }
        for (i in 0..a.length) dp[i][0] = i
        for (j in 0..b.length) dp[0][j] = j

        for (i in 1..a.length) {
            for (j in 1..b.length) {
                val cost = if (a[i - 1] == b[j - 1]) 0 else 1
                dp[i][j] = minOf(
                    dp[i - 1][j] + 1,      // deletion
                    dp[i][j - 1] + 1,      // insertion
                    dp[i - 1][j - 1] + cost // substitution
                )
                // Transposition (Damerau-Levenshtein)
                if (i > 1 && j > 1 && a[i - 1] == b[j - 2] && a[i - 2] == b[j - 1]) {
                    dp[i][j] = minOf(dp[i][j], dp[i - 2][j - 2] + cost)
                }
            }
        }
        return dp[a.length][b.length]
    }

    companion object {
        // AZERTY keyboard adjacency (French)
        private val AZERTY_ADJACENCY = mapOf(
            'a' to setOf('z', 'q'),
            'z' to setOf('a', 'e', 'q', 's'),
            'e' to setOf('z', 'r', 's', 'd'),
            'r' to setOf('e', 't', 'd', 'f'),
            't' to setOf('r', 'y', 'f', 'g'),
            'y' to setOf('t', 'u', 'g', 'h'),
            'u' to setOf('y', 'i', 'h', 'j'),
            'i' to setOf('u', 'o', 'j', 'k'),
            'o' to setOf('i', 'p', 'k', 'l'),
            'p' to setOf('o', 'l', 'm'),
            'q' to setOf('a', 'z', 's', 'w'),
            's' to setOf('q', 'z', 'e', 'd', 'w', 'x'),
            'd' to setOf('s', 'e', 'r', 'f', 'x', 'c'),
            'f' to setOf('d', 'r', 't', 'g', 'c', 'v'),
            'g' to setOf('f', 't', 'y', 'h', 'v', 'b'),
            'h' to setOf('g', 'y', 'u', 'j', 'b', 'n'),
            'j' to setOf('h', 'u', 'i', 'k', 'n'),
            'k' to setOf('j', 'i', 'o', 'l'),
            'l' to setOf('k', 'o', 'p', 'm'),
            'm' to setOf('l', 'p'),
            'w' to setOf('q', 's', 'x'),
            'x' to setOf('w', 's', 'd', 'c'),
            'c' to setOf('x', 'd', 'f', 'v'),
            'v' to setOf('c', 'f', 'g', 'b'),
            'b' to setOf('v', 'g', 'h', 'n'),
            'n' to setOf('b', 'h', 'j'),
        )

        // QWERTY keyboard adjacency (English)
        private val QWERTY_ADJACENCY = mapOf(
            'q' to setOf('w', 'a'),
            'w' to setOf('q', 'e', 'a', 's'),
            'e' to setOf('w', 'r', 's', 'd'),
            'r' to setOf('e', 't', 'd', 'f'),
            't' to setOf('r', 'y', 'f', 'g'),
            'y' to setOf('t', 'u', 'g', 'h'),
            'u' to setOf('y', 'i', 'h', 'j'),
            'i' to setOf('u', 'o', 'j', 'k'),
            'o' to setOf('i', 'p', 'k', 'l'),
            'p' to setOf('o', 'l'),
            'a' to setOf('q', 'w', 's', 'z'),
            's' to setOf('a', 'w', 'e', 'd', 'z', 'x'),
            'd' to setOf('s', 'e', 'r', 'f', 'x', 'c'),
            'f' to setOf('d', 'r', 't', 'g', 'c', 'v'),
            'g' to setOf('f', 't', 'y', 'h', 'v', 'b'),
            'h' to setOf('g', 'y', 'u', 'j', 'b', 'n'),
            'j' to setOf('h', 'u', 'i', 'k', 'n', 'm'),
            'k' to setOf('j', 'i', 'o', 'l', 'm'),
            'l' to setOf('k', 'o', 'p'),
            'z' to setOf('a', 's', 'x'),
            'x' to setOf('z', 's', 'd', 'c'),
            'c' to setOf('x', 'd', 'f', 'v'),
            'v' to setOf('c', 'f', 'g', 'b'),
            'b' to setOf('v', 'g', 'h', 'n'),
            'n' to setOf('b', 'h', 'j', 'm'),
            'm' to setOf('n', 'j', 'k'),
        )
    }
}
