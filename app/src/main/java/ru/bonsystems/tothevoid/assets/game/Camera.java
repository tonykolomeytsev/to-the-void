package ru.bonsystems.tothevoid.assets.game;

import ru.bonsystems.tothevoid.platform.Config;
import ru.bonsystems.tothevoid.platform.GameObject;

/**
 * Created by Kolomeytsev Anton on 23.02.2016.
 */
public class Camera extends GameObject{
    private static Camera instance;
    private float expectedX, expectedY, x, y;
    private Visible direction;
    private float areaHeight;
    private float areaWidth;

    public static Camera getInstance() {
        if (instance == null) {
            instance = new Camera();
        }
        return instance;
    }

    @Override
    public void init() {
        expectedX = 0f;
        expectedY = 0f;
        x = 0f;
        y = 0f;
    }

    @Override
    public void update(float delta) {
        float surfaceWidth = Config.RENDER_WIDTH;
        float surfaceHeight = Config.RENDER_HEIGHT;
        expectedX = direction.getX() - surfaceWidth / 4f;
        expectedY = direction.getY() - surfaceHeight / 2f;
        if (expectedX < 0) expectedX = 0;
        if (expectedY < 0) expectedY = 0;
        if (expectedX + surfaceWidth > areaWidth) expectedX = areaWidth - surfaceWidth;
        if (expectedY + surfaceHeight > areaHeight) expectedY = areaHeight - surfaceHeight;
        x += (expectedX - x) / 10f;
        y += (expectedY - y) / 10f;
    }

    public Camera setArea(float width, float height) {
        this.areaWidth = width;
        this.areaHeight = height;
        return this;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getAreaWidth() {
        return areaWidth;
    }

    public float getAreaHeight() {
        return areaHeight;
    }

    public Camera setDirection(Visible direction) {
        this.direction = direction;
        return this;
    }

    interface Visible {
        float getX();
        float getY();
    }
}
