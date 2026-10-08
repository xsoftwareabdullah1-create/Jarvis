# JARVIS Ultimate Mobile — GitHub APK Build

A native Android JARVIS-style agent foundation designed for phone-only development.

### Included
- 🔵 Interactive blue Arc-Reactor hologram
- 🌀 Multi-ring 3D-style HUD, particles, scan beam and drag rotation
- 🎙️ Android speech recognition
- 🔊 Original deep cinematic UK voice preset
- 🧠 Local command brain + persistent memory
- 💻 Self-coding workspace agent (folder/file/code creation inside app sandbox)
- 📱 Android controls through supported APIs
- ⚙️ Foreground background-listening foundation
- 🧩 Local GGUF/llama.cpp integration boundary
- 🗣️ Neural TTS integration boundary
- 🚀 GitHub Actions release build
- 📦 Output is **Jarvis.apk**, not debug.apk

### Important truth
The project contains the integration architecture for local neural LLM and neural
TTS, but the large GGUF/TTS model files and native inference libraries are not bundled.
That keeps the repository buildable and avoids pretending that a tiny APK contains a
full foundation model. The included command brain and coding/file agent work without
an API key.

### GitHub
Upload the contents of this folder to a repository. Push to any branch or run the
workflow manually. GitHub Actions creates an artifact named `Jarvis` containing:
`Jarvis.apk`
