package com.jarvis.local;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import java.util.Locale;

/** Original cinematic voice preset. Does not clone a movie/actor voice. */
public final class CinematicVoiceManager implements TextToSpeech.OnInitListener {
    private final TextToSpeech tts;
    private boolean ready;
    public CinematicVoiceManager(Context context) {
        tts = new TextToSpeech(context.getApplicationContext(), this);
    }
    @Override public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            tts.setLanguage(Locale.UK);
            tts.setPitch(0.62f);
            tts.setSpeechRate(0.82f);
            ready = true;
        }
    }
    public void speak(String text) {
        if (ready) tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "jarvis_cinematic");
    }
    public void shutdown() { tts.stop(); tts.shutdown(); }
}
