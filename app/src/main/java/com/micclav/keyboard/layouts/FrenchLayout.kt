package com.micclav.keyboard.layouts

import com.micclav.keyboard.core.Key
import com.micclav.keyboard.core.KeyType
import com.micclav.keyboard.core.KeyboardLanguage
import com.micclav.keyboard.core.KeyboardLayout

object FrenchLayout {
    fun get() = KeyboardLayout(
        language = KeyboardLanguage.FRENCH,
        rows = listOf(
            // Row 1: AZERTY
            listOf(
                Key("a", longPressChars = listOf("à", "â", "æ", "ä")),
                Key("z"),
                Key("e", longPressChars = listOf("é", "è", "ê", "ë", "€")),
                Key("r"),
                Key("t"),
                Key("y", longPressChars = listOf("ÿ")),
                Key("u", longPressChars = listOf("ù", "û", "ü")),
                Key("i", longPressChars = listOf("î", "ï")),
                Key("o", longPressChars = listOf("ô", "œ", "ö")),
                Key("p")
            ),
            // Row 2
            listOf(
                Key("q"),
                Key("s"),
                Key("d"),
                Key("f"),
                Key("g"),
                Key("h"),
                Key("j"),
                Key("k"),
                Key("l"),
                Key("m")
            ),
            // Row 3: Shift + characters + backspace
            listOf(
                Key("⇧", type = KeyType.SHIFT, width = 1.5f),
                Key("w"),
                Key("x"),
                Key("c", longPressChars = listOf("ç", "ć")),
                Key("v"),
                Key("b"),
                Key("n", longPressChars = listOf("ñ")),
                Key("'", longPressChars = listOf("'", "\"")),
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
