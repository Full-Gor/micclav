# Micclav Keyboard ProGuard rules

# Keep Whisper JNI
-keep class com.micclav.keyboard.voice.WhisperEngine {
    native <methods>;
}

# Keep IME service
-keep class com.micclav.keyboard.core.MicclavInputMethodService { *; }
