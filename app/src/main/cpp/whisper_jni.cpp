#include <jni.h>
#include <string>
#include <android/log.h>

#define TAG "WhisperJNI"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)

#ifndef WHISPER_STUB
#include "whisper.h"
#endif

extern "C" {

JNIEXPORT jlong JNICALL
Java_com_micclav_keyboard_voice_WhisperEngine_whisperInit(
        JNIEnv *env, jobject obj, jstring model_path) {
#ifndef WHISPER_STUB
    const char *path = env->GetStringUTFChars(model_path, nullptr);
    LOGI("Loading model from: %s", path);

    struct whisper_context_params cparams = whisper_context_default_params();
    struct whisper_context *ctx = whisper_init_from_file_with_params(path, cparams);

    env->ReleaseStringUTFChars(model_path, path);

    if (ctx == nullptr) {
        LOGE("Failed to initialize whisper context");
        return 0;
    }

    LOGI("Model loaded successfully");
    return (jlong) ctx;
#else
    LOGI("Whisper stub mode - no model loaded");
    return 1; // non-zero to indicate "loaded"
#endif
}

JNIEXPORT jstring JNICALL
Java_com_micclav_keyboard_voice_WhisperEngine_whisperTranscribe(
        JNIEnv *env, jobject obj, jlong context_ptr, jfloatArray audio_data, jstring language) {
#ifndef WHISPER_STUB
    auto *ctx = (struct whisper_context *) context_ptr;
    if (ctx == nullptr) {
        return env->NewStringUTF("");
    }

    const char *lang = env->GetStringUTFChars(language, nullptr);
    jfloat *audio = env->GetFloatArrayElements(audio_data, nullptr);
    jsize audio_len = env->GetArrayLength(audio_data);

    LOGI("Transcribing %d samples, language: %s", audio_len, lang);

    // Configure whisper parameters
    struct whisper_full_params params = whisper_full_default_params(WHISPER_SAMPLING_GREEDY);
    params.language = lang;
    params.n_threads = 4;
    params.translate = false;
    params.no_timestamps = true;
    params.single_segment = true;

    // Run inference
    int result = whisper_full(ctx, params, audio, audio_len);

    env->ReleaseFloatArrayElements(audio_data, audio, 0);
    env->ReleaseStringUTFChars(language, lang);

    if (result != 0) {
        LOGE("Whisper transcription failed with code: %d", result);
        return env->NewStringUTF("");
    }

    // Get transcribed text
    int n_segments = whisper_full_n_segments(ctx);
    std::string text;
    for (int i = 0; i < n_segments; i++) {
        text += whisper_full_get_segment_text(ctx, i);
    }

    LOGI("Transcription result: %s", text.c_str());
    return env->NewStringUTF(text.c_str());
#else
    LOGI("Whisper stub mode - returning empty transcription");
    return env->NewStringUTF("[Dictée vocale: installez whisper.cpp]");
#endif
}

JNIEXPORT void JNICALL
Java_com_micclav_keyboard_voice_WhisperEngine_whisperFree(
        JNIEnv *env, jobject obj, jlong context_ptr) {
#ifndef WHISPER_STUB
    auto *ctx = (struct whisper_context *) context_ptr;
    if (ctx != nullptr) {
        whisper_free(ctx);
        LOGI("Whisper context freed");
    }
#else
    LOGI("Whisper stub mode - nothing to free");
#endif
}

} // extern "C"
