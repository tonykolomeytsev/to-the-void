package ru.bonsystems.tothevoid.assets.game.particles;

import android.graphics.Canvas;
import android.graphics.Paint;

import ru.bonsystems.tothevoid.assets.game.Camera;
import ru.bonsystems.tothevoid.assets.game.Player;
import ru.bonsystems.tothevoid.platform.Config;
import ru.bonsystems.tothevoid.platform.GameObject;
import ru.bonsystems.tothevoid.platform.extend.Log;

/**
 * Created by Kolomeytsev Anton on 24.02.2016.
 */
public class ExplosionEffect extends GameObject {
    private CompleteListener listener;
    private Player parent;
    private Paint ex_paint = new Paint();
    private Paint lt_paint = new Paint();
    private float radius = 10;
    private int ex_alpha = 255;
    private int lt_alpha = 128;

    public ExplosionEffect(Player player, CompleteListener listener){
        Log.i(this, "Instance");
        this.listener = listener;
        this.ex_paint.setARGB(255, 255, 255, 255); ex_paint.setAntiAlias(true);
        this.lt_paint.setARGB(128, 255, 255, 255); lt_paint.setAntiAlias(true);
        parent = player;
    }

    @Override
    public void render(Canvas canvas) {
        canvas.drawCircle(x, y, radius, ex_paint);
        canvas.drawRect(0f, 0f, Config.RENDER_WIDTH, Config.RENDER_HEIGHT, lt_paint);
    }

    @Override
    public void update(float delta) {
        if (radius < (Config.RENDER_WIDTH * 4)) {
            radius *= 1.2f * 40f * delta;
            lt_alpha *= 0.99f * 30f * delta;
            //ex_alpha *= 0.99f * 30f * delta;
        } else {
            async(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (listener != null) {
                        listener.OnExplosionComplete();
                        listener = null;
                    }
                }
            });
            parent.getPaint().setAlpha((int) (parent.getPaint().getAlpha() * 0.9f * 30f * delta));
        }
        checkStates();
        x = parent.getX() + 16f;
        y = parent.getY() + 16f - Camera.getInstance().getY();
    }

    private void checkStates() {
        ex_paint.setAlpha(ex_alpha);
        lt_paint.setAlpha(lt_alpha);
    }

    public interface CompleteListener {
        void OnExplosionComplete();
    }
}
