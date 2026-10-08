package com.jarvis.local;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import java.util.Random;

/** Interactive blue reactor HUD: touch/drag rotates the hologram. */
public final class JarvisView extends View {
    private final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint stroke = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Random random = new Random(7);
    private final float[] px = new float[48], py = new float[48];
    private float rotation, lastX;
    private long start = SystemClock.uptimeMillis();

    public JarvisView(Context c) {
        super(c);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        stroke.setStyle(Paint.Style.STROKE);
        stroke.setColor(0xFF51D9FF);
        for (int i=0;i<px.length;i++) { px[i]=random.nextFloat(); py[i]=random.nextFloat(); }
    }

    @Override protected void onDraw(Canvas c) {
        int w=getWidth(), h=getHeight();
        c.drawColor(0xFF02050A);
        float cx=w*.5f, cy=h*.40f, t=(SystemClock.uptimeMillis()-start)/1000f;
        p.setShader(new RadialGradient(cx,cy,190,new int[]{0xCC6BE7FF,0x5528AFFF,0x00000000},null,Shader.TileMode.CLAMP));
        c.drawCircle(cx,cy,190,p); p.setShader(null);
        for(int i=0;i<48;i++) {
            float x=px[i]*w, y=py[i]*h;
            p.setColor(0x8847CFFF); p.setAlpha((int)(50+50*Math.abs(Math.sin(t+i)))); c.drawCircle(x,y,1.4f,p);
        }
        p.setAlpha(255);
        for(int i=0;i<7;i++) {
            float r=58+i*23;
            c.save(); c.rotate(rotation+(i%2==0?1:-1)*(t*18+i*15),cx,cy);
            stroke.setStrokeWidth(i==0?3:1.4f); stroke.setAlpha(90+i*20);
            c.drawArc(cx-r,cy-r,cx+r,cy+r,12,235,false,stroke);
            c.drawArc(cx-r,cy-r,cx+r,cy+r,245,72,false,stroke);
            c.restore();
        }
        float pulse=(float)(Math.sin(t*4)*4);
        p.setShader(new RadialGradient(cx,cy,62,new int[]{0xFFFFFFFF,0xFF67DFFF,0xFF008DFF,0x00008DFF},null,Shader.TileMode.CLAMP));
        c.drawCircle(cx,cy,52+pulse,p); p.setShader(null);
        p.setColor(0xFF041522); c.drawCircle(cx,cy,34,p);
        p.setColor(0xFFD9FAFF); c.drawCircle(cx,cy,8,p);
        // scan beam
        stroke.setStrokeWidth(2); stroke.setAlpha(90); stroke.setColor(0xFF45D8FF);
        c.drawLine(cx-150,cy,cx+150,cy,stroke);
        postInvalidateDelayed(16);
    }

    @Override public boolean onTouchEvent(MotionEvent e) {
        if(e.getAction()==MotionEvent.ACTION_DOWN){ lastX=e.getX(); return true; }
        if(e.getAction()==MotionEvent.ACTION_MOVE){ rotation += (e.getX()-lastX)*.7f; lastX=e.getX(); invalidate(); return true; }
        return true;
    }
}
