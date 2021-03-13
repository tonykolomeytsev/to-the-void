package ru.bonsystems.tothevoid.assets.backgrounds;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.Random;

import ru.bonsystems.tothevoid.assets.game.Camera;
import ru.bonsystems.tothevoid.assets.game.GameState;
import ru.bonsystems.tothevoid.assets.utils.Pallete;
import ru.bonsystems.tothevoid.platform.GameObject;

/**
 * @author Kolomeytsev Anton
 */
public class SpaceBackground extends GameObject {

    private final static Random random = new Random();
    private final static Camera camera = Camera.getInstance();
    private final int dustsCount;
    private Dust[] dusts;
    private int[] sparksPallet;
    private final float dustRadius;
    private Flashlight flashlight;

    public SpaceBackground(float dustRadius, int dustsCount) {
        this.dustRadius = dustRadius;
        this.dustsCount = dustsCount;
        init();
    }

    @Override
    public void init() {
        dusts = new Dust[dustsCount];
        sparksPallet = new int[]{
                Color.parseColor("#CFDAE7"),
                Color.parseColor("#7B90A6"),
                Color.parseColor("#439DC1"),
                Color.parseColor("#DFB06D"),
                Color.parseColor("#9DADC8")
        };
        for (int i = 0; i < dusts.length; i++) {
            dusts[i] = new Dust(true, dustRadius);
        }
    }

    @Override
    public void update(float delta) {
        for (int i = 0; i < dusts.length; i++) {
            Dust dust = dusts[i];
            if (!dust.isAlive()) {
                dusts[i] = new Dust(false, dustRadius);
            }
            dust.update(delta);
        }

        if (flashlight != null && flashlight.isAlive()) {
            flashlight.update(delta);
        } else if (random.nextFloat() < 0.005f) {
            flashlight = new Flashlight();
        }
    }

    @Override
    public void render(Canvas canvas) {
        for (Dust dustPiece : dusts) {
            dustPiece.render(canvas);
        }
        if (flashlight != null && flashlight.isAlive()) {
            flashlight.render(canvas);
        }
    }

    private class Dust extends GameObject {
        private final float radius;
        private float alpha;
        private float vx;
        private float vy;
        private boolean alive;

        public Dust(boolean rand, float dustRadius) {
            radius = 0.2f + dustRadius * random.nextFloat();
            if (rand) {
                this.x = camera.getAreaWidth() * 2f * random.nextFloat();
            } else {
                this.x = camera.getAreaWidth() + (Camera.getInstance().getAreaWidth() * random.nextFloat());
            }
            this.y = camera.getAreaHeight() * random.nextFloat();
            init();
        }

        @Override
        public void init() {
            paint = new Paint();
            paint.setColor(sparksPallet[random.nextInt(sparksPallet.length)]);
            paint.setAntiAlias(true);
            vx = ((random.nextFloat() * 1f) - 10f) * (5f + radius) + (5f + radius) * GameState.gameSpeed / 2f;
            vy = 0f;
            this.alpha = 0.8f + (0.2f * random.nextFloat());
            alive = true;
        }

        @Override
        public void update(float delta) {
            paint.setAlpha((int) (alpha * 255));
            x += (vx * delta * 3f);
            y += (vy * delta * 5f);
            if (x < 0) alive = false;
        }

        public boolean isAlive() {
            return alive;
        }

        @Override
        public void render(Canvas canvas) {
            float translatedY = (y - Camera.getInstance().getY() * radius * 0.15f);
            canvas.drawCircle(x, translatedY, radius, paint);
        }
    }

    private class Flashlight extends GameObject {

        private int alpha = (int) (28 + 60 * random.nextFloat());

        public Flashlight() {
            paint = new Paint();
            paint.setColor(Pallete.ACCENT_LIGHT);
        }

        @Override
        public void update(float delta) {
            alpha *= 0.92f;
        }

        @Override
        public void render(Canvas canvas) {
            paint.setAlpha(alpha);
            canvas.drawPaint(paint);
        }

        public boolean isAlive() {
            return alpha > 2;
        }
    }
}
