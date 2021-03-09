package ru.bonsystems.tothevoid.assets.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import ru.bonsystems.tothevoid.assets.utils.Pallete;
import ru.bonsystems.tothevoid.platform.GameObject;

/**
 * Created by Kolomeytsev Anton on 24.02.2016.
 */
public class ScoreGUI extends GameObject{
    private Bitmap hsIcon;
    private String score, highscore;
    Paint scorePaint, highScorePaint;

    @Override
    public void init() {
        scorePaint = new Paint();
        scorePaint.setAntiAlias(true);
        scorePaint.setColor(Pallete.ACCENT_LIGHT);
        scorePaint.setTypeface(loadTypefaceFromAssets("fonts/caviar.ttf"));
        scorePaint.setTextSize(60);

        highScorePaint = new Paint(scorePaint);
        highScorePaint.setTextSize(40);

        hsIcon = Bitmap.createScaledBitmap(loadTextureFromAssets("game/highscore.png"), 30, 30, true);
    }

    @Override
    public void update(float delta) {
        score = String.valueOf((int) GameState.score);
        highscore = String.valueOf((int) GameState.highscore);
    }

    @Override
    public void render(Canvas canvas) {
        canvas.drawText(score, 40f, 80f, scorePaint);
        canvas.drawText(highscore, 90f, 140f, highScorePaint);
        canvas.drawBitmap(hsIcon, 45f, 110f, null);
    }
}
