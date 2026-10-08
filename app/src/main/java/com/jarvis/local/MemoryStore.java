package com.jarvis.local;

import android.content.Context;
import android.content.SharedPreferences;

public final class MemoryStore {
    private final SharedPreferences prefs;
    public MemoryStore(Context context) {
        prefs = context.getSharedPreferences("jarvis_memory", Context.MODE_PRIVATE);
    }
    public void remember(String value) { prefs.edit().putString("memory", value).apply(); }
    public String recall() { return prefs.getString("memory", ""); }
    public void clear() { prefs.edit().remove("memory").apply(); }
}
