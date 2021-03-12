package ru.bonsystems.tothevoid.assets.game.screens;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

import ru.bonsystems.tothevoid.R;
import ru.bonsystems.tothevoid.assets.game.BaseGame;
import ru.bonsystems.tothevoid.assets.utils.Pallete;
import ru.bonsystems.tothevoid.platform.Config;
import ru.bonsystems.tothevoid.platform.Controller;
import ru.bonsystems.tothevoid.platform.GameScreen;
import ru.bonsystems.tothevoid.platform.extend.UIModel;
import ru.bonsystems.tothevoid.platform.extend.uis.Control;

/**
 * Created by Kolomeytsev Anton on 25.02.2016.
 */
public class PauseScreen extends GameScreen implements View.OnTouchListener {

    private final Label pauseHeader;
    private final Label buttonMenu;
    private final Label buttonResume;
    private final Label buttonRestart;
    private float renderRadius;
    private float radiusIncrement;
    private Paint darkPaint;
    private Paint transparentPaint;

    public PauseScreen() {
        x = Config.RENDER_WIDTH / 2f;
        y = Config.RENDER_HEIGHT / 2f;
        renderRadius = Config.RENDER_HEIGHT * 0.44f;

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Pallete.ACCENT_LIGHT);

        transparentPaint = new Paint(paint);
        transparentPaint.setAlpha(100);

        darkPaint = new Paint();
        paint.setAntiAlias(true);
        darkPaint.setColor(Pallete.SPACE_DARK);

        (pauseHeader = new Label(Controller.getInstance().getString(R.string.pause_header), 70f, x, y - 140f)).setTestAlign(Paint.Align.CENTER);
        (buttonMenu = new Label(Controller.getInstance().getString(R.string.pause_menu), 45f, x, y - 40f)).setTestAlign(Paint.Align.CENTER).addOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    Controller.getInstance().getPresenter().setOnTouchListener(null);
                    Controller.getInstance().getRoot().popScreen(); // выход из паузы на экран игры
                    Controller.getInstance().getRoot().popScreen(); // выход из игры на экран меню
                }
                return true;
            }
        });
        (buttonResume = new Label(Controller.getInstance().getString(R.string.pause_continue), 45f, x, y + 40f)).setTestAlign(Paint.Align.CENTER).addOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    Controller.getInstance().getPresenter().setOnTouchListener(null);
                    Controller.getInstance().getRoot().popScreen(); // выход из паузы на экран игры
                }
                return true;
            }
        });
        (buttonRestart = new Label(Controller.getInstance().getString(R.string.pause_restart), 45f, x, y + 120f)).setTestAlign(Paint.Align.CENTER).addOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    Controller.getInstance().getPresenter().setOnTouchListener(null);
                    Controller.getInstance().getRoot().popScreen(); // выход из паузы на экран игры
                    Controller.getInstance().getRoot().popScreen(); // выход из игры на экран меню
                    Controller.getInstance().getRoot().pushScreen(new BaseGame());
                }
                return true;
            }
        });

        (uiModel = new UIModel())
                .addControl(buttonMenu)
                .addControl(buttonResume)
                .addControl(buttonRestart);
    }

    @Override
    public void update(float delta) {
        radiusIncrement += delta * 4f;
        if (radiusIncrement > Math.PI) radiusIncrement -= radiusIncrement;
    }

    @Override
    public void render() {
        canvas.drawRect(0f, 0f, Config.RENDER_WIDTH, Config.RENDER_HEIGHT, darkPaint);
        canvas.drawCircle(x, y, (float) (renderRadius + Math.sin(radiusIncrement) * 15f), transparentPaint);
        canvas.drawCircle(x, y, renderRadius, paint);

        pauseHeader.render(canvas);
        buttonMenu.render(canvas);
        buttonResume.render(canvas);
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
            (paint = new Paint()).setColor(Pallete.SPACE_DARK);
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
