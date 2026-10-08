package com.jarvis.local;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;

public final class MainActivity extends Activity {
    private static final int REQ_AUDIO = 7001;
    private static final int REQ_SPEECH = 7002;
    private static final int REQ_NOTIF = 7003;
    private JarvisBrain brain;
    private VoiceManager voice;
    private TextView log;
    private EditText input;
    private TextView mode;
    private android.widget.Button background;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        brain = new JarvisBrain(this);
        voice = new VoiceManager(this);
        requestAudioPermission();
        requestNotificationPermission();
        buildUi();
    }

    private void requestAudioPermission() {
        if (Build.VERSION.SDK_INT >= 23 && checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, REQ_AUDIO);
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= 33 && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQ_NOTIF);
    }

    private void buildUi() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(20, 18, 20, 18);
        root.setBackgroundColor(0xFF05070B);

        TextView title = new TextView(this);
        title.setText("J A R V I S   //   LOCAL CORE");
        title.setTextColor(0xFF9DEBFF);
        title.setTextSize(18);
        title.setGravity(Gravity.CENTER);
        root.addView(title, new LinearLayout.LayoutParams(-1, 48));

        mode = new TextView(this);
        mode.setText("● ONLINE  •  LOCAL  •  ARMED");
        mode.setTextColor(0xFF67E7A7);
        mode.setTextSize(10);
        mode.setGravity(Gravity.CENTER);
        root.addView(mode, new LinearLayout.LayoutParams(-1, 30));

        root.addView(new JarvisView(this), new LinearLayout.LayoutParams(-1, 0, 1));

        log = new TextView(this);
        log.setText("JARVIS online. Local command brain ready.\nBackground listening is user-controlled.");
        log.setTextColor(0xFFD9F6FF);
        log.setTextSize(13);
        log.setPadding(10, 8, 10, 8);
        root.addView(log, new LinearLayout.LayoutParams(-1, 100));

        input = new EditText(this);
        input.setHint("Speak or type a command…");
        input.setHintTextColor(0xFF62808C);
        input.setTextColor(0xFFEFFFFF);
        input.setSingleLine(true);
        GradientDrawable field = new GradientDrawable();
        field.setColor(0xFF0A1016);
        field.setStroke(2, 0xFF174E61);
        field.setCornerRadius(18);
        input.setBackground(field);
        root.addView(input, new LinearLayout.LayoutParams(-1, 58));

        LinearLayout row1 = new LinearLayout(this);
        row1.setGravity(Gravity.CENTER);
        android.widget.Button talk = HudButton.make(this, "MIC / VOICE");
        android.widget.Button send = HudButton.make(this, "SEND");
        row1.addView(talk, weight());
        row1.addView(send, weight());
        root.addView(row1, new LinearLayout.LayoutParams(-1, 62));

        LinearLayout row2 = new LinearLayout(this);
        row2.setGravity(Gravity.CENTER);
        background = HudButton.make(this, "BACKGROUND LISTENING");
        android.widget.Button settings = HudButton.make(this, "ANDROID SETTINGS");
        row2.addView(background, weight());
        row2.addView(settings, weight());
        root.addView(row2, new LinearLayout.LayoutParams(-1, 62));

        send.setOnClickListener(v -> run(input.getText().toString()));
        talk.setOnClickListener(v -> startSpeech());
        background.setOnClickListener(v -> toggleBackground());
        settings.setOnClickListener(v -> startActivity(new Intent(Settings.ACTION_SETTINGS)));
        setContentView(root);
    }

    private LinearLayout.LayoutParams weight() { return new LinearLayout.LayoutParams(0, 52, 1); }

    private void toggleBackground() {
        if (Build.VERSION.SDK_INT >= 23 && checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestAudioPermission();
            return;
        }
        Intent service = new Intent(this, JarvisListeningService.class);
        if (isBackgroundActive()) {
            service.setAction(JarvisListeningService.ACTION_STOP);
            startService(service);
            background.setText("BACKGROUND LISTENING");
            mode.setText("● ONLINE  •  LOCAL  •  STANDBY");
            mode.setTextColor(0xFFF0C96D);
            log.setText("JARVIS: Background listener stopped.");
        } else {
            service.setAction(JarvisListeningService.ACTION_START);
            if (Build.VERSION.SDK_INT >= 26) startForegroundService(service); else startService(service);
            background.setText("STOP BACKGROUND LISTENING");
            mode.setText("● ONLINE  •  LOCAL  •  BACKGROUND ARMED");
            mode.setTextColor(0xFF67E7A7);
            log.setText("JARVIS: Background listener armed. Keep the persistent notification visible.");
        }
    }

    private boolean isBackgroundActive() {
        android.app.ActivityManager am = (android.app.ActivityManager) getSystemService(ACTIVITY_SERVICE);
        if (am == null) return false;
        for (android.app.ActivityManager.RunningServiceInfo s : am.getRunningServices(50)) {
            if (JarvisListeningService.class.getName().equals(s.service.getClassName())) return true;
        }
        return false;
    }

    private void run(String command) {
        if (command == null || command.trim().isEmpty()) return;
        String reply = brain.think(command);
        log.setText("YOU: " + command + "\nJARVIS: " + reply);
        voice.speak(reply);
        input.setText("");
    }

    private void startSpeech() {
        try {
            Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-IN");
            i.putExtra(RecognizerIntent.EXTRA_PROMPT, "JARVIS listening");
            startActivityForResult(i, REQ_SPEECH);
        } catch (Exception e) {
            log.setText("VOICE: Speech recognition is not available on this device.");
        }
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_SPEECH && resultCode == RESULT_OK && data != null) {
            ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (results != null && !results.isEmpty()) run(results.get(0));
        }
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        if (voice != null) voice.shutdown();
    }
}
