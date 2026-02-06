package com.micclav.keyboard.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import com.micclav.keyboard.core.Key
import com.micclav.keyboard.core.KeyType
import com.micclav.keyboard.core.KeyboardLanguage
import com.micclav.keyboard.core.KeyboardLayout

@SuppressLint("ViewConstructor")
class KeyboardView(context: Context) : LinearLayout(context) {

    var onKeyPress: ((Key) -> Unit)? = null
    var onKeyLongPress: ((Key) -> Unit)? = null
    var onSuggestionSelected: ((String) -> Unit)? = null
    var onVoiceInput: (() -> Unit)? = null
    var onLanguageSwitch: (() -> Unit)? = null

    private val suggestionsBar: LinearLayout
    private val keyboardContainer: LinearLayout
    private val languageIndicator: TextView
    private var currentLayout: KeyboardLayout? = null
    private var isShiftActive = false
    private var popupWindow: PopupWindow? = null

    private val keyHeight = dpToPx(48)
    private val keyMargin = dpToPx(3)
    private val rowPadding = dpToPx(2)

    init {
        orientation = VERTICAL
        setBackgroundColor(Color.parseColor("#E8EAED"))
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        // Suggestions bar
        suggestionsBar = LinearLayout(context).apply {
            orientation = HORIZONTAL
            layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT,
                dpToPx(40)
            )
            setBackgroundColor(Color.parseColor("#F1F3F4"))
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dpToPx(8), 0, dpToPx(8), 0)
        }
        addView(suggestionsBar)

        // Language indicator
        languageIndicator = TextView(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, dpToPx(20))
            gravity = Gravity.CENTER
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
            setTextColor(Color.parseColor("#5F6368"))
        }
        addView(languageIndicator)

        // Keyboard rows container
        keyboardContainer = LinearLayout(context).apply {
            orientation = VERTICAL
            layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
            )
            setPadding(dpToPx(2), dpToPx(4), dpToPx(2), dpToPx(8))
        }
        addView(keyboardContainer)
    }

    fun setLayout(layout: KeyboardLayout) {
        currentLayout = layout
        keyboardContainer.removeAllViews()

        val isRtl = layout.language.isRtl
        layoutDirection = if (isRtl) View.LAYOUT_DIRECTION_RTL else View.LAYOUT_DIRECTION_LTR

        for (row in layout.rows) {
            val rowView = LinearLayout(context).apply {
                orientation = HORIZONTAL
                layoutParams = LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT
                )
                gravity = Gravity.CENTER_HORIZONTAL
                setPadding(0, rowPadding, 0, rowPadding)
            }

            val totalWeight = row.sumOf { it.width.toDouble() }.toFloat()

            for (key in row) {
                val keyView = createKeyView(key, totalWeight)
                rowView.addView(keyView)
            }

            keyboardContainer.addView(rowView)
        }

        setLanguageIndicator(layout.language)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun createKeyView(key: Key, totalWeight: Float): View {
        val keyView = TextView(context).apply {
            text = getKeyDisplayText(key)
            gravity = Gravity.CENTER
            setTextSize(TypedValue.COMPLEX_UNIT_SP, getTextSize(key))
            setTextColor(getKeyTextColor(key))
            typeface = if (key.type == KeyType.CHARACTER) Typeface.DEFAULT else Typeface.DEFAULT_BOLD

            val lp = LayoutParams(0, keyHeight).apply {
                weight = key.width / totalWeight
                setMargins(keyMargin, 0, keyMargin, 0)
            }
            layoutParams = lp

            setBackgroundResource(getKeyBackground(key))
            isClickable = true
            isFocusable = true

            // Handle touch events
            var longPressTriggered = false
            setOnTouchListener { v, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        longPressTriggered = false
                        v.isPressed = true
                        // Start long press timer
                        v.postDelayed({
                            if (v.isPressed) {
                                longPressTriggered = true
                                if (key.longPressChars.isNotEmpty()) {
                                    showPopupChars(key, v)
                                }
                                onKeyLongPress?.invoke(key)
                            }
                        }, 400)
                        true
                    }
                    MotionEvent.ACTION_UP -> {
                        v.isPressed = false
                        v.removeCallbacks(null)
                        if (!longPressTriggered) {
                            onKeyPress?.invoke(key)
                        }
                        true
                    }
                    MotionEvent.ACTION_CANCEL -> {
                        v.isPressed = false
                        v.removeCallbacks(null)
                        true
                    }
                    else -> false
                }
            }
        }
        return keyView
    }

    private fun getKeyDisplayText(key: Key): String = when (key.type) {
        KeyType.CHARACTER -> if (isShiftActive) key.shiftLabel else key.label
        KeyType.SHIFT -> if (isShiftActive) "⇪" else "⇧"
        KeyType.BACKSPACE -> "⌫"
        KeyType.ENTER -> "⏎"
        KeyType.SPACE -> currentLayout?.language?.shortLabel ?: ""
        KeyType.LANGUAGE_SWITCH -> "🌐"
        KeyType.VOICE_INPUT -> "🎤"
        KeyType.NUMBERS -> key.label
        KeyType.SYMBOLS -> key.label
        KeyType.COMMA -> key.label
        KeyType.PERIOD -> key.label
    }

    private fun getTextSize(key: Key): Float = when (key.type) {
        KeyType.CHARACTER -> 20f
        KeyType.SPACE -> 12f
        KeyType.NUMBERS, KeyType.SYMBOLS -> 13f
        KeyType.LANGUAGE_SWITCH, KeyType.VOICE_INPUT -> 18f
        else -> 18f
    }

    private fun getKeyTextColor(key: Key): Int = when (key.type) {
        KeyType.CHARACTER, KeyType.COMMA, KeyType.PERIOD -> Color.parseColor("#212121")
        KeyType.SHIFT, KeyType.BACKSPACE, KeyType.NUMBERS, KeyType.SYMBOLS -> Color.parseColor("#FFFFFF")
        KeyType.ENTER -> Color.parseColor("#FFFFFF")
        KeyType.LANGUAGE_SWITCH, KeyType.VOICE_INPUT -> Color.parseColor("#1A73E8")
        KeyType.SPACE -> Color.parseColor("#5F6368")
    }

    private fun getKeyBackground(key: Key): Int {
        val packageName = context.packageName
        return when (key.type) {
            KeyType.CHARACTER, KeyType.COMMA, KeyType.PERIOD ->
                context.resources.getIdentifier("key_background", "drawable", packageName)
            KeyType.SPACE ->
                context.resources.getIdentifier("key_space_background", "drawable", packageName)
            else ->
                context.resources.getIdentifier("key_special_background", "drawable", packageName)
        }
    }

    fun setShifted(shifted: Boolean) {
        isShiftActive = shifted
        currentLayout?.let { setLayout(it) }
    }

    fun setLanguageIndicator(language: KeyboardLanguage) {
        languageIndicator.text = language.displayName
    }

    fun showSuggestions(suggestions: List<String>) {
        suggestionsBar.removeAllViews()
        for (suggestion in suggestions) {
            val tv = TextView(context).apply {
                text = suggestion
                setPadding(dpToPx(16), dpToPx(8), dpToPx(16), dpToPx(8))
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
                setTextColor(Color.parseColor("#1A73E8"))
                typeface = Typeface.DEFAULT_BOLD
                setBackgroundResource(android.R.drawable.list_selector_background)
                setOnClickListener {
                    onSuggestionSelected?.invoke(suggestion)
                }
            }
            suggestionsBar.addView(tv)
        }
    }

    fun clearSuggestions() {
        suggestionsBar.removeAllViews()
    }

    fun showPopupChars(key: Key, anchorView: View? = null) {
        popupWindow?.dismiss()

        val anchor = anchorView ?: return
        val popupLayout = LinearLayout(context).apply {
            orientation = HORIZONTAL
            setBackgroundColor(Color.parseColor("#FFFFFF"))
            elevation = dpToPx(4).toFloat()
            setPadding(dpToPx(4), dpToPx(4), dpToPx(4), dpToPx(4))
        }

        for (char in key.longPressChars) {
            val charView = TextView(context).apply {
                text = char
                gravity = Gravity.CENTER
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
                setTextColor(Color.parseColor("#212121"))
                setPadding(dpToPx(12), dpToPx(8), dpToPx(12), dpToPx(8))
                setBackgroundResource(android.R.drawable.list_selector_background)
                setOnClickListener {
                    val charKey = Key(char, type = KeyType.CHARACTER)
                    onKeyPress?.invoke(charKey)
                    popupWindow?.dismiss()
                }
            }
            popupLayout.addView(charView)
        }

        popupWindow = PopupWindow(
            popupLayout,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        ).apply {
            elevation = dpToPx(8).toFloat()
            showAsDropDown(anchor, 0, -keyHeight * 2)
        }
    }

    fun setVoiceListening(listening: Boolean) {
        // Visual feedback for voice recording
        if (listening) {
            languageIndicator.text = "🔴 Écoute…"
            languageIndicator.setTextColor(Color.parseColor("#D93025"))
        } else {
            currentLayout?.let { setLanguageIndicator(it.language) }
            languageIndicator.setTextColor(Color.parseColor("#5F6368"))
        }
    }

    private fun dpToPx(dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            context.resources.displayMetrics
        ).toInt()
    }
}
