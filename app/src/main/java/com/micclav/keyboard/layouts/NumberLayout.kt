package com.micclav.keyboard.layouts

import com.micclav.keyboard.core.Key
import com.micclav.keyboard.core.KeyType
import com.micclav.keyboard.core.KeyboardLanguage
import com.micclav.keyboard.core.KeyboardLayout

object NumberLayout {
    fun get() = KeyboardLayout(
        language = KeyboardLanguage.FRENCH,
        rows = listOf(
            listOf(
                Key("1"), Key("2"), Key("3"), Key("4"), Key("5"),
                Key("6"), Key("7"), Key("8"), Key("9"), Key("0")
            ),
            listOf(
                Key("@"), Key("#"), Key("€"), Key("_"), Key("&"),
                Key("-"), Key("+"), Key("("), Key(")"), Key("/")
            ),
            listOf(
                Key("#+=", type = KeyType.SYMBOLS, width = 1.5f),
                Key("*"), Key("\""), Key("'"), Key(":"),
                Key(";"), Key("!"), Key("?"),
                Key("⌫", type = KeyType.BACKSPACE, width = 1.5f)
            ),
            listOf(
                Key("ABC", type = KeyType.NUMBERS, width = 1.2f),
                Key("🌐", type = KeyType.LANGUAGE_SWITCH),
                Key(",", type = KeyType.COMMA),
                Key(" ", type = KeyType.SPACE, width = 4f),
                Key(".", type = KeyType.PERIOD),
                Key("🎤", type = KeyType.VOICE_INPUT),
                Key("⏎", type = KeyType.ENTER, width = 1.2f)
            )
        )
    )
}
