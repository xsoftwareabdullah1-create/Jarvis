package com.jarvis.local;

/** Local model boundary. No network/API is required by this interface. */
public interface LocalBrainEngine {
    boolean isLoaded();
    String generate(String prompt);
}
