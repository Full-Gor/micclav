package com.micclav.keyboard.ui

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.micclav.keyboard.R
import com.micclav.keyboard.voice.WhisperEngine

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 48, 48, 48)
        }

        // Title
        layout.addView(TextView(this).apply {
            text = "Micclav Keyboard"
            textSize = 28f
            setPadding(0, 0, 0, 16)
        })

        // Subtitle
        layout.addView(TextView(this).apply {
            text = "Clavier multilingue avec dictée vocale offline"
            textSize = 16f
            setPadding(0, 0, 0, 48)
            setTextColor(0xFF666666.toInt())
        })

        // Step 1: Enable keyboard
        layout.addView(TextView(this).apply {
            text = "Étape 1 : Activer le clavier"
            textSize = 18f
            setPadding(0, 0, 0, 8)
        })

        layout.addView(Button(this).apply {
            text = "Ouvrir les paramètres de saisie"
            setOnClickListener {
                startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
            }
        })

        layout.addView(TextView(this).apply {
            text = "Activez \"Micclav\" dans la liste des claviers."
            textSize = 14f
            setPadding(0, 0, 0, 32)
            setTextColor(0xFF888888.toInt())
        })

        // Step 2: Select keyboard
        layout.addView(TextView(this).apply {
            text = "Étape 2 : Choisir Micclav comme clavier actif"
            textSize = 18f
            setPadding(0, 0, 0, 8)
        })

        layout.addView(Button(this).apply {
            text = "Choisir le clavier"
            setOnClickListener {
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showInputMethodPicker()
            }
        })

        // Step 3: Voice model status
        layout.addView(TextView(this).apply {
            text = "Dictée vocale (Whisper)"
            textSize = 18f
            setPadding(0, 32, 0, 8)
        })

        val whisper = WhisperEngine(this)
        val modelStatus = if (whisper.isModelAvailable()) {
            "✓ Modèle vocal installé"
        } else {
            "⚠ Modèle non trouvé\nPlacez le fichier ggml-base.bin dans:\n${whisper.getModelPath()}"
        }

        layout.addView(TextView(this).apply {
            text = modelStatus
            textSize = 14f
            setPadding(0, 0, 0, 16)
            setTextColor(
                if (whisper.isModelAvailable()) 0xFF4CAF50.toInt() else 0xFFFF9800.toInt()
            )
        })

        // Languages
        layout.addView(TextView(this).apply {
            text = "Langues supportées"
            textSize = 18f
            setPadding(0, 32, 0, 8)
        })

        val languages = listOf(
            "🇫🇷 Français (AZERTY) — priorité",
            "🇸🇦 العربية (Arabe)",
            "🇬🇧 English (QWERTY)",
            "ⵣ ⵜⴰⵛⵍⵃⵉⵜ (Tifinagh)",
            "ⵣ Tachelhit (Latin)"
        )
        for (lang in languages) {
            layout.addView(TextView(this).apply {
                text = lang
                textSize = 15f
                setPadding(16, 4, 0, 4)
            })
        }

        // Instructions
        layout.addView(TextView(this).apply {
            text = "\nUtilisation :"
            textSize = 18f
            setPadding(0, 32, 0, 8)
        })

        val instructions = listOf(
            "🌐 Appui pour changer de langue",
            "🎤 Appui pour dictée vocale",
            "Appui long sur une touche pour les accents",
            "Les suggestions apparaissent en haut du clavier"
        )
        for (instr in instructions) {
            layout.addView(TextView(this).apply {
                text = "• $instr"
                textSize = 14f
                setPadding(16, 4, 0, 4)
            })
        }

        setContentView(layout)
    }
}
