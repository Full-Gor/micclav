package com.micclav.keyboard.core

import android.inputmethodservice.InputMethodService
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.View
import android.view.inputmethod.EditorInfo
import com.micclav.keyboard.autocorrect.AutoCorrectEngine
import com.micclav.keyboard.layouts.LayoutProvider
import com.micclav.keyboard.ui.KeyboardView
import com.micclav.keyboard.voice.WhisperEngine

class MicclavInputMethodService : InputMethodService() {

    private lateinit var keyboardView: KeyboardView
    private lateinit var autoCorrect: AutoCorrectEngine
    private lateinit var whisperEngine: WhisperEngine

    private var currentLanguage = KeyboardLanguage.DEFAULT
    private var isShifted = false
    private var isCapsLock = false
    private var isNumberMode = false
    private var currentWord = StringBuilder()

    override fun onCreateInputView(): View {
        autoCorrect = AutoCorrectEngine(this)
        whisperEngine = WhisperEngine(this)

        keyboardView = KeyboardView(this).apply {
            setLayout(LayoutProvider.getLayout(currentLanguage))
            onKeyPress = { key -> handleKeyPress(key) }
            onKeyLongPress = { key -> handleKeyLongPress(key) }
            onSuggestionSelected = { suggestion -> applySuggestion(suggestion) }
            onVoiceInput = { startVoiceInput() }
            onLanguageSwitch = { switchLanguage() }
        }

        return keyboardView
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        currentWord.clear()
        keyboardView.clearSuggestions()
        keyboardView.setLayout(LayoutProvider.getLayout(currentLanguage))
    }

    private fun handleKeyPress(key: Key) {
        vibrateKey()
        val ic = currentInputConnection ?: return

        when (key.type) {
            KeyType.CHARACTER -> {
                val char = if (isShifted || isCapsLock) key.shiftLabel else key.label
                ic.commitText(char, 1)
                currentWord.append(char)
                updateSuggestions()
                if (isShifted && !isCapsLock) {
                    isShifted = false
                    keyboardView.setShifted(false)
                }
            }

            KeyType.SPACE -> {
                commitCurrentWord()
                ic.commitText(" ", 1)
                currentWord.clear()
                keyboardView.clearSuggestions()
            }

            KeyType.BACKSPACE -> {
                if (currentWord.isNotEmpty()) {
                    currentWord.deleteCharAt(currentWord.length - 1)
                }
                ic.deleteSurroundingText(1, 0)
                updateSuggestions()
            }

            KeyType.ENTER -> {
                commitCurrentWord()
                val action = currentInputEditorInfo?.imeOptions ?: 0
                if (!sendDefaultEditorAction(true)) {
                    ic.commitText("\n", 1)
                }
                currentWord.clear()
                keyboardView.clearSuggestions()
            }

            KeyType.SHIFT -> {
                if (isShifted) {
                    isCapsLock = !isCapsLock
                } else {
                    isShifted = true
                }
                keyboardView.setShifted(isShifted || isCapsLock)
            }

            KeyType.LANGUAGE_SWITCH -> {
                switchLanguage()
            }

            KeyType.VOICE_INPUT -> {
                startVoiceInput()
            }

            KeyType.NUMBERS -> {
                isNumberMode = !isNumberMode
                keyboardView.setLayout(
                    if (isNumberMode) LayoutProvider.getNumberLayout()
                    else LayoutProvider.getLayout(currentLanguage)
                )
            }

            KeyType.COMMA -> {
                ic.commitText(",", 1)
                commitCurrentWord()
                currentWord.clear()
            }

            KeyType.PERIOD -> {
                ic.commitText(".", 1)
                commitCurrentWord()
                currentWord.clear()
            }

            KeyType.SYMBOLS -> {
                keyboardView.setLayout(LayoutProvider.getSymbolLayout())
            }
        }
    }

    private fun handleKeyLongPress(key: Key) {
        if (key.longPressChars.isNotEmpty()) {
            keyboardView.showPopupChars(key)
        }
    }

    private fun switchLanguage() {
        val languages = KeyboardLanguage.entries
        val currentIndex = languages.indexOf(currentLanguage)
        currentLanguage = languages[(currentIndex + 1) % languages.size]
        isNumberMode = false
        keyboardView.setLayout(LayoutProvider.getLayout(currentLanguage))
        keyboardView.setLanguageIndicator(currentLanguage)
        autoCorrect.setLanguage(currentLanguage)
        currentWord.clear()
        keyboardView.clearSuggestions()
    }

    private fun updateSuggestions() {
        if (currentWord.isEmpty()) {
            keyboardView.clearSuggestions()
            return
        }
        val suggestions = autoCorrect.getSuggestions(currentWord.toString(), currentLanguage)
        keyboardView.showSuggestions(suggestions)
    }

    private fun applySuggestion(suggestion: String) {
        val ic = currentInputConnection ?: return
        // Delete current word and replace with suggestion
        ic.deleteSurroundingText(currentWord.length, 0)
        ic.commitText(suggestion, 1)
        currentWord.clear()
        currentWord.append(suggestion)
        keyboardView.clearSuggestions()
    }

    private fun commitCurrentWord() {
        // Auto-correct on space/enter if there's a strong suggestion
        if (currentWord.isEmpty()) return
        val topSuggestion = autoCorrect.getTopCorrection(currentWord.toString(), currentLanguage)
        if (topSuggestion != null && topSuggestion != currentWord.toString()) {
            val ic = currentInputConnection ?: return
            ic.deleteSurroundingText(currentWord.length, 0)
            ic.commitText(topSuggestion, 1)
        }
    }

    private fun startVoiceInput() {
        whisperEngine.startListening(currentLanguage) { text ->
            val ic = currentInputConnection ?: return@startListening
            ic.commitText(text, 1)
            keyboardView.setVoiceListening(false)
        }
        keyboardView.setVoiceListening(true)
    }

    private fun vibrateKey() {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = getSystemService(VIBRATOR_MANAGER_SERVICE) as VibratorManager
            manager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(VIBRATOR_SERVICE) as Vibrator
        }
        vibrator.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE))
    }

    override fun onDestroy() {
        super.onDestroy()
        whisperEngine.release()
    }
}
