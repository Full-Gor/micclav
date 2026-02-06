cmake_minimum_required(VERSION 3.22)
project(whisper-jni)

set(CMAKE_CXX_STANDARD 17)

# Path to whisper.cpp source (clone it alongside the project)
# git clone https://github.com/ggerganov/whisper.cpp ../whisper.cpp
set(WHISPER_DIR ${CMAKE_SOURCE_DIR}/../../../../whisper.cpp)

# If whisper.cpp is available, build it
if(EXISTS ${WHISPER_DIR}/whisper.cpp)
    add_library(whisper STATIC
        ${WHISPER_DIR}/whisper.cpp
        ${WHISPER_DIR}/ggml.c
        ${WHISPER_DIR}/ggml-alloc.c
        ${WHISPER_DIR}/ggml-backend.c
        ${WHISPER_DIR}/ggml-quants.c
    )
    target_include_directories(whisper PUBLIC ${WHISPER_DIR})
    target_compile_definitions(whisper PUBLIC GGML_USE_CPU)
endif()

# JNI bridge
add_library(whisper-jni SHARED whisper_jni.cpp)

if(TARGET whisper)
    target_link_libraries(whisper-jni whisper log android)
    target_include_directories(whisper-jni PRIVATE ${WHISPER_DIR})
else()
    # Stub mode - allows building without whisper.cpp for UI development
    target_compile_definitions(whisper-jni PRIVATE WHISPER_STUB)
    target_link_libraries(whisper-jni log android)
endif()
