package com.micclav.keyboard.layouts

import com.micclav.keyboard.core.Key
import com.micclav.keyboard.core.KeyType
import com.micclav.keyboard.core.KeyboardLanguage
import com.micclav.keyboard.core.KeyboardLayout

object EnglishLayout {
    fun get() = KeyboardLayout(
        language = KeyboardLanguage.ENGLISH,
        rows = listOf(
            // Row 1: QWERTY
            listOf(
                Key("q"),
                Key("w"),
                Key("e"),
                Key("r"),
                Key("t"),
                Key("y"),
                Key("u"),
                Key("i"),
                Key("o"),
                Key("p")
            ),
            // Row 2
            listOf(
                Key("a"),
                Key("s"),
                Key("d"),
                Key("f"),
                Key("g"),
                Key("h"),
                Key("j"),
                Key("k"),
                Key("l")
            ),
            // Row 3
            listOf(
                Key("⇧", type = KeyType.SHIFT, width = 1.5f),
                Key("z"),
                Key("x"),
                Key("c"),
                Key("v"),
                Key("b"),
                Key("n"),
                Key("m"),
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
