package com.micclav.keyboard.autocorrect

import com.micclav.keyboard.core.KeyboardLanguage

/**
 * Phonetic matching engine that understands common pronunciation patterns
 * and articulation errors across supported languages.
 *
 * For French: handles common confusions like:
 * - "an/en/am/em" sound alike
 * - "o/au/eau" sound alike
 * - "é/è/ê/ai/ei" confusions
 * - silent final consonants
 * - "qu/k" confusion
 * - "f/ph" confusion
 * - nasal vowels
 */
class PhoneticMatcher {

    fun similarity(input: String, candidate: String, language: KeyboardLanguage): Float {
        val inputPhonemes = toPhonemes(input, language)
        val candidatePhonemes = toPhonemes(candidate, language)

        if (inputPhonemes.isEmpty() || candidatePhonemes.isEmpty()) return 0f

        val distance = levenshteinDistance(inputPhonemes, candidatePhonemes)
        val maxLen = maxOf(inputPhonemes.length, candidatePhonemes.length)
        return 1f - (distance.toFloat() / maxLen)
    }

    private fun toPhonemes(text: String, language: KeyboardLanguage): String = when (language) {
        KeyboardLanguage.FRENCH -> frenchToPhonemes(text)
        KeyboardLanguage.ARABIC -> arabicToPhonemes(text)
        KeyboardLanguage.ENGLISH -> englishToPhonemes(text)
        KeyboardLanguage.TACHELHIT_TIFINAGH -> tachelhitToPhonemes(text)
        KeyboardLanguage.TACHELHIT_LATIN -> tachelhitLatinToPhonemes(text)
    }

    /**
     * French phonetic normalization.
     * Groups sounds that are commonly confused, especially by non-native
     * speakers or in casual/fast speech.
     */
    private fun frenchToPhonemes(text: String): String {
        var s = text.lowercase()

        // Multi-character replacements (order matters - longest first)
        val replacements = listOf(
            // Nasal vowels
            "tion" to "SJÕ",
            "sion" to "SJÕ",
            "ment" to "MÃ",
            "ement" to "MÃ",
            "aient" to "E",
            "ais" to "E",
            "ait" to "E",
            "ain" to "Ẽ",
            "aim" to "Ẽ",
            "ein" to "Ẽ",
            "eim" to "Ẽ",
            "in" to "Ẽ",
            "im" to "Ẽ",
            "un" to "Ẽ",
            "ym" to "Ẽ",
            "yn" to "Ẽ",
            "an" to "Ã",
            "am" to "Ã",
            "en" to "Ã",
            "em" to "Ã",
            "on" to "Õ",
            "om" to "Õ",

            // Vowel groups
            "eau" to "O",
            "aux" to "O",
            "au" to "O",
            "ou" to "U",
            "oi" to "WA",
            "ai" to "E",
            "ei" to "E",
            "eu" to "Ø",
            "œu" to "Ø",

            // Consonant groups
            "ph" to "F",
            "qu" to "K",
            "ch" to "SH",
            "gn" to "NY",
            "gu" to "G",
            "ge" to "JE",
            "gi" to "JI",
            "ll" to "L",
            "ss" to "S",
            "cc" to "KS",
            "th" to "T",
            "rr" to "R",
            "tt" to "T",
            "mm" to "M",
            "nn" to "N",
            "pp" to "P",

            // Accented vowels to base phonemes
            "é" to "E",
            "è" to "E",
            "ê" to "E",
            "ë" to "E",
            "à" to "A",
            "â" to "A",
            "ä" to "A",
            "î" to "I",
            "ï" to "I",
            "ô" to "O",
            "ö" to "O",
            "ù" to "U",
            "û" to "U",
            "ü" to "U",
            "ÿ" to "I",
            "ç" to "S",
            "œ" to "Ø",
            "æ" to "E"
        )

        for ((from, to) in replacements) {
            s = s.replace(from, to)
        }

        // Remove common silent endings
        s = s.replace(Regex("[sxztdpe]$"), "")

        return s.uppercase()
    }

    private fun arabicToPhonemes(text: String): String {
        var s = text
        // Normalize similar Arabic letters
        val replacements = mapOf(
            'ة' to 'ه',  // ta marbuta -> ha
            'ى' to 'ي',  // alif maqsura -> ya
            'أ' to 'ا',  // hamza alif -> alif
            'إ' to 'ا',
            'آ' to 'ا',
            'ٱ' to 'ا',
            'ئ' to 'ي',  // hamza on ya -> ya
            'ؤ' to 'و',  // hamza on waw -> waw
        )
        for ((from, to) in replacements) {
            s = s.replace(from, to)
        }
        // Remove diacritics (tashkeel)
        s = s.replace(Regex("[\u064B-\u065F\u0670]"), "")
        return s
    }

    private fun englishToPhonemes(text: String): String {
        var s = text.lowercase()
        val replacements = listOf(
            "ough" to "O",
            "tion" to "SHUN",
            "sion" to "ZHUN",
            "th" to "TH",
            "ph" to "F",
            "ch" to "CH",
            "sh" to "SH",
            "ck" to "K",
            "gh" to "",
            "kn" to "N",
            "wr" to "R",
            "wh" to "W",
            "ee" to "I",
            "ea" to "I",
            "oo" to "U",
            "ou" to "AW",
            "ow" to "AW",
            "ai" to "AY",
            "ay" to "AY",
            "oi" to "OY",
            "oy" to "OY",
        )
        for ((from, to) in replacements) {
            s = s.replace(from, to)
        }
        // Remove double consonants
        s = s.replace(Regex("(.)\\1"), "$1")
        return s.uppercase()
    }

    private fun tachelhitToPhonemes(text: String): String {
        // Tifinagh to phonetic representation
        val mapping = mapOf(
            'ⴰ' to "A", 'ⴱ' to "B", 'ⴳ' to "G", 'ⴷ' to "D",
            'ⴹ' to "D", 'ⴼ' to "F", 'ⴽ' to "K", 'ⵀ' to "H",
            'ⵃ' to "H", 'ⵄ' to "A", 'ⵅ' to "X", 'ⵉ' to "I",
            'ⵊ' to "J", 'ⵍ' to "L", 'ⵎ' to "M", 'ⵏ' to "N",
            'ⵓ' to "U", 'ⵔ' to "R", 'ⵕ' to "R", 'ⵙ' to "S",
            'ⵚ' to "S", 'ⵜ' to "T", 'ⵟ' to "T", 'ⵡ' to "W",
            'ⵢ' to "Y", 'ⵣ' to "Z", 'ⵥ' to "Z", 'ⵇ' to "Q",
            'ⵛ' to "SH", 'ⵖ' to "GH", 'ⵯ' to "W"
        )
        return text.map { mapping[it] ?: it.toString() }.joinToString("")
    }

    private fun tachelhitLatinToPhonemes(text: String): String {
        var s = text.lowercase()
        val replacements = listOf(
            "ɣ" to "GH",
            "ɛ" to "A",
            "ḥ" to "H",
            "ḍ" to "D",
            "ṛ" to "R",
            "ṣ" to "S",
            "ṭ" to "T",
            "ẓ" to "Z",
            "š" to "SH",
            "č" to "CH",
            "gʷ" to "GW",
            "kʷ" to "KW",
        )
        for ((from, to) in replacements) {
            s = s.replace(from, to)
        }
        // Remove double consonants
        s = s.replace(Regex("(.)\\1"), "$1")
        return s.uppercase()
    }

    private fun levenshteinDistance(a: String, b: String): Int {
        val dp = Array(a.length + 1) { IntArray(b.length + 1) }
        for (i in 0..a.length) dp[i][0] = i
        for (j in 0..b.length) dp[0][j] = j

        for (i in 1..a.length) {
            for (j in 1..b.length) {
                val cost = if (a[i - 1] == b[j - 1]) 0 else 1
                dp[i][j] = minOf(
                    dp[i - 1][j] + 1,
                    dp[i][j - 1] + 1,
                    dp[i - 1][j - 1] + cost
                )
            }
        }
        return dp[a.length][b.length]
    }
}
