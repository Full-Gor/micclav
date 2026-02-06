package com.micclav.keyboard.core

data class Key(
    val label: String,
    val code: Int = label.firstOrNull()?.code ?: 0,
    val width: Float = 1f,       // relative width (1 = standard key)
    val type: KeyType = KeyType.CHARACTER,
    val shiftLabel: String = label.uppercase(),
    val longPressChars: List<String> = emptyList()
)

enum class KeyType {
    CHARACTER,
    SHIFT,
    BACKSPACE,
    ENTER,
    SPACE,
    LANGUAGE_SWITCH,
    VOICE_INPUT,
    NUMBERS,
    SYMBOLS,
    COMMA,
    PERIOD
}

data class KeyboardLayout(
    val language: KeyboardLanguage,
    val rows: List<List<Key>>
)
