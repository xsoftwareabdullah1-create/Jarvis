# JARVIS feature map

## Included now
- Interactive blue reactor hologram with particles, scan beam and touch rotation
- Voice input through Android SpeechRecognizer
- Original low/deep cinematic UK TTS preset
- Local command brain with persistent memory
- Coding agent: create folders, create files, write code, list workspace
- App-private coding workspace
- Android actions: flashlight, volume, Wi-Fi/Bluetooth/display/settings, browser/YouTube
- Foreground voice-listening service foundation
- GitHub Actions release build producing `Jarvis.apk`

## Local model integration point
`LocalModelGateway` is the boundary for an embedded GGUF/llama.cpp backend.
A real neural model and native inference library are intentionally not bundled
because they can be hundreds of MB and are model/license dependent.

## Android limits
System-wide automation remains permission- and API-limited. Accessibility or
other sensitive capabilities must be explicitly enabled by the user.
