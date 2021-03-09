package ru.bonsystems.tothevoid.assets.game.gameplay;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.Random;

import ru.bonsystems.tothevoid.assets.game.Camera;
import ru.bonsystems.tothevoid.assets.game.Player;
import ru.bonsystems.tothevoid.assets.game.Rocket;
import ru.bonsystems.tothevoid.assets.game.particles.FireEffect;
import ru.bonsystems.tothevoid.assets.game.particles.SmokeEffect;
import ru.bonsystems.tothevoid.assets.utils.Pallete;
import ru.bonsystems.tothevoid.platform.Config;
import ru.bonsystems.tothevoid.platform.GameObject;

/**
 * Created by Kolomeytsev Anton on 22.03.2016.
 */
public class Enemy extends Rocket {
    private float elapedTime, bulletElapsedTime;
    private SmokeEffect smokeEffect;
    private FireEffect fireEffect;
    private float vX, vY;
    private Player player;
    private int halfHeight;
    private Random random = new Random();
    private ArrayList<Bullet> bullets = new ArrayList<>();
    public boolean alive = true;

    public Enemy(Player player) {
        this.player = player;
        init();
    }

    @Override
    public void init() {
        super.init();
        texture = loadTextureFromAssets("game/gameplay/enemy.png");
        halfHeight = texture.getHeight() / 2;
        (smokeEffect = new SmokeEffect(this)).setHalfHeightTranslation(halfHeight);
        (fireEffect = new FireEffect(this)).setHalfHeightTranslation(halfHeight);
        x = -texture.getWidth();
        y = Config.rndHeight / 2f;
        elapedTime = 0f;
        bulletElapsedTime = 0f;
        vX = 0;
        vY = 0;
    }

    @Override
    public void update(float delta) {
        updateScenario(delta);
        updateBullets(delta);
        x += vX;
        y += vY;
        transform.setTranslate(x, y);
        super.update(delta);
        smokeEffect.update(delta);
        fireEffect.update(delta);
    }

    private void updateBullets(float delta) {
        for (int i = 0, ie = bullets.size(); i < ie; i++) {
            Bullet bullet = bullets.get(i);
            if (bullet.isAlive()) {
                bullet.update(delta);
                if (bullet.getX() < 0) {
                    bullet.alive(false);
                }
            }
        }
    }

    private void updateScenario(float delta) {
        elapedTime += delta;
        if (elapedTime > 1000f) {
            return;
        } else if (elapedTime > 15f) {
            alive = false;
        } else if (elapedTime > 10f) {
            vX += delta * 20f;
            vY = 0f;
        } else if (elapedTime > 5f) {
            vX = 0f;
            vY = (player.getY() - y) / 20f;
            bulletElapsedTime += delta;
            if (bulletElapsedTime > 0.3f) {
                bulletElapsedTime = 0f;
                bullets.add(new Bullet(this));
            }
        } else if (elapedTime > 3f) {
            vX = ((Config.rndWidth * 0.75f) - x) / 10f;
        }
    }

    public ArrayList<Bullet> getBullets() {
        return bullets;
    }

    @Override
    public void render(Canvas canvas) {
        smokeEffect.render(canvas);
        fireEffect.render(canvas);
        super.render(canvas);
        for (Bullet bullet : bullets) {
            if (bullet.isAlive()) bullet.render(canvas);
        }
    }

    public boolean endScenario() {
        return !alive;
    }

    public class Bullet extends GameObject {
        private float radiusIncrement;
        private float xSpeed;
        private boolean alive;

        public Bullet(Rocket parent) {
            alive = true;
            (paint = new Paint()).setAntiAlias(true);
            paint.setColor(Pallete.BULLETS_COLOR);
            radiusIncrement = 0;
            x = parent.getX();
            y = parent.getY() + halfHeight;
            xSpeed = random.nextInt(7) + 13; xSpeed *= 60;
        }

        @Override
        public void update(float delta) {
            x -= xSpeed * delta;
            radiusIncrement += delta * 10f;
            if (radiusIncrement > Math.PI) radiusIncrement = 0f;
        }

        @Override
        public void render(Canvas canvas) {
            canvas.drawCircle(x, y - Camera.getInstance().getY(), 5f + (float) Math.sin(radiusIncrement) * 5f, paint);
        }

        public float getX() {
            return x;
        }

        public float getY() {
            return y;
        }

        public boolean isAlive() {
            return alive;
        }

        public void alive(boolean b) {
            alive = b;
        }
    }
}
