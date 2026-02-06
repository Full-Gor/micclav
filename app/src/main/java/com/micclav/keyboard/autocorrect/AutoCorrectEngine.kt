package com.micclav.keyboard.autocorrect

import android.content.Context
import com.micclav.keyboard.core.KeyboardLanguage
import com.micclav.keyboard.dictionaries.DictionaryManager

class AutoCorrectEngine(private val context: Context) {

    private val dictionaryManager = DictionaryManager(context)
    private val phoneticMatcher = PhoneticMatcher()
    private val fuzzyMatcher = FuzzyMatcher()

    private var currentLanguage = KeyboardLanguage.DEFAULT

    fun setLanguage(language: KeyboardLanguage) {
        currentLanguage = language
        dictionaryManager.loadDictionary(language)
    }

    fun getSuggestions(input: String, language: KeyboardLanguage, maxResults: Int = 5): List<String> {
        if (input.length < 2) return emptyList()

        val candidates = mutableListOf<ScoredWord>()
        val words = dictionaryManager.getWords(language)

        for (word in words) {
            val score = calculateScore(input, word, language)
            if (score > SUGGESTION_THRESHOLD) {
                candidates.add(ScoredWord(word, score))
            }
        }

        return candidates
            .sortedByDescending { it.score }
            .take(maxResults)
            .map { it.word }
    }

    fun getTopCorrection(input: String, language: KeyboardLanguage): String? {
        val suggestions = getSuggestions(input, language, maxResults = 1)
        val top = suggestions.firstOrNull() ?: return null
        // Only auto-correct if confidence is high enough
        val score = calculateScore(input, top, language)
        return if (score > AUTO_CORRECT_THRESHOLD) top else null
    }

    private fun calculateScore(input: String, candidate: String, language: KeyboardLanguage): Float {
        val inputLower = input.lowercase()
        val candidateLower = candidate.lowercase()

        // Exact prefix match gets highest score
        if (candidateLower.startsWith(inputLower)) {
            return 1.0f
        }

        // Edit distance score
        val editScore = fuzzyMatcher.normalizedSimilarity(inputLower, candidateLower)

        // Phonetic similarity score (language-aware)
        val phoneticScore = phoneticMatcher.similarity(inputLower, candidateLower, language)

        // Keyboard proximity score (adjacent key typos)
        val proximityScore = fuzzyMatcher.keyboardProximityScore(inputLower, candidateLower, language)

        // Weighted combination - phonetic gets more weight for speech correction
        return (editScore * 0.3f + phoneticScore * 0.5f + proximityScore * 0.2f)
    }

    private data class ScoredWord(val word: String, val score: Float)

    companion object {
        private const val SUGGESTION_THRESHOLD = 0.4f
        private const val AUTO_CORRECT_THRESHOLD = 0.85f
    }
}
