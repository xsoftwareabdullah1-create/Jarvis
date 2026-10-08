# Neural cinematic voice slot

The included voice engine is an original low-pitch UK cinematic Android-TTS
preset so the APK remains small and buildable. A neural local TTS backend
(Kokoro/Piper/ONNX-style) can be wired to the same `VoiceManager` interface.
No Iron Man actor/movie voice is bundled or cloned.

A neural model should be kept out of GitHub when it is large; install it into
app-private storage and select it at runtime.
