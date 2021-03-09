package ru.bonsystems.tothevoid.assets;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Random;

import ru.bonsystems.tothevoid.platform.Config;
import ru.bonsystems.tothevoid.platform.Controller;
import ru.bonsystems.tothevoid.platform.GameObject;
import ru.bonsystems.tothevoid.platform.GameScreen;

/**
 * Created by Kolomeytsev Anton on 26.12.2015.
 */
public class LoadingScreen extends GameScreen {

    private GameObject loading;
    private int[] sparksPallet;
    private ArrayList<Dust> dusts;

    public LoadingScreen(){

    }

    @Override
    public void init() {
        loading = new GameObject() {
            private float incrementOpacity = 0f;
            private float opacity = 1f;

            @Override
            public void init() {
                paint = new Paint();
                paint.setAntiAlias(true);
                texture = loadTextureFromAssets("loading.png");
                transform = new Matrix();
                transform.setTranslate(
                        Config.RENDER_WIDTH / 2 - texture.getWidth() / 2,
                        Config.RENDER_HEIGHT / 2 - texture.getHeight() / 2
                );
            }

            @Override
            public void update(float delta) {
                opacity = (float) Math.abs(Math.sin(incrementOpacity));
                incrementOpacity += 5f * delta;
                paint.setAlpha((int) (255 * opacity));
            }

            @Override
            public void render(Canvas canvas) {
                canvas.drawBitmap(texture, transform, paint);
            }
        };
        dusts = new ArrayList<>();
        sparksPallet = new int[]{
                Color.parseColor("#A72E66"),
                Color.parseColor("#F95E6B"),
                Color.parseColor("#FDA26D"),
                Color.parseColor("#FECD6D"),
                Color.parseColor("#31D39E")
        };
        for (int i = 0; i < 300; i++) {
            dusts.add(new Dust());
        }
    }

    @Override
    public void update(float delta) {
        loading.update(delta);
        for (int i = 0; i < dusts.size(); i++) {
            Dust dust = dusts.get(i);
            dust.update(delta);
        }
    }

    @Override
    public void render() {
        canvas.drawColor(Color.BLACK);
        loading.render(canvas);
        for (int i = 0; i < dusts.size(); i++) {
            dusts.get(i).render(canvas);
        }
    }

    @Override
    public void onShow() {
        Controller.getInstance().getPresenter().setOnTouchListener(null);
        System.out.println("onShow: Loading screen");
    }

    @Override
    public void onHide() {
        System.out.println("onHide: Loading screen");
    }

    @Override
    public boolean onPop() {
        return false;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }

    private class Dust extends GameObject {

        private float alpha, alphaInc, alphaSpd, radius, vx, vy, r2;

        @Override
        public void init() {
            Random random = new Random();
            (paint = new Paint()).setColor(sparksPallet[random.nextInt(sparksPallet.length)]);
            paint.setAntiAlias(true);
            this.radius = 0.1f + 5f * random.nextFloat();
            this.x = Config.RENDER_WIDTH * random.nextFloat();
            this.y = Config.RENDER_HEIGHT * random.nextFloat();
            vx = ((random.nextFloat() * 2f) - 1f) * 5f;
            vy = ((random.nextFloat() * 2f) - 1f) * 5f;
            this.alpha = 0.2f + (0.5f * random.nextFloat());
            alphaInc = 1f;
            alphaSpd = random.nextFloat() * 5f;
            r2 = len(0f, 0f,
                    Config.RENDER_HEIGHT /*Я не ошибся, тут height*/ / 2f,
                    Config.RENDER_HEIGHT / 2f
            ) / 3f;
        }

        private float len(float x1, float y1, float x2, float y2) {
            float dx = (x2 - x1);
            float dy = (y2 - y1);
            return dx * dx + dy * dy;
        }

        @Override
        public void update(float delta) {
            alphaInc += delta * alphaSpd;
            alpha = (float) Math.abs(Math.sin(alphaInc));
            float k = len(x - (Config.RENDER_WIDTH - Config.RENDER_HEIGHT) / 2f, y, Config.RENDER_HEIGHT / 2f, Config.RENDER_HEIGHT / 2f) / r2;
            alpha *= 1f - ((k <= 1f)?k:1f);
            paint.setAlpha((int) (alpha * 255));
            x += vx * delta * 5f;
            y += vy * delta * 5f;
            if (x < 0 || x > Config.RENDER_WIDTH) vx = -vx;
            if (y < 0 || y > Config.RENDER_HEIGHT) vy = -vy;
        }


        @Override
        public void render(Canvas canvas) {
            if (alpha > 0f) canvas.drawCircle(x, y, radius, paint);
        }

    }
}
