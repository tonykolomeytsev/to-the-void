package ru.bonsystems.tothevoid.assets.game.screens;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

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
    private final Label buttonMenu;
    private final Label buttonRestart;
    private Paint darkPaint;
    private GameObject dustBackground;

    public GameOverScreen() {
        dustBackground = new DustBackground();
        x = Config.rndWidth / 2f;
        y = Config.rndHeight / 2f;
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Pallete.ACCENT_LIGHT);

        darkPaint = new Paint();
        paint.setAntiAlias(true);
        darkPaint.setColor(Pallete.SPACE_DARK);

        (scoreHeader = new Label((int) GameState.score + "M", 180f, x, y)).setTestAlign(Paint.Align.CENTER);
        (buttonMenu = new Label(Controller.getInstance().getString(R.string.pause_menu), 45f, x, y + 180f)).setTestAlign(Paint.Align.CENTER).addOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    Log.i(this, "Menu");
                    Controller.getInstance().getPresenter().setOnTouchListener(null);
                    while (!(Controller.getInstance().getRoot().getScreenStack().peek() instanceof StartScreen)) {
                        Controller.getInstance().getRoot().popScreen();
                    }
                }
                return true;
            }
        });
        (buttonRestart = new Label(Controller.getInstance().getString(R.string.pause_restart), 45f, x, y + 100f)).setTestAlign(Paint.Align.CENTER).addOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    Log.i(this, "Restart");
                    Controller.getInstance().getPresenter().setOnTouchListener(null);
                    Controller.getInstance().getRoot().changeScreen(new BaseGame());
                }
                return true;
            }
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
        canvas.drawRect(0f, 0f, Config.rndWidth, Config.rndHeight, darkPaint);
        dustBackground.render(canvas);
        //canvas.drawCircle(x, y, Config.rndHeight * 0.3333f, paint);

        scoreHeader.render(canvas);
        buttonMenu.render(canvas);
        buttonRestart.render(canvas);
    }

    @Override
    public void onShow() {
    }

    @Override
    public void onHide() {

    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        uiModel.onTouch(motionEvent);
        return true;
    }

    class Label extends Control {
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

        public Label setPosition(float x, float y) {
            this.x = x;
            this.y = y;


            setArea(new Area(
                    x - 150f, y - 30f, 300f, 60f
            ));

            return this;
        }

        public Label setTestAlign(Paint.Align a) {
            paint.setTextAlign(a);
            return this;
        }

        @Deprecated
        @Override
        public void update(float delta) {
        }

        @Override
        public void render(Canvas canvas) {
            canvas.drawText(label, x, y, paint);
            /*Config.debugMode = true;
            super.render(canvas);
            Config.debugMode = false;*/
        }
    }
}
