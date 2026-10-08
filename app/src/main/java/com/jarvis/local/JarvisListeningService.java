package com.jarvis.local;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;

/**
 * User-enabled background listener foundation.
 * It keeps a visible Android foreground-service notification and samples
 * microphone activity while the screen is off. A true local wake-word/ASR
 * engine can be plugged into the onAudioFrame callback without changing the
 * Android service contract.
 */
public final class JarvisListeningService extends Service {
    public static final String ACTION_START = "com.jarvis.local.action.START";
    public static final String ACTION_STOP = "com.jarvis.local.action.STOP";
    public static final String ACTION_OPEN = "com.jarvis.local.action.OPEN";
    private static final String CHANNEL_ID = "jarvis_background";
    private static final int NOTIFICATION_ID = 4401;

    private volatile boolean running;
    private AudioRecord recorder;
    private Thread audioThread;

    @Override public void onCreate() {
        super.onCreate();
        createChannel();
    }

    @Override public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent == null ? ACTION_START : intent.getAction();
        if (ACTION_STOP.equals(action)) {
            stopListening();
            stopSelf();
            return START_NOT_STICKY;
        }

        if (Build.VERSION.SDK_INT >= 33 && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            // FGS may still run, but the user should grant notification permission
            // from the app for a visible status card.
        }

        Notification notification = buildNotification();
        if (Build.VERSION.SDK_INT >= 29) {
            startForeground(NOTIFICATION_ID, notification,
                    android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE);
        } else {
            startForeground(NOTIFICATION_ID, notification);
        }
        startListening();
        return START_NOT_STICKY;
    }

    private void startListening() {
        if (running) return;
        if (Build.VERSION.SDK_INT >= 23 && checkSelfPermission(Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            stopSelf();
            return;
        }

        final int sampleRate = 8000;
        final int min = AudioRecord.getMinBufferSize(sampleRate,
                AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        if (min <= 0) return;

        try {
            recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate,
                    AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT,
                    Math.max(min * 2, 4096));
            recorder.startRecording();
            running = true;
            audioThread = new Thread(() -> {
                short[] buffer = new short[Math.max(1024, min / 2)];
                while (running && recorder != null) {
                    int n = recorder.read(buffer, 0, buffer.length);
                    if (n > 0) onAudioFrame(buffer, n);
                }
            }, "JARVIS-AudioLoop");
            audioThread.start();
        } catch (Throwable ignored) {
            stopListening();
        }
    }

    private void onAudioFrame(short[] samples, int count) {
        // Lightweight voice-activity detector. It intentionally does not
        // transmit, save, or upload audio. Replace this callback with a local
        // wake-word model (Porcupine/Vosk/Whisper.cpp/etc.) later.
        double sum = 0;
        for (int i = 0; i < count; i++) {
            double v = samples[i] / 32768.0;
            sum += v * v;
        }
        double rms = Math.sqrt(sum / count);
        if (rms > 0.08) {
            long now = SystemClock.elapsedRealtime();
            if (now % 7000 < 80) {
                updateNotification("Voice activity detected • JARVIS armed");
            }
        }
    }

    private void updateNotification(String text) {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) manager.notify(NOTIFICATION_ID, buildNotification(text));
    }

    private Notification buildNotification() {
        return buildNotification("Background listening active");
    }

    private Notification buildNotification(String content) {
        Intent open = new Intent(this, MainActivity.class);
        open.setAction(ACTION_OPEN);
        PendingIntent openPi = PendingIntent.getActivity(this, 1, open,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Intent stop = new Intent(this, JarvisListeningService.class);
        stop.setAction(ACTION_STOP);
        PendingIntent stopPi = PendingIntent.getService(this, 2, stop,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Notification.Builder b = Build.VERSION.SDK_INT >= 26
                ? new Notification.Builder(this, CHANNEL_ID)
                : new Notification.Builder(this);
        return b.setContentTitle("JARVIS")
                .setContentText(content)
                .setSmallIcon(android.R.drawable.ic_btn_speak_now)
                .setOngoing(true)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setContentIntent(openPi)
                .addAction(new Notification.Action.Builder(null, "OPEN", openPi).build())
                .addAction(new Notification.Action.Builder(null, "STOP", stopPi).build())
                .build();
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel ch = new NotificationChannel(CHANNEL_ID, "JARVIS background",
                    NotificationManager.IMPORTANCE_LOW);
            ch.setDescription("Shows when JARVIS background listening is enabled.");
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) manager.createNotificationChannel(ch);
        }
    }

    private void stopListening() {
        running = false;
        if (recorder != null) {
            try { recorder.stop(); } catch (Throwable ignored) {}
            recorder.release();
            recorder = null;
        }
        audioThread = null;
    }

    @Override public void onDestroy() {
        stopListening();
        super.onDestroy();
    }

    @Override public IBinder onBind(Intent intent) { return null; }
}
