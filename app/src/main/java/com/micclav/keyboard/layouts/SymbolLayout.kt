package com.micclav.keyboard.layouts

import com.micclav.keyboard.core.Key
import com.micclav.keyboard.core.KeyType
import com.micclav.keyboard.core.KeyboardLanguage
import com.micclav.keyboard.core.KeyboardLayout

object SymbolLayout {
    fun get() = KeyboardLayout(
        language = KeyboardLanguage.FRENCH,
        rows = listOf(
            listOf(
                Key("~"), Key("`"), Key("|"), Key("·"), Key("√"),
                Key("π"), Key("÷"), Key("×"), Key("¶"), Key("∆")
            ),
            listOf(
                Key("£"), Key("¥"), Key("$"), Key("¢"), Key("^"),
                Key("°"), Key("="), Key("{"), Key("}"), Key("\\")
            ),
            listOf(
                Key("123", type = KeyType.NUMBERS, width = 1.5f),
                Key("%"), Key("©"), Key("®"), Key("™"),
                Key("✓"), Key("["), Key("]"),
                Key("⌫", type = KeyType.BACKSPACE, width = 1.5f)
            ),
            listOf(
                Key("ABC", type = KeyType.NUMBERS, width = 1.2f),
                Key("🌐", type = KeyType.LANGUAGE_SWITCH),
                Key("<"),
                Key(" ", type = KeyType.SPACE, width = 4f),
                Key(">"),
                Key("🎤", type = KeyType.VOICE_INPUT),
                Key("⏎", type = KeyType.ENTER, width = 1.2f)
            )
        )
    )
}
