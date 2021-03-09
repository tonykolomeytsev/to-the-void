package ru.bonsystems.tothevoid.assets.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import ru.bonsystems.tothevoid.assets.game.particles.FireEffect;
import ru.bonsystems.tothevoid.assets.game.particles.SmokeEffect;
import ru.bonsystems.tothevoid.assets.utils.Vector3f;
import ru.bonsystems.tothevoid.platform.Config;

/**
 * Created by Kolomeytsev Anton on 23.02.2016.
 */
public class Player extends Rocket {

    private Vector3f[] collisionPoints;
    private float acceleration;
    private SmokeEffect smokeEffect;
    private FireEffect fireEffect;

    @Override
    public void init() {
        super.init();
        x = Config.RENDER_WIDTH / 4;
        y = Camera.getInstance().getAreaHeight() / 2;
        acceleration = 0f;
        Camera.getInstance().setDirection(this);
        texture = loadTextureFromAssets("game/player.png");
        collisionPoints = new Vector3f[]{new Vector3f(), new Vector3f(), new Vector3f()};
        smokeEffect = new SmokeEffect(this);
        fireEffect = new FireEffect(this);
        x = Config.RENDER_WIDTH / 4;
    }

    @Override
    public void update(float delta) {
        fireEffect.update(delta);
        smokeEffect.update(delta);

        transform.setTranslate(x, y);
        transform.preRotate(acceleration, 0f, texture.getHeight() / 2f);
        Camera.getInstance().update(delta);
        setUpCollisionPoints();
        super.update(delta);
    }

    private void setUpCollisionPoints() {
        collisionPoints[0].set(x, y, 0f);
        collisionPoints[1].set(x, y + texture.getHeight(), 0f);
        collisionPoints[2].set(x + texture.getWidth(), y + texture.getHeight() / 2f, 0f);
    }

    public void setPosition(float x, float y) {
        //this.x = x;
        if (y < 20f) y = 20f;
        if (y > Camera.getInstance().getAreaHeight() - texture.getHeight() - 20f)
            y = Camera.getInstance().getAreaHeight() - texture.getHeight() - 20f;
        this.y = y;
    }

    public void setX(float x) {
        this.x = x;
    }

    public Vector3f[] getCollisionPoints() {
        return collisionPoints;
    }

    @Override
    public void render(Canvas canvas) {
        super.render(canvas);
        smokeEffect.render(canvas);
        fireEffect.render(canvas);
        if (Config.debugMode) drawDebug(canvas);
    }

    public Player setAcceleration(float acceleration) {
        this.acceleration = acceleration;
        return this;
    }

    private Paint red_paint = new Paint();
    private void drawDebug(Canvas canvas) {
        red_paint.setColor(Color.RED);
        for (int i = 0; i < collisionPoints.length; i++){
            canvas.drawCircle(collisionPoints[i].getX(), collisionPoints[i].getY() - Camera.getInstance().getY(), 3, red_paint);
            canvas.drawText("(" + collisionPoints[i].getX() + ";" + collisionPoints[i].getY() + ")", collisionPoints[i].getX(), collisionPoints[i].getY() - Camera.getInstance().getY(), red_paint);
        }
    }

    public Paint getPaint() {
        return paint;
    }
}
