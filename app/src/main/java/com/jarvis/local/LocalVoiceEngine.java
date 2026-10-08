package com.jarvis.local;

/** Voice boundary for a later embedded neural TTS model. */
public interface LocalVoiceEngine {
    boolean isLoaded();
    void speak(String text);
    void stop();
}
