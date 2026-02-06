package com.micclav.keyboard.layouts

import com.micclav.keyboard.core.Key
import com.micclav.keyboard.core.KeyType
import com.micclav.keyboard.core.KeyboardLanguage
import com.micclav.keyboard.core.KeyboardLayout

object ArabicLayout {
    fun get() = KeyboardLayout(
        language = KeyboardLanguage.ARABIC,
        rows = listOf(
            // Row 1
            listOf(
                Key("ض"),
                Key("ص"),
                Key("ث"),
                Key("ق"),
                Key("ف"),
                Key("غ"),
                Key("ع"),
                Key("ه", longPressChars = listOf("ة")),
                Key("خ"),
                Key("ح"),
                Key("ج")
            ),
            // Row 2
            listOf(
                Key("ش"),
                Key("س"),
                Key("ي", longPressChars = listOf("ى", "ئ")),
                Key("ب"),
                Key("ل", longPressChars = listOf("لا", "لأ", "لإ", "لآ")),
                Key("ا", longPressChars = listOf("أ", "إ", "آ", "ٱ")),
                Key("ت", longPressChars = listOf("ة")),
                Key("ن"),
                Key("م"),
                Key("ك"),
                Key("ط")
            ),
            // Row 3
            listOf(
                Key("⇧", type = KeyType.SHIFT, width = 1.5f),
                Key("ذ"),
                Key("ء"),
                Key("ؤ"),
                Key("ر"),
                Key("ى"),
                Key("ة"),
                Key("و"),
                Key("ز"),
                Key("د"),
                Key("⌫", type = KeyType.BACKSPACE, width = 1.5f)
            ),
            // Row 4: Bottom row
            listOf(
                Key("123", type = KeyType.NUMBERS, width = 1.2f),
                Key("🌐", type = KeyType.LANGUAGE_SWITCH),
                Key("،", type = KeyType.COMMA),
                Key(" ", type = KeyType.SPACE, width = 4f),
                Key(".", type = KeyType.PERIOD, longPressChars = listOf("!", "؟", ":", "؛")),
                Key("🎤", type = KeyType.VOICE_INPUT),
                Key("⏎", type = KeyType.ENTER, width = 1.2f)
            )
        )
    )
}
