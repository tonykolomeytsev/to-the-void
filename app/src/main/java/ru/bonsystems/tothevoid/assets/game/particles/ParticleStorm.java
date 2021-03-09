package ru.bonsystems.tothevoid.assets.game.particles;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.Random;

import ru.bonsystems.tothevoid.assets.game.Camera;
import ru.bonsystems.tothevoid.assets.game.GameState;
import ru.bonsystems.tothevoid.assets.utils.Pallete;
import ru.bonsystems.tothevoid.platform.Config;
import ru.bonsystems.tothevoid.platform.GameObject;

/**
 * Created by Kolomeytsev Anton on 25.02.2016.
 */
public class ParticleStorm extends GameObject {

    private Particle[] particles;
    private static Random random = new Random();
    private int particleCount;

    @Override
    public void init() {
        paint = new Paint();
        paint.setColor(Pallete.ACCENT_LIGHT);
        paint.setAlpha(48);
        particles = new Particle[30];
        for (int i = 0; i < particles.length; i++) {
            particles[i] = new Particle(
                    Config.RENDER_WIDTH,
                    Config.RENDER_HEIGHT * random.nextFloat(),
                    50f,
                    1000f + random.nextFloat() * 300
            );
            for (int j = 0; j < i; j++) {
                particles[i].x -= (particles[i].speed + particles[i].speed * GameState.gameSpeed / 9f) * (1f / 30f);
            }
        }
    }

    @Override
    public void update(float delta) {
        particleCount = (int) (GameState.gameSpeed * 7f);
        particleCount = (particleCount <= particles.length)? particleCount : particles.length;
        for (int i = 0; i < particleCount; i++) {
            if (!particles[i].isAlive()) particles[i] = new Particle(
                    Config.RENDER_WIDTH,
                    Config.RENDER_HEIGHT * random.nextFloat(),
                    100f,
                    1500f + random.nextFloat() * 300f
            );
            particles[i].x -= (particles[i].speed + particles[i].speed * GameState.gameSpeed / 9f) * delta;
        }
    }

    @Override
    public void render(Canvas canvas) {
        for (int i = 0; i < particleCount; i++) {
            canvas.drawRect(particles[i].x, particles[i].y - Camera.getInstance().getY(), particles[i].x + particles[i].size, particles[i].y + 3f - Camera.getInstance().getY(), paint);
        }
    }

    class Particle {
        public float x;
        public float y;
        public float size;

        public Particle(float x, float y, float size, float speed) {
            this.x = x;
            this.speed = speed;
            this.size = size;
            this.y = y;
        }

        public float speed;

        public boolean isAlive() {
            return x > 0;
        }
    }

}
