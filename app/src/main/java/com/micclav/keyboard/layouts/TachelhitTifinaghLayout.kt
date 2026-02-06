package com.micclav.keyboard.layouts

import com.micclav.keyboard.core.Key
import com.micclav.keyboard.core.KeyType
import com.micclav.keyboard.core.KeyboardLanguage
import com.micclav.keyboard.core.KeyboardLayout

/**
 * Tachelhit Souss keyboard in Tifinagh script (IRCAM standard).
 * Based on the official Moroccan Amazigh Tifinagh layout.
 */
object TachelhitTifinaghLayout {
    fun get() = KeyboardLayout(
        language = KeyboardLanguage.TACHELHIT_TIFINAGH,
        rows = listOf(
            // Row 1 - Common Tachelhit consonants
            listOf(
                Key("ⴰ"),  // a (ya)
                Key("ⴱ"),  // b (yab)
                Key("ⴳ", longPressChars = listOf("ⴳⵯ")),  // g (yag), gʷ
                Key("ⴷ", longPressChars = listOf("ⴹ")),    // d (yad), ḍ
                Key("ⴼ"),  // f (yaf)
                Key("ⴽ", longPressChars = listOf("ⴽⵯ")),  // k (yak), kʷ
                Key("ⵀ"),  // h (yah)
                Key("ⵃ"),  // ḥ (yaḥ)
                Key("ⵄ"),  // ɛ (yaɛ)
                Key("ⵅ")   // x (yax)
            ),
            // Row 2
            listOf(
                Key("ⵉ"),  // i (yi)
                Key("ⵊ"),  // j (yaj)
                Key("ⵍ"),  // l (yal)
                Key("ⵎ"),  // m (yam)
                Key("ⵏ"),  // n (yan)
                Key("ⵓ"),  // u (yu)
                Key("ⵔ", longPressChars = listOf("ⵕ")),  // r (yar), ṛ
                Key("ⵙ", longPressChars = listOf("ⵚ")),  // s (yas), ṣ
                Key("ⵜ", longPressChars = listOf("ⵟ")),  // t (yat), ṭ
                Key("ⵡ")   // w (yaw)
            ),
            // Row 3
            listOf(
                Key("⇧", type = KeyType.SHIFT, width = 1.5f),
                Key("ⵢ"),  // y (yay)
                Key("ⵣ", longPressChars = listOf("ⵥ")),  // z (yaz), ẓ
                Key("ⵇ"),  // q (yaq)
                Key("ⵛ"),  // š/ch (yach)
                Key("ⵖ"),  // ɣ (yaɣ)
                Key("ⵯ"),  // labialization mark
                Key("⌫", type = KeyType.BACKSPACE, width = 1.5f)
            ),
            // Row 4: Bottom row
            listOf(
                Key("123", type = KeyType.NUMBERS, width = 1.2f),
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
