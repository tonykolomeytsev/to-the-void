package ru.bonsystems.tothevoid.assets.game;

import android.graphics.Matrix;
import android.graphics.Paint;

import ru.bonsystems.tothevoid.platform.GameObject;

/**
 * Created by Kolomeytsev Anton on 23.02.2016.
 */
public abstract class Rocket extends GameObject implements Camera.Visible {

    @Override
    public void init() {
        (paint = new Paint()).setAntiAlias(true);
        transform = new Matrix();
    }

    @Override
    public void update(float delta) {
        transform.postTranslate(-Camera.getInstance().getX(), -Camera.getInstance().getY());
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return y;
    }
}
