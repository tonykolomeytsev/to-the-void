package ru.bonsystems.tothevoid.assets.transitions;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.Random;

import ru.bonsystems.tothevoid.platform.Config;
import ru.bonsystems.tothevoid.platform.GameObject;
import ru.bonsystems.tothevoid.platform.GameScreen;
import ru.bonsystems.tothevoid.platform.extend.Transition;

/**
 * Created by Kolomeytsev Anton on 31.12.2015.
 */
public class StartTransition extends Transition {
    private final int time2;
    private final int time1;
    private Paint paint;
    private float alpha = 0f;
    private float timer = 0f;
    private boolean opacityTransition = false;
    private Canvas osc;
    private ArrayList<Circle> circles = new ArrayList<>();

    public StartTransition() {
        (paint = new Paint()).setAlpha((int) (255 * alpha));
        time1 = 1000;
        time2 = 520;
    }

    @Override
    public void setScreens(GameScreen oldScreen, GameScreen newScreen) {
        super.setScreens(oldScreen, newScreen);
        osc = new Canvas(oldScreen.getBuffer());
    }

    @Override
    public void update(final float delta) {
        delay(delta, time1, new Runnable() {
            @Override
            public void run() {
                goOpacityTransition();
            }
        });
        timer += delta;
        if (timer > 1f / 25f) {
            timer = 0f;
            circles.add(new Circle());
        }
        if (opacityTransition) {
            alpha = Math.min(alpha + 2f * delta, 1f);
            paint.setAlpha((int) (255 * alpha));
            if (alpha == 1f) finish();

        }
        updateCircles(delta);
        oldScreen.update(delta);
        newScreen.update(delta);
    }

    private void goOpacityTransition() {
        alpha = 0f;
        paint.setAlpha(0);
        opacityTransition = true;
    }

    @Override
    public void render() {
        oldScreen.render();
        renderCircles(osc);
        newScreen.render();
    }

    @Override
    public void renderTo(Canvas canvas) {
        canvas.drawBitmap(oldScreen.getBuffer(), oldScreen.getTransform(), oldScreen.getPaint());
        if (alpha > 0f) canvas.drawBitmap(newScreen.getBuffer(), newScreen.getTransform(), paint);
    }

    private void renderCircles(Canvas canvas) {
        for (Circle circle : circles) {
            circle.render(canvas);
        }
    }

    private void updateCircles(float delta) {
        for (Circle circle : circles) {
            circle.update(delta);
        }
    }

    private class Circle extends GameObject {

        private float x, y, r;
        private Paint paint;

        @Override
        public void init() {
            Random random = new Random();
            x = (random.nextFloat() * Config.RENDER_WIDTH * 0.7f) + Config.RENDER_WIDTH * 0.15f;
            y = (random.nextFloat() * Config.RENDER_HEIGHT * 0.7f) + Config.RENDER_HEIGHT * 0.15f;
            r = 0f;

            (paint = new Paint()).setColor(Color.rgb(0, 0, 0));
        }

        @Override
        public void update(float delta) {
            r += 75f * delta;
            r *= 1.2;
            if (r > Config.RENDER_WIDTH) r = Config.RENDER_WIDTH;
        }

        @Override
        public void render(Canvas canvas) {
            canvas.drawCircle(x, y, r, paint);
        }
    }
}
