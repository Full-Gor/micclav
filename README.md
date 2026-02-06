# Micclav Keyboard

Clavier Android custom multilingue avec dictée vocale 100% offline. Zéro dépendance Google.

## Langues supportées

| Langue | Layout | Script | Dictée vocale |
|--------|--------|--------|---------------|
| **Français** (priorité) | AZERTY | Latin + accents (é,è,ê,ç,à,ù…) | ✅ Whisper FR |
| **Arabe** | Standard arabe | Arabe (RTL) | ✅ Whisper AR |
| **English** | QWERTY | Latin | ✅ Whisper EN |
| **Tachelhit Souss** | Custom | ⵜⵉⴼⵉⵏⴰⵖ (Tifinagh IRCAM) | ✅ via AR |
| **Tachelhit Souss** | AZERTY adapté | Latin (ɛ,ɣ,ḍ,ṛ,ṣ,ṭ,ẓ…) | ✅ via AR |

## Fonctionnalités

- **Clavier IME complet** — remplace le clavier natif Android
- **Dictée vocale offline** — Whisper.cpp en local, aucun serveur, fonctionne sans internet
- **Correction d'articulation** — matching phonétique qui tolère les erreurs de prononciation/frappe
  - FR : `bato` → `bateau` (eau/au/o = même son), `farmasi` → `pharmacie` (ph/f)
  - Nasales : `an/en/am/em`, `in/ain/ein`, `on/om` regroupés
  - Consonnes muettes finales gérées
- **Proximité clavier** — corrige les fautes de frappe par touches adjacentes (AZERTY/QWERTY)
- **Appui long** — accents et variantes sur chaque touche (ex: e → é,è,ê,ë,€)
- **Bascule de langue** — touche 🌐 pour switcher entre les 5 claviers
- **Support RTL** — arabe affiché de droite à gauche
- **Suggestions** — barre de suggestions en haut du clavier
- **Vibration haptique** — retour tactile sur chaque touche

## Stack technique

| Composant | Technologie |
|-----------|-------------|
| Plateforme | Android (minSdk 26 / Android 8.0+) |
| Langage | **Kotlin** |
| Build | Gradle 8.5 / AGP 8.2.2 |
| Service clavier | `InputMethodService` (IME Android) |
| Dictée vocale | **Whisper.cpp** via JNI/NDK (C++17) |
| Autocorrection | Phonétique custom + Damerau-Levenshtein + proximité clavier |
| UI | Views Android programmatiques (pas de XML layout) |
| JDK | Java 17 |

## Dépendances

```
androidx.core:core-ktx:1.12.0
androidx.appcompat:appcompat:1.6.1
com.google.android.material:material:1.11.0
androidx.constraintlayout:constraintlayout:2.1.4
kotlinx-coroutines-android:1.7.3
androidx.viewpager2:viewpager2:1.0.0
androidx.preference:preference-ktx:1.2.1
```

**Native (C++) :**
- [whisper.cpp](https://github.com/ggerganov/whisper.cpp) — STT offline
- Modèle : `ggml-base.bin` (~150 MB)

## Architecture

```
com.micclav.keyboard/
├── core/               → IME service, modèles de données (Key, KeyboardLanguage)
├── layouts/            → Définition des claviers (FR, AR, EN, Tachelhit x2, chiffres, symboles)
├── autocorrect/        → Moteur de correction (PhoneticMatcher, FuzzyMatcher)
├── voice/              → Whisper engine (enregistrement + transcription)
├── dictionaries/       → Gestion des dictionnaires (assets + fallback intégré)
└── ui/                 → KeyboardView (rendu clavier) + SettingsActivity
```

## Installation

```bash
# 1. Setup (clone whisper.cpp + télécharge le modèle vocal)
./setup.sh

# 2. Build
./gradlew assembleDebug

# 3. Installer sur téléphone connecté (USB debug activé)
./gradlew installDebug
```

Puis sur le téléphone :
1. Paramètres → Langues et saisie → Clavier → Gérer les claviers
2. Activer **Micclav**
3. Changer le clavier actif pour **Micclav**

## Utilisation

| Action | Résultat |
|--------|----------|
| Appui touche | Saisie du caractère |
| Appui long | Popup accents/variantes |
| 🌐 | Changer de langue |
| 🎤 | Dictée vocale (offline) |
| 123 | Basculer chiffres/symboles |
| ⇧ | Majuscule (double tap = caps lock) |
