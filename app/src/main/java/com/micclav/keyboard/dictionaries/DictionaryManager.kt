package com.micclav.keyboard.dictionaries

import android.content.Context
import android.util.Log
import com.micclav.keyboard.core.KeyboardLanguage
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * Manages word dictionaries for all supported languages.
 *
 * Dictionaries are stored as simple text files in assets/dictionaries/
 * with one word per line, sorted by frequency (most common first).
 */
class DictionaryManager(private val context: Context) {

    private val dictionaries = mutableMapOf<KeyboardLanguage, Set<String>>()
    private val wordLists = mutableMapOf<KeyboardLanguage, List<String>>()

    companion object {
        private const val TAG = "DictionaryManager"
        private const val MAX_WORDS = 50000
    }

    fun loadDictionary(language: KeyboardLanguage) {
        if (dictionaries.containsKey(language)) return

        val filename = getDictionaryFilename(language)
        try {
            val words = mutableListOf<String>()
            context.assets.open("dictionaries/$filename").bufferedReader().useLines { lines ->
                lines.take(MAX_WORDS).forEach { line ->
                    val word = line.trim().lowercase()
                    if (word.isNotEmpty() && !word.startsWith("#")) {
                        words.add(word)
                    }
                }
            }
            dictionaries[language] = words.toSet()
            wordLists[language] = words
            Log.d(TAG, "Loaded ${words.size} words for ${language.displayName}")
        } catch (e: Exception) {
            Log.w(TAG, "Dictionary not found for ${language.displayName}: $filename, using built-in")
            loadBuiltInDictionary(language)
        }
    }

    fun getWords(language: KeyboardLanguage): List<String> {
        if (!wordLists.containsKey(language)) {
            loadDictionary(language)
        }
        return wordLists[language] ?: emptyList()
    }

    fun contains(word: String, language: KeyboardLanguage): Boolean {
        if (!dictionaries.containsKey(language)) {
            loadDictionary(language)
        }
        return dictionaries[language]?.contains(word.lowercase()) ?: false
    }

    private fun getDictionaryFilename(language: KeyboardLanguage): String = when (language) {
        KeyboardLanguage.FRENCH -> "fr_words.txt"
        KeyboardLanguage.ARABIC -> "ar_words.txt"
        KeyboardLanguage.ENGLISH -> "en_words.txt"
        KeyboardLanguage.TACHELHIT_TIFINAGH -> "shi_tifinagh_words.txt"
        KeyboardLanguage.TACHELHIT_LATIN -> "shi_latin_words.txt"
    }

    /**
     * Built-in minimal dictionaries as fallback when asset files are not present.
     * These contain the most common words in each language.
     */
    private fun loadBuiltInDictionary(language: KeyboardLanguage) {
        val words = when (language) {
            KeyboardLanguage.FRENCH -> FRENCH_COMMON_WORDS
            KeyboardLanguage.ARABIC -> ARABIC_COMMON_WORDS
            KeyboardLanguage.ENGLISH -> ENGLISH_COMMON_WORDS
            KeyboardLanguage.TACHELHIT_TIFINAGH -> TACHELHIT_TIFINAGH_WORDS
            KeyboardLanguage.TACHELHIT_LATIN -> TACHELHIT_LATIN_WORDS
        }
        dictionaries[language] = words.toSet()
        wordLists[language] = words
    }

    // =========================================================================
    // Built-in word lists (most common words, used as fallback)
    // =========================================================================

    private val FRENCH_COMMON_WORDS = listOf(
        // Articles & déterminants
        "le", "la", "les", "un", "une", "des", "du", "de", "au", "aux",
        // Pronoms
        "je", "tu", "il", "elle", "on", "nous", "vous", "ils", "elles",
        "me", "te", "se", "lui", "leur", "ce", "ça", "cela", "ceci",
        "qui", "que", "quoi", "dont", "où", "moi", "toi", "soi",
        // Verbes très courants
        "être", "avoir", "faire", "dire", "aller", "voir", "savoir", "pouvoir",
        "vouloir", "venir", "devoir", "falloir", "prendre", "donner", "parler",
        "aimer", "passer", "trouver", "mettre", "croire", "demander", "rester",
        "est", "suis", "es", "sommes", "êtes", "sont", "ai", "as", "a", "avons",
        "avez", "ont", "fait", "fais", "va", "vais", "vas", "allons", "allez", "vont",
        "dit", "dis", "vois", "voit", "sait", "peut", "peux", "veut", "veux",
        "vient", "dois", "doit", "faut", "prend", "prends", "donne", "donnes",
        "parle", "parles", "aime", "aimes", "passe", "passes", "trouve", "trouves",
        "sera", "serai", "seras", "serons", "serez", "seront",
        "était", "étais", "étions", "étiez", "étaient",
        "avait", "avais", "avions", "aviez", "avaient",
        // Prépositions & conjonctions
        "à", "dans", "en", "sur", "pour", "par", "avec", "sans", "sous",
        "entre", "vers", "chez", "après", "avant", "depuis", "pendant",
        "et", "ou", "mais", "donc", "car", "ni", "que", "si", "quand",
        "comme", "lorsque", "puisque", "parce",
        // Adverbes
        "ne", "pas", "plus", "bien", "très", "aussi", "tout", "trop",
        "encore", "déjà", "toujours", "jamais", "rien", "vraiment",
        "aussi", "même", "ici", "là", "maintenant", "alors", "puis",
        "comment", "pourquoi", "combien", "oui", "non", "peut-être",
        // Adjectifs courants
        "bon", "bonne", "petit", "petite", "grand", "grande", "nouveau", "nouvelle",
        "premier", "première", "dernier", "dernière", "autre", "même", "tout", "toute",
        "beau", "belle", "vieux", "vieille", "jeune", "long", "longue",
        // Noms courants
        "homme", "femme", "enfant", "jour", "temps", "année", "fois",
        "monde", "vie", "main", "chose", "pays", "moment", "heure",
        "maison", "travail", "père", "mère", "fils", "fille",
        "bonjour", "merci", "salut", "bonsoir", "au revoir",
        "problème", "question", "réponse", "idée", "exemple",
        // Mots très fréquents dans la saisie mobile
        "ok", "oui", "non", "lol", "mdr", "stp", "svp",
        "demain", "aujourd'hui", "hier", "soir", "matin",
        "message", "téléphone", "numéro", "adresse", "nom", "prénom",
    )

    private val ARABIC_COMMON_WORDS = listOf(
        "في", "من", "على", "إلى", "عن", "مع", "هذا", "هذه", "ذلك", "تلك",
        "أن", "كان", "قال", "بعد", "قبل", "كل", "بين", "حتى", "لم", "لا",
        "ما", "هل", "أنا", "أنت", "هو", "هي", "نحن", "هم", "كيف", "أين",
        "متى", "لماذا", "ماذا", "الله", "بسم", "الرحمن", "الرحيم",
        "يوم", "سنة", "وقت", "بيت", "مدرسة", "كتاب", "ماء", "أكل",
        "شكرا", "مرحبا", "أهلا", "سلام", "نعم", "صباح", "مساء", "خير",
        "واحد", "اثنان", "ثلاثة", "أربعة", "خمسة", "ستة", "سبعة", "ثمانية",
        "كبير", "صغير", "جديد", "قديم", "جميل", "طيب", "حسن",
        "يريد", "يعرف", "يقول", "يذهب", "يأتي", "يعمل", "يحب",
    )

    private val ENGLISH_COMMON_WORDS = listOf(
        "the", "be", "to", "of", "and", "a", "in", "that", "have", "i",
        "it", "for", "not", "on", "with", "he", "as", "you", "do", "at",
        "this", "but", "his", "by", "from", "they", "we", "say", "her", "she",
        "or", "an", "will", "my", "one", "all", "would", "there", "their", "what",
        "so", "up", "out", "if", "about", "who", "get", "which", "go", "me",
        "when", "make", "can", "like", "time", "no", "just", "him", "know", "take",
        "people", "into", "year", "your", "good", "some", "could", "them", "see",
        "other", "than", "then", "now", "look", "only", "come", "its", "over", "think",
        "also", "back", "after", "use", "two", "how", "our", "work", "first", "well",
        "way", "even", "new", "want", "because", "any", "these", "give", "day", "most",
        "hello", "thanks", "please", "sorry", "yes", "yeah", "no", "ok", "okay",
    )

    private val TACHELHIT_TIFINAGH_WORDS = listOf(
        // Common Tachelhit words in Tifinagh
        "ⴰⵣⵓⵍ",    // azul (bonjour)
        "ⵜⴰⵏⵎⵉⵔⵜ",  // tanmirt (merci)
        "ⵉⵢⵢⵀ",     // iyyeh (oui)
        "ⵓⵀⵓ",      // uhu (non)
        "ⴰⵎⴽ",      // amek (comment)
        "ⵎⴰⵏⵉ",     // mani (où)
        "ⵎⴰⵜⴰ",     // mata (quoi)
        "ⵎⵍⵎⵉ",     // melmi (quand)
        "ⵎⴰⵅ",      // max (pourquoi)
        "ⴰⴼⵍⵍⴰ",    // afella (en haut)
        "ⴰⴷⴷⴰ",     // adda (en bas)
        "ⵜⴰⴷⴷⴰⵔⵜ",  // taddart (maison)
        "ⴰⵔⴳⴰⵣ",    // argaz (homme)
        "ⵜⴰⵎⵖⴰⵔⵜ",  // tamghart (femme)
        "ⴰⴼⵔⵓⵅ",    // afrukh (enfant)
        "ⴰⵎⴰⵏ",     // aman (eau)
        "ⴰⵖⵔⵓⵎ",    // aghrum (pain)
        "ⵜⵉⴼⴰⵡⵜ",   // tifawt (lumière/matin)
        "ⵜⵉⴷⴷⵉⵜ",   // tiddit (vérité)
        "ⵜⴰⵎⴰⵣⵉⵖⵜ", // tamazight
        "ⴰⵙⵙ",      // ass (jour)
        "ⵉⴹ",       // id (nuit)
        "ⴰⵙⴳⴳⵯⴰⵙ",  // asggwas (année)
        "ⵜⴰⵡⵊⴰ",    // tawja (famille)
    )

    private val TACHELHIT_LATIN_WORDS = listOf(
        // Same words in Latin transliteration
        "azul", "tanmirt", "iyyeh", "uhu",
        "amek", "mani", "mata", "melmi", "max",
        "afella", "adda",
        "taddart", "argaz", "tamghart", "afrukh",
        "aman", "aghrum", "tifawt", "tiddit",
        "tamazight", "tachelhit", "souss",
        "ass", "id", "asggwas", "tawja",
        "ur", "ad", "ra", "iga", "illa",
        "zund", "ntta", "nttat", "nkki", "kmmi",
        "lḥmd", "bslama", "mrhba",
        "yallah", "bzzaf", "chwiya", "mezyan",
        "lxir", "lɛafit", "ssalam",
    )
}
