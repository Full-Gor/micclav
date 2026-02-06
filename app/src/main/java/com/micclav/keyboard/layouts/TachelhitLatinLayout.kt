package com.micclav.keyboard.layouts

import com.micclav.keyboard.core.Key
import com.micclav.keyboard.core.KeyType
import com.micclav.keyboard.core.KeyboardLanguage
import com.micclav.keyboard.core.KeyboardLayout

/**
 * Tachelhit Souss keyboard in Latin transliteration.
 * Includes special characters used in Amazigh Latin script (ɛ, ɣ, ḍ, ṛ, ṣ, ṭ, ẓ, etc.)
 */
object TachelhitLatinLayout {
    fun get() = KeyboardLayout(
        language = KeyboardLanguage.TACHELHIT_LATIN,
        rows = listOf(
            // Row 1 - Based on French AZERTY with Amazigh additions
            listOf(
                Key("a", longPressChars = listOf("ɛ")),
                Key("z", longPressChars = listOf("ẓ")),
                Key("e"),
                Key("r", longPressChars = listOf("ṛ")),
                Key("t", longPressChars = listOf("ṭ")),
                Key("y"),
                Key("u"),
                Key("i"),
                Key("o"),
                Key("p")
            ),
            // Row 2
            listOf(
                Key("q"),
                Key("s", longPressChars = listOf("ṣ", "š")),
                Key("d", longPressChars = listOf("ḍ")),
                Key("f"),
                Key("g", longPressChars = listOf("ɣ", "gʷ")),
                Key("h", longPressChars = listOf("ḥ")),
                Key("j"),
                Key("k", longPressChars = listOf("kʷ")),
                Key("l"),
                Key("m")
            ),
            // Row 3
            listOf(
                Key("⇧", type = KeyType.SHIFT, width = 1.5f),
                Key("w"),
                Key("x", longPressChars = listOf("x̌")),
                Key("c", longPressChars = listOf("č")),
                Key("v"),
                Key("b"),
                Key("n"),
                Key("ɛ"),
                Key("ɣ"),
                Key("⌫", type = KeyType.BACKSPACE, width = 1.5f)
            ),
            // Row 4: Bottom row
            listOf(
                Key("123", type = KeyType.NUMBERS, width = 1.2f),
                Key("🌐", type = KeyType.LANGUAGE_SWITCH),
                Key(",", type = KeyType.COMMA),
                Key(" ", type = KeyType.SPACE, width = 4f),
                Key(".", type = KeyType.PERIOD, longPressChars = listOf("!", "?", ":", ";")),
                Key("🎤", type = KeyType.VOICE_INPUT),
                Key("⏎", type = KeyType.ENTER, width = 1.2f)
            )
        )
    )
}
