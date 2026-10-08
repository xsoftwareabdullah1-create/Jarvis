package com.jarvis.local;

import android.content.Context;
import android.content.Intent;
import android.hardware.camera2.CameraManager;
import android.media.AudioManager;
import android.net.Uri;
import android.provider.Settings;
import android.content.ComponentName;
import android.content.pm.PackageManager;

public final class AndroidTools {
    private final Context context;
    private boolean flashOn;
    public AndroidTools(Context context) { this.context = context; }

    public String execute(String command) {
        String c = command.toLowerCase().trim();
        try {
            if (c.contains("flash") || c.contains("torch")) {
                CameraManager cm = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
                String id = cm.getCameraIdList()[0];
                flashOn = !flashOn;
                cm.setTorchMode(id, flashOn);
                return flashOn ? "Flashlight enabled." : "Flashlight disabled.";
            }
            if (c.contains("volume")) {
                AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                if (c.contains("up")) { am.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI); return "Volume increased."; }
                if (c.contains("down")) { am.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI); return "Volume decreased."; }
            }
            if (c.contains("brightness")) {
                context.startActivity(new Intent(Settings.ACTION_DISPLAY_SETTINGS));
                return "Opening display settings for brightness control.";
            }
            if (c.contains("wifi")) {
                context.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                return "Opening Wi-Fi settings.";
            }
            if (c.contains("bluetooth")) {
                context.startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
                return "Opening Bluetooth settings.";
            }
            if (c.startsWith("open ")) {
                String app = c.substring(5).trim();
                if (app.contains("youtube")) { context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://youtube.com"))); return "Opening YouTube."; }
                if (app.contains("browser") || app.contains("chrome")) { context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com"))); return "Opening the browser."; }
                if (app.contains("settings")) { context.startActivity(new Intent(Settings.ACTION_SETTINGS)); return "Opening Android settings."; }
            }
        } catch (Exception ignored) { return "I could not complete that Android action."; }
        return null;
    }
  }
                  
