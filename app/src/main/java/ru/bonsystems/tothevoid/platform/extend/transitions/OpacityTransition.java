package ru.bonsystems.tothevoid.platform.extend.transitions;

import android.graphics.Canvas;
import android.graphics.Paint;

import ru.bonsystems.tothevoid.platform.extend.Transition;

/**
 * Created by Kolomeytsev Anton
 */
public class OpacityTransition extends Transition {
    private Key[] keys;
    private Paint paint;
    private int n;
    private Runnable opacityIncrementRunnable;

    public OpacityTransition(Key[] keys){
        paint = new Paint();
        this.keys = keys;
        this.type = TransitionType.opacity;
    }

    public static Transition getBasic() {
        return new OpacityTransition(
            new Key[]{
                new Key(0, 0),
                new Key(40, 20),
                new Key(40, 40),
                new Key(40, 60),
                new Key(40, 80),
                new Key(40, 100),
                new Key(40, 120),
                new Key(40, 140),
                new Key(40, 160),
                new Key(40, 180),
                new Key(40, 200),
                new Key(40, 220),
                new Key(40, 240),
                new Key(40, 255)
            }
        );
    }

    @Override
    public void firstUpdate(float delta) {
        paint.setAlpha(keys[0].opacity);
        n = 0;
        opacityIncrementRunnable = new Runnable() {
            @Override
            public void run() {
                paint.setAlpha(keys[n].opacity);
                n++;
            }
        };
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        if (n < keys.length) delay(delta, keys[n].time, opacityIncrementRunnable);
        else finish();

        oldScreen.update(delta);
        newScreen.update(delta);
    }

    @Override
    public void render() {
        oldScreen.render();
        newScreen.render();
    }

    @Override
    public void renderTo(Canvas canvas) {
        canvas.drawBitmap(oldScreen.getBuffer(), oldScreen.getTransform(), oldScreen.getPaint());
        canvas.drawBitmap(newScreen.getBuffer(), newScreen.getTransform(), paint);
    }

    public static class Key {
        public int time;
        public int opacity;

        public Key(int time, int opacity){
            this.time = time;
            this.opacity = opacity;
        }
    }

    @Override
    protected void finish() {
        super.finish();
    }
}
