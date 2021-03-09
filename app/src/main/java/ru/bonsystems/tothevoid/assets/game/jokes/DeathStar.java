package ru.bonsystems.tothevoid.assets.game.jokes;

import android.graphics.Matrix;
import android.graphics.Paint;

import ru.bonsystems.tothevoid.assets.game.Camera;
import ru.bonsystems.tothevoid.platform.Config;
import ru.bonsystems.tothevoid.platform.extend.Log;

/**
 * Created by Kolomeytsev Anton on 26.02.2016.
 */
public class DeathStar extends Joke {

    @Override
    public void init() {
        alive = true;
        transform = new Matrix();
        texture = loadTextureFromAssets("game/jokes/star.png");
        paint = new Paint();
        x = Config.rndWidth + texture.getWidth() + 10f;
        y = Config.rndHeight * 0.5f;//(new Random()).nextFloat() * (Config.rndHeight - texture.getHeight());
        Log.i(this, "INIT");
    }

    @Override
    public void update(float delta) {
        x -= 50f * delta;
        if (x < -texture.getWidth() - 10f) alive = false;
        float translatedY = y - (Camera.getInstance().getY() / 4f);
        transform.setTranslate(x, translatedY);
    }
}
