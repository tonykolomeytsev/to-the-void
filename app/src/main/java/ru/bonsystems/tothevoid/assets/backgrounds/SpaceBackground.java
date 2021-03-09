package ru.bonsystems.tothevoid.assets.backgrounds;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.Random;

import ru.bonsystems.tothevoid.assets.game.Camera;
import ru.bonsystems.tothevoid.assets.game.GameState;
import ru.bonsystems.tothevoid.platform.GameObject;

/**
 * @author Kolomeytsev Anton
 */
public class SpaceBackground extends GameObject {

    private final int dustsCount;
    private Dust[] dusts;
    private int[] sparksPallet;
    private float dustRadius;

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
    }

    @Override
    public void render(Canvas canvas) {
        for (int i = 0; i < dusts.length; i++) {
            dusts[i].render(canvas);
        }
    }

    private class Dust extends GameObject {

        private float alpha, radius, vx, vy;
        private boolean alive;

        public Dust(boolean rand, float dustRadius){
            Random random = new Random();
            this.radius = 0.2f + dustRadius * random.nextFloat();
            if (rand) {
                this.x = Camera.getInstance().getAreaWidth() * 2f * random.nextFloat();
                this.y = Camera.getInstance().getAreaHeight() * random.nextFloat();
            } else {
                this.x = Camera.getInstance().getAreaWidth() + (Camera.getInstance().getAreaWidth() * random.nextFloat());
                this.y = Camera.getInstance().getAreaHeight() * random.nextFloat();
            }
            init();
        }

        @Override
        public void init() {
            Random random = new Random();
            (paint = new Paint()).setColor(sparksPallet[random.nextInt(sparksPallet.length)]);
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
            //if (y > Config.rndHeight) alive = false;
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


}
