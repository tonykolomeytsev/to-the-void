package ru.bonsystems.tothevoid.assets.game.particles;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.Random;

import ru.bonsystems.tothevoid.assets.game.Camera;
import ru.bonsystems.tothevoid.assets.game.GameState;
import ru.bonsystems.tothevoid.assets.game.Player;
import ru.bonsystems.tothevoid.assets.game.Rocket;
import ru.bonsystems.tothevoid.platform.GameObject;

/**
 * Created by Kolomeytsev Anton on 24.02.2016.
 */
public class SmokeEffect extends GameObject {
    private float ALPHA_DECREMENT = 15f;
    private Smoke[] train = new Smoke[14];
    private Rocket parent;
    private static Random random = new Random();
    private float halfHeightTranslation = 16f;

    public SmokeEffect(Rocket parent){
        this.parent = parent;
        if (parent instanceof Player) ALPHA_DECREMENT = 15f;
        else ALPHA_DECREMENT = 360f;
        initTrain(); // TRAIN переводится как "шлейф", а не только как "поезд".
    }

    private void initTrain(){
        for (int i = 0; i < train.length; i++){
            train[i] = new Smoke();
            train[i].fillValues();
            train[i].setCoordinates((int) parent.getX(), (int) (parent.getY() + halfHeightTranslation));
        }
    }

    @Override
    public void render(Canvas canvas) {
        for (int i = 0; i < train.length; i++){
            train[i].render(canvas);
        }
    }

    @Override
    public void update(float delta) {
        for (int i = 0; i < train.length; i++){
            train[i].update(delta);
            if ((train[i].getX() < -30) || (train[i].alpha < 10)){
                train[i].fillValues();
                train[i].setCoordinates((int) parent.getX(), (int) (parent.getY() + halfHeightTranslation));
            }
        }
    }

    public SmokeEffect setHalfHeightTranslation(float halfHeightTranslation) {
        this.halfHeightTranslation = halfHeightTranslation;
        return this;
    }

    class Smoke {
        private int x;
        private int y;
        public float radius;
        private int xSpeed;
        private int ySpeed;
        private int alpha = 255;
        private float aDecrement;
        private Paint paint = new Paint();

        public Smoke(){
            paint.setARGB(255, 189, 195, 199);
            paint.setAntiAlias(true);
            aDecrement = ALPHA_DECREMENT + (random.nextFloat() * 100f);
        }

        public void setCoordinates(int X, int Y){
            this.x = X;
            this.y = Y;
        }

        public int getX(){
            return x;
        }

        public void fillValues(){
            Random random = new Random();
            alpha = 255;
            radius = random.nextInt(3) + 5;
            xSpeed = random.nextInt(7) + 13; xSpeed *= 30;
            ySpeed = random.nextInt(4) - 2;
        }

        public void render(Canvas canvas) {
            canvas.drawCircle(x, y - Camera.getInstance().getY(), radius, paint);
        }

        public void update(float delta){ /* обновляет состояние, то есть меняет координаты, градус поворота и т.д. */
            x -= (xSpeed) * delta + ((xSpeed) * delta * GameState.gameSpeed / 2f);
            y += ySpeed;
            radius += 60f * delta;
            alpha -= aDecrement * delta;
            if (alpha < 0) alpha = 0;
            paint.setAlpha(alpha);
        }
    }

}
