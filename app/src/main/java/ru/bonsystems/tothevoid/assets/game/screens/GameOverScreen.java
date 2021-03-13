package ru.bonsystems.tothevoid.assets.game.screens;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.ColorInt;

import ru.bonsystems.tothevoid.R;
import ru.bonsystems.tothevoid.assets.StartScreen;
import ru.bonsystems.tothevoid.assets.backgrounds.DustBackground;
import ru.bonsystems.tothevoid.assets.game.BaseGame;
import ru.bonsystems.tothevoid.assets.game.GameState;
import ru.bonsystems.tothevoid.assets.utils.Pallete;
import ru.bonsystems.tothevoid.platform.Config;
import ru.bonsystems.tothevoid.platform.Controller;
import ru.bonsystems.tothevoid.platform.GameObject;
import ru.bonsystems.tothevoid.platform.GameScreen;
import ru.bonsystems.tothevoid.platform.extend.Log;
import ru.bonsystems.tothevoid.platform.extend.UIModel;
import ru.bonsystems.tothevoid.platform.extend.uis.Control;

/**
 * Created by Kolomeytsev Anton on 25.02.2016.
 */
public class GameOverScreen extends GameScreen implements View.OnTouchListener {

    private final Label scoreHeader;
    private final Label newRecordHeader;
    private final Label buttonMenu;
    private final Label buttonRestart;
    private final GameObject dustBackground;
    private final boolean isHighScore;

    @SuppressLint("ClickableViewAccessibility")
    public GameOverScreen(boolean isHighScore) {
        this.isHighScore = isHighScore;
        dustBackground = new DustBackground();
        x = Config.RENDER_WIDTH / 2f;
        y = Config.RENDER_HEIGHT / 2f;
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Pallete.ACCENT_LIGHT);

        @ColorInt
        final int textColor = (isHighScore) ? 0xfff1c101 : Pallete.ACCENT_LIGHT;
        final String newRecordCongrats = Controller.getInstance().getString(R.string.new_record_congratulations);
        (newRecordHeader = new Label(newRecordCongrats, 45f, x, y - 164f))
                .setTextAlign(Paint.Align.CENTER)
                .setTextColor(textColor);
        (scoreHeader = new Label((int) GameState.score + "M", 180f, x, y))
                .setTextAlign(Paint.Align.CENTER)
                .setTextColor(textColor);
        (buttonMenu = new Label(Controller.getInstance().getString(R.string.pause_menu), 45f, x, y + 180f))
                .setTextAlign(Paint.Align.CENTER)
                .addOnTouchListener((view, motionEvent) -> {
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        Controller.getInstance().getPresenter().setOnTouchListener(null);
                        while (!(Controller.getInstance().getRoot().getScreenStack().peek() instanceof StartScreen)) {
                            Controller.getInstance().getRoot().popScreen();
                        }
                    }
                    return true;
                });
        (buttonRestart = new Label(Controller.getInstance().getString(R.string.pause_restart), 45f, x, y + 100f))
                .setTextAlign(Paint.Align.CENTER)
                .addOnTouchListener((view, motionEvent) -> {
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        Controller.getInstance().getPresenter().setOnTouchListener(null);
                        Controller.getInstance().getRoot().changeScreen(new BaseGame());
                    }
                    return true;
                });
        (uiModel = new UIModel())
                .addControl(buttonMenu)
                .addControl(buttonRestart);
    }

    @Override
    public void update(float delta) {
        dustBackground.update(delta);
    }

    @Override
    public void render() {
        canvas.drawColor(Pallete.SPACE_DARK);
        dustBackground.render(canvas);

        if (isHighScore) newRecordHeader.render(canvas);
        scoreHeader.render(canvas);
        buttonMenu.render(canvas);
        buttonRestart.render(canvas);
    }

    @Override
    public void onShow() { /* no-op */ }

    @Override
    public void onHide() { /* no-op */ }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        uiModel.onTouch(motionEvent);
        return true;
    }

    private static class Label extends Control {
        final String label;

        public Label(String label, float size) {
            this.label = label;
            (paint = new Paint()).setColor(Pallete.ACCENT_LIGHT);
            paint.setAntiAlias(true);
            paint.setTypeface(loadTypefaceFromAssets("fonts/caviar.ttf"));
            paint.setTextSize(size);
        }

        public Label(String label, float size, float x, float y) {
            this(label, size);
            setPosition(x, y);
        }

        public void setPosition(float x, float y) {
            this.x = x;
            this.y = y;
            setArea(new Area(
                    x - 150f, y - 30f, 300f, 60f
            ));
        }

        public Label setTextAlign(Paint.Align a) {
            paint.setTextAlign(a);
            return this;
        }

        public Label setTextColor(int color) {
            paint.setColor(color);
            return this;
        }

        @Override
        public void update(float delta) { /* no-op */ }

        @Override
        public void render(Canvas canvas) {
            canvas.drawText(label, x, y, paint);
        }
    }
}
