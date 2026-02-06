package com.micclav.keyboard.core

enum class KeyboardLanguage(
    val code: String,
    val displayName: String,
    val shortLabel: String,
    val isRtl: Boolean = false,
    val whisperLangCode: String = code
) {
    FRENCH("fr", "Français", "FR", whisperLangCode = "fr"),
    ARABIC("ar", "العربية", "ع", isRtl = true, whisperLangCode = "ar"),
    ENGLISH("en", "English", "EN", whisperLangCode = "en"),
    TACHELHIT_TIFINAGH("shi", "ⵜⴰⵛⵍⵃⵉⵜ", "ⵜⵛⵍ", whisperLangCode = "ar"),
    TACHELHIT_LATIN("shi-latn", "Tachelhit", "TCH", whisperLangCode = "ar");

    companion object {
        val DEFAULT = FRENCH
        fun fromCode(code: String): KeyboardLanguage =
            entries.firstOrNull { it.code == code } ?: DEFAULT
    }
}
