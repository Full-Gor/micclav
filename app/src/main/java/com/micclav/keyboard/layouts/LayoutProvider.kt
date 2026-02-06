package com.micclav.keyboard.layouts

import com.micclav.keyboard.core.Key
import com.micclav.keyboard.core.KeyType
import com.micclav.keyboard.core.KeyboardLanguage
import com.micclav.keyboard.core.KeyboardLayout

object LayoutProvider {

    fun getLayout(language: KeyboardLanguage): KeyboardLayout = when (language) {
        KeyboardLanguage.FRENCH -> FrenchLayout.get()
        KeyboardLanguage.ARABIC -> ArabicLayout.get()
        KeyboardLanguage.ENGLISH -> EnglishLayout.get()
        KeyboardLanguage.TACHELHIT_TIFINAGH -> TachelhitTifinaghLayout.get()
        KeyboardLanguage.TACHELHIT_LATIN -> TachelhitLatinLayout.get()
    }

    fun getNumberLayout(): KeyboardLayout = NumberLayout.get()

    fun getSymbolLayout(): KeyboardLayout = SymbolLayout.get()
}
