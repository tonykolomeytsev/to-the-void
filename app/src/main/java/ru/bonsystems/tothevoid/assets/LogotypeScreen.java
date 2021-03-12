package ru.bonsystems.tothevoid.assets;

import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

import ru.bonsystems.tothevoid.platform.Config;
import ru.bonsystems.tothevoid.platform.Controller;
import ru.bonsystems.tothevoid.platform.GameScreen;
import ru.bonsystems.tothevoid.platform.extend.transitions.OpacityTransition;

/**
 * Created by Kolomeytsev Anton on 25.02.2016.
 */
public class LogotypeScreen extends GameScreen {

    private float elapsedTime;

    @Override
    public void init() {
        elapsedTime = 0f;
        texture = loadTextureFromAssets("img/bonsystems.png");
        paint = new Paint();
        paint.setColor(Color.BLACK);
        x = (Config.RENDER_WIDTH - texture.getWidth()) / 2f;
        y = (Config.RENDER_HEIGHT - texture.getHeight()) / 2f;
    }

    @Override
    public void update(float delta) {
        elapsedTime += delta;
        if (elapsedTime > 3f) {
            Controller.getInstance().getRoot().changeScreen(new StartScreen(), OpacityTransition.getBasic());
        }
    }

    @Override
    public void render() {
        canvas.drawRect(0f, 0f, Config.RENDER_WIDTH, Config.RENDER_HEIGHT, paint);
        canvas.drawBitmap(texture, x, y, paint);
    }

    @Override
    public void onShow() {

    }

    @Override
    public void onHide() {

    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }
}
