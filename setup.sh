#!/bin/bash
# Micclav Keyboard - Setup Script
# Ce script prépare l'environnement de build

set -e

echo "=== Micclav Keyboard Setup ==="
echo ""

# 1. Clone whisper.cpp if not present
if [ ! -d "whisper.cpp" ]; then
    echo "[1/3] Clonage de whisper.cpp..."
    git clone --depth 1 https://github.com/ggerganov/whisper.cpp.git
else
    echo "[1/3] whisper.cpp déjà présent ✓"
fi

# 2. Download whisper model (base, ~150MB - good balance quality/size for mobile)
MODEL_DIR="app/src/main/assets/models"
MODEL_FILE="$MODEL_DIR/ggml-base.bin"
if [ ! -f "$MODEL_FILE" ]; then
    echo "[2/3] Téléchargement du modèle Whisper (base, ~150MB)..."
    mkdir -p "$MODEL_DIR"
    # Use whisper.cpp's download script
    if [ -f "whisper.cpp/models/download-ggml-model.sh" ]; then
        cd whisper.cpp
        bash models/download-ggml-model.sh base
        cp models/ggml-base.bin "../$MODEL_FILE"
        cd ..
    else
        echo "  Téléchargement direct..."
        curl -L "https://huggingface.co/ggerganov/whisper.cpp/resolve/main/ggml-base.bin" -o "$MODEL_FILE"
    fi
    echo "  Modèle téléchargé ✓"
else
    echo "[2/3] Modèle Whisper déjà présent ✓"
fi

# 3. Check Android SDK
echo "[3/3] Vérification du SDK Android..."
if [ -z "$ANDROID_HOME" ] && [ -z "$ANDROID_SDK_ROOT" ]; then
    echo "  ⚠ ANDROID_HOME non défini."
    echo "  Installez Android Studio ou le SDK Android :"
    echo "  https://developer.android.com/studio"
    echo ""
    echo "  Puis exportez:"
    echo "  export ANDROID_HOME=\$HOME/Android/Sdk"
else
    echo "  SDK Android trouvé ✓"
fi

echo ""
echo "=== Setup terminé ==="
echo ""
echo "Pour compiler:"
echo "  ./gradlew assembleDebug"
echo ""
echo "Pour installer sur un appareil connecté:"
echo "  ./gradlew installDebug"
echo ""
echo "Ensuite sur le téléphone:"
echo "  1. Paramètres → Langues et saisie → Clavier → Gérer les claviers"
echo "  2. Activez 'Micclav'"
echo "  3. Changez le clavier actif pour 'Micclav'"
