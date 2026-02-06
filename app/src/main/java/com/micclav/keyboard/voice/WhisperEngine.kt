package com.micclav.keyboard.voice

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import com.micclav.keyboard.core.KeyboardLanguage
import kotlinx.coroutines.*
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Offline speech-to-text engine using Whisper.
 *
 * Uses whisper.cpp via JNI for on-device transcription.
 * No internet required, no Google dependency.
 */
class WhisperEngine(private val context: Context) {

    private var isListening = false
    private var audioRecord: AudioRecord? = null
    private var recordingJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val mainHandler = Handler(Looper.getMainLooper())

    private var whisperContext: Long = 0L
    private var isModelLoaded = false

    companion object {
        private const val TAG = "WhisperEngine"
        private const val SAMPLE_RATE = 16000
        private const val CHANNEL = AudioFormat.CHANNEL_IN_MONO
        private const val ENCODING = AudioFormat.ENCODING_PCM_16BIT
        private const val MAX_RECORD_SECONDS = 30
        private const val MODEL_FILENAME = "ggml-base.bin"

        init {
            try {
                System.loadLibrary("whisper")
            } catch (e: UnsatisfiedLinkError) {
                Log.e(TAG, "Failed to load whisper native library", e)
            }
        }
    }

    fun isModelAvailable(): Boolean {
        val modelFile = File(context.filesDir, "models/$MODEL_FILENAME")
        return modelFile.exists()
    }

    fun getModelPath(): String {
        return File(context.filesDir, "models/$MODEL_FILENAME").absolutePath
    }

    private fun ensureModelLoaded(): Boolean {
        if (isModelLoaded) return true

        val modelPath = getModelPath()
        if (!File(modelPath).exists()) {
            // Try to copy from assets
            try {
                val assetsModel = context.assets.open("models/$MODEL_FILENAME")
                val outDir = File(context.filesDir, "models")
                outDir.mkdirs()
                val outFile = File(outDir, MODEL_FILENAME)
                assetsModel.use { input ->
                    outFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Model not found: $modelPath", e)
                return false
            }
        }

        whisperContext = whisperInit(modelPath)
        isModelLoaded = whisperContext != 0L
        return isModelLoaded
    }

    fun startListening(language: KeyboardLanguage, onResult: (String) -> Unit) {
        if (isListening) return

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e(TAG, "RECORD_AUDIO permission not granted")
            return
        }

        if (!ensureModelLoaded()) {
            Log.e(TAG, "Whisper model not loaded")
            mainHandler.post { onResult("[Modèle vocal non disponible]") }
            return
        }

        isListening = true

        recordingJob = scope.launch {
            try {
                val bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL, ENCODING)
                audioRecord = AudioRecord(
                    MediaRecorder.AudioSource.MIC,
                    SAMPLE_RATE,
                    CHANNEL,
                    ENCODING,
                    bufferSize * 2
                )

                val audioData = mutableListOf<Short>()
                val buffer = ShortArray(bufferSize)

                audioRecord?.startRecording()
                val maxSamples = SAMPLE_RATE * MAX_RECORD_SECONDS

                // Record until stopped or max duration
                while (isListening && audioData.size < maxSamples) {
                    val read = audioRecord?.read(buffer, 0, buffer.size) ?: 0
                    if (read > 0) {
                        for (i in 0 until read) {
                            audioData.add(buffer[i])
                        }
                    }

                    // Check for silence (auto-stop after 2 seconds of silence)
                    if (audioData.size > SAMPLE_RATE * 2) {
                        val recentSamples = audioData.takeLast(SAMPLE_RATE)
                        val rms = calculateRMS(recentSamples)
                        if (rms < 100) { // silence threshold
                            break
                        }
                    }
                }

                audioRecord?.stop()

                // Convert to float array for Whisper
                val floatData = FloatArray(audioData.size) { audioData[it].toFloat() / 32768f }

                // Run Whisper inference
                val langCode = language.whisperLangCode
                val text = whisperTranscribe(whisperContext, floatData, langCode)

                mainHandler.post {
                    onResult(text.trim())
                }

            } catch (e: Exception) {
                Log.e(TAG, "Recording/transcription error", e)
                mainHandler.post {
                    onResult("")
                }
            } finally {
                isListening = false
                audioRecord?.release()
                audioRecord = null
            }
        }
    }

    fun stopListening() {
        isListening = false
    }

    private fun calculateRMS(samples: List<Short>): Double {
        var sum = 0.0
        for (sample in samples) {
            sum += sample * sample
        }
        return Math.sqrt(sum / samples.size)
    }

    fun release() {
        stopListening()
        recordingJob?.cancel()
        scope.cancel()
        if (isModelLoaded) {
            whisperFree(whisperContext)
            isModelLoaded = false
        }
    }

    // JNI native methods - implemented via whisper.cpp
    private external fun whisperInit(modelPath: String): Long
    private external fun whisperTranscribe(context: Long, audioData: FloatArray, language: String): String
    private external fun whisperFree(context: Long)
}
