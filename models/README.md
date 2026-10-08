# Local AI model slot

JARVIS is designed for an offline GGUF model through `LocalModelGateway`.
Do not commit a huge model into GitHub. Put a compatible GGUF model into the
app-private `files/models` directory at runtime and connect a llama.cpp JNI/AAR
backend to `LocalModelGateway`.

The app has no AI API key requirement and does not send prompts to a cloud API
in the included command brain.
