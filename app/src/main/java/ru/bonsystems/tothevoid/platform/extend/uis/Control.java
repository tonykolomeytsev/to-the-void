package ru.bonsystems.tothevoid.platform.extend.uis;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

import ru.bonsystems.tothevoid.platform.Config;
import ru.bonsystems.tothevoid.platform.GameObject;

/**
 * Created by Kolomeytsev Anton
 */
public abstract class Control extends GameObject {
    protected final ArrayList<View.OnTouchListener> listeners = new ArrayList<>();
    protected Area area;
    private static Paint debugPaint = new Paint();

    public void onTouch(MotionEvent event){
        if (area == null) throw new NullPointerException("Area не определена");
        float x = event.getX() * Config.scaleFactor, y = event.getY() * Config.scaleFactor;
        if (
                x > area.x &&
                x < area.x + area.width &&
                y > area.y &&
                y < area.y + area.height
            ) {
            synchronized (listeners) {
                for (View.OnTouchListener listener : listeners) {
                    listener.onTouch(null, event);
                }
            }
        }
    }

    public Control addOnTouchListener(View.OnTouchListener listener) {
        listeners.add(listener);
        return this;
    }

    @Override
    public void render(Canvas canvas) {
//        if (Config.debugMode) {
//            debugPaint.setStyle(Paint.Style.STROKE);
//            debugPaint.setStrokeWidth(2);
//            debugPaint.setColor(Color.YELLOW);
//            canvas.drawRect(area.x, area.y, area.x + area.width, area.y + area.height, debugPaint);
//        }
    }

    public void removeOnTouchListener(View.OnTouchListener listener) {
        listeners.remove(listener);
    }

    public Control setArea(Area area) {
        this.area = area;
        return this;
    }

    public static class Area{
        public float x, y, width, height;
        public Area(float x, float y, float width, float height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
    }
}
