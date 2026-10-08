package com.jarvis.local;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.Button;

public final class HudButton {
    private HudButton() {}

    public static Button make(Context context, String label) {
        Button b = new Button(context);
        b.setText(label);
        b.setAllCaps(false);
        b.setTextSize(11);
        b.setTextColor(0xFF9DEBFF);
        b.setGravity(Gravity.CENTER);
        b.setPadding(8, 2, 8, 2);
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(0x00101820);
        bg.setStroke(2, 0xFF1E7898);
        bg.setCornerRadius(18);
        b.setBackground(bg);
        b.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) v.animate().scaleX(.94f).scaleY(.94f).setDuration(70).start();
            if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL)
                v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
            return false;
        });
        return b;
    }
}
