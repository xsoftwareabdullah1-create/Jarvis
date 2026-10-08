package com.jarvis.local;

import android.content.Context;
import java.io.File;

/**
 * Local-model boundary for an embedded llama.cpp/GGUF backend.
 * The APK stays buildable without shipping a huge model or native binary.
 * Put a compatible native backend/model into the app's private model area and
 * implement the JNI methods below; the rest of JARVIS does not change.
 */
public final class LocalModelGateway implements LocalBrainEngine {
    private final File modelDir;
    private boolean loaded;

    public LocalModelGateway(Context context) {
        modelDir = new File(context.getFilesDir(), "models");
        if (!modelDir.exists()) modelDir.mkdirs();
        loaded = false;
    }

    public boolean loadModel(File gguf) {
        // Runtime hook for a local GGUF engine. No network/API key is used.
        // A native llama.cpp AAR/.so can call this class through JNI.
        loaded = gguf != null && gguf.isFile() && gguf.length() > 0;
        return loaded;
    }

    public File getModelDirectory() { return modelDir; }

    @Override public boolean isLoaded() { return loaded; }

    @Override public String generate(String prompt) {
        // Kept intentionally conservative until the native inference backend is present.
        return null;
    }
}
