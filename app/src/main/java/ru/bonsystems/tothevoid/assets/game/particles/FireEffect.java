package ru.bonsystems.tothevoid.assets.game.particles;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.Random;

import ru.bonsystems.tothevoid.assets.game.Camera;
import ru.bonsystems.tothevoid.assets.game.Rocket;
import ru.bonsystems.tothevoid.platform.GameObject;

/**
 * Created by Kolomeytsev Anton on 24.02.2016.
 */
public class FireEffect extends GameObject {
    private Sparcle[] train = new Sparcle[4];
    private Rocket parent;
    private float halfHeightTranslation = 16f;

    public FireEffect(Rocket parent) {
        this.parent = parent;
        initTrain(); // TRAIN переводится как "шлейф", а не только как "поезд".
    }

    private void initTrain() {
        for (int i = 0; i < train.length; i++) {
            train[i] = new Sparcle();
            train[i].set((int) (parent.getX() - (2 * i)), (int) (parent.getY() + halfHeightTranslation), (6 - (i * 2)));
        }
    }

    @Override
    public void render(Canvas canvas) {
        for (int i = 0; i < train.length; i++) {
            train[i].render(canvas);
        }
    }

    @Override
    public void update(float delta) {
        for (int i = 0; i < train.length; i++) {
            train[i].update(delta);
            if ((train[i].getRadius() == 0)) {
                train[i].set((int) parent.getX(), (int) (parent.getY() + halfHeightTranslation), 9);
            }
        }
    }

    public FireEffect setHalfHeightTranslation(int halfHeightTranslation) {
        this.halfHeightTranslation = halfHeightTranslation;
        return this;
    }

    class Sparcle {
        private int x;
        private int y;
        public float radius;
        private int xSpeed;
        private int ySpeed;
        private Paint paint = new Paint();
        private float buffer_radius_increment;
        private Random rnd = new Random();

        public Sparcle() {
            paint.setARGB(255, 234, 179, 89);
            paint.setAntiAlias(true);
            buffer_radius_increment = 0.71f;
            xSpeed = 2 * 30;
            ySpeed = 0;
        }

        public void set(int X, int Y, int radius) {
            this.x = X;
            this.y = Y;
            this.radius = radius;
        }

        public float getRadius() {
            return radius;
        }

        public void render(Canvas canvas) {
            canvas.drawCircle(x, y - Camera.getInstance().getY(), radius, paint);
        }

        public void update(float delta) { /* обновляет состояние, то есть меняет координаты, градус поворота и т.д. */
            x -= (xSpeed * delta);
            radius -= buffer_radius_increment;
            if (radius < 0) radius = 0;
        }
    }
}
