package ru.bonsystems.tothevoid.assets;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

import ru.bonsystems.tothevoid.assets.backgrounds.DustBackground;
import ru.bonsystems.tothevoid.assets.game.Asteroid;
import ru.bonsystems.tothevoid.assets.game.BaseGame;
import ru.bonsystems.tothevoid.assets.game.GameState;
import ru.bonsystems.tothevoid.assets.transitions.StartTransition;
import ru.bonsystems.tothevoid.assets.utils.Pallete;
import ru.bonsystems.tothevoid.platform.Config;
import ru.bonsystems.tothevoid.platform.Controller;
import ru.bonsystems.tothevoid.platform.GameObject;
import ru.bonsystems.tothevoid.platform.GameScreen;
import ru.bonsystems.tothevoid.platform.extend.DataStorage;
import ru.bonsystems.tothevoid.platform.extend.Log;
import ru.bonsystems.tothevoid.platform.extend.UIModel;
import ru.bonsystems.tothevoid.platform.extend.uis.Control;

/**
 * Created by Kolomeytsev Anton a long time ago...
 */
public class StartScreen extends GameScreen implements View.OnTouchListener {
    private final GameObject logo;
    private final GameObject touchToStart;
    private final GameObject backBackground;
    private final Control startButton;
    private final SettingsButton settingsButton;
    private final ScoreGUI scoreGui;

    public StartScreen() {
        backBackground = new DustBackground();
        logo = new GameObject() {
            private float scale = 1f;

            @Override
            public void init() {
                super.init();
                texture = loadTextureFromAssets("start_menu/logo.png");
                transform = new Matrix();
                paint = new Paint();
                paint.setAntiAlias(true);
            }

            @Override
            public void update(float delta) {
                {
                    angle = 0f;
                    transform.setRotate(angle, texture.getWidth() / 2, texture.getHeight() / 2);
                }
                {
                    scale = 1f;
                    transform.postScale(scale, scale);
                }
                transform.postTranslate(
                        Config.RENDER_WIDTH / 2 - texture.getWidth() * scale / 2,
                        Config.RENDER_HEIGHT / 2 - texture.getHeight() * scale / 2 - 50f
                );
            }

            @Override
            public void render(Canvas canvas) {
                canvas.drawBitmap(texture, transform, paint);
            }
        };
        touchToStart = new GameObject() {
            private float incrementOpacity = 0f;
            private float opacity = 1f;

            @Override
            public void init() {
                DataStorage.getInstance().reload().set("debug_mode", "true").apply();

                paint = new Paint();
                paint.setAntiAlias(true);
                texture = loadTextureFromAssets("start_menu/touch_to_start.png");
                transform = new Matrix();
                float startButtonY = 720f / 4f * 3f;
                transform.setTranslate(
                        Config.RENDER_WIDTH / 2 - texture.getWidth() / 2,
                        startButtonY + (texture.getHeight() / 2f)
                );
            }

            @Override
            public void update(float delta) {
                incrementOpacity += 5f * delta;
                opacity = (float) Math.abs(Math.sin(incrementOpacity));
                paint.setAlpha((int) (255 * opacity));
            }

            @Override
            public void render(Canvas canvas) {
                canvas.drawBitmap(texture, transform, paint);
            }
        };
        /**
         * Далее создаем кнопки, которые будут
         * запускать игру, открывать настройки и т.д.
         * */
        (startButton = new StartButton())
                .addOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) startGame();
                        return true;
                    }
                });
        (settingsButton = new SettingsButton())
                .addOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                            Controller.getInstance().getPresenter().setOnTouchListener(null);
                            Controller.getInstance().getRoot().pushScreen(new SettingsScreen(), new StartTransition());
                        }
                        return true;
                    }
                });
        scoreGui = new ScoreGUI();

        /**
         * В конце концов, добавляем созданные кнопки
         * в список класс-обработчика касаний
         */
        (uiModel = new UIModel())
                .addControl(startButton)
                .addControl(settingsButton);

        loadHeavyResources();
        GameState.reset();
    }

    /**
     * Это важно!
     * Этот метод вызывается при нажатии на кнопку startButton,
     * созданную в init() этого класса
     */
    private void startGame() {
        Controller.getInstance().getRoot().pushScreen(new BaseGame(), new StartTransition());
    }

    private void loadHeavyResources() {
        Asteroid.textures.put("3x_corners_0", loadTextureFromAssets("game/3x_corners_0.png"));
        Asteroid.textures.put("3x_corners_1", loadTextureFromAssets("game/3x_corners_1.png"));
        Asteroid.textures.put("4x_corners_0", loadTextureFromAssets("game/4x_corners_0.png"));
        Asteroid.textures.put("4x_corners_1", loadTextureFromAssets("game/4x_corners_1.png"));
        Asteroid.textures.put("5x_corners_0", loadTextureFromAssets("game/5x_corners_0.png"));
        Asteroid.textures.put("5x_corners_1", loadTextureFromAssets("game/5x_corners_1.png"));
        Asteroid.textures.put("6x_corners_0", loadTextureFromAssets("game/6x_corners_0.png"));
        Asteroid.textures.put("6x_corners_1", loadTextureFromAssets("game/6x_corners_1.png"));
        Asteroid.textures.put("7x_corners_0", loadTextureFromAssets("game/7x_corners_0.png"));
        Asteroid.textures.put("7x_corners_1", loadTextureFromAssets("game/7x_corners_1.png"));
        Asteroid.textures.put("8x_corners_0", loadTextureFromAssets("game/8x_corners_0.png"));
        Asteroid.textures.put("8x_corners_1", loadTextureFromAssets("game/8x_corners_1.png"));
        Asteroid.textures.put("doge", loadTextureFromAssets("game/jokes/doge.png"));
    }

    @Override
    public void update(float delta) {
        backBackground.update(delta);
        logo.update(delta);
        touchToStart.update(delta);
        startButton.update(delta);
        settingsButton.update(delta);
        scoreGui.update(delta);
    }

    @Override
    public void render() {
        canvas.drawColor(Pallete.SPACE_DARK);
        backBackground.render(canvas);
        logo.render(canvas);
        touchToStart.render(canvas);
        startButton.render(canvas);
        settingsButton.render(canvas);
        scoreGui.render(canvas);
    }

    @Override
    public void onShow() {
        Log.i(this, "onShow");

    }

    @Override
    public void onHide() {
        Log.i(this, "onHide");
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        uiModel.onTouch(motionEvent);
        return true;
    }

    class StartButton extends Control {
        private float renderCenterX;
        private float renderCenterY;
        private float renderRadius;
        private float radiusIncrement;

        @Override
        public void init() {
            renderCenterX = 1280f / 2f;
            renderCenterY = 720f / 4f * 3f;
            renderRadius = 60f;
            radiusIncrement = 0f;

            texture = loadTextureFromAssets("start_menu/icon_play.png");
            texture = Bitmap.createScaledBitmap(texture, (int) (texture.getWidth() * 0.8f), (int) (texture.getHeight() * 0.8f), true);

            (paint = new Paint()).setColor(Pallete.ACCENT_LIGHT);
            paint.setAntiAlias(true);
            setArea(new Area(
                    renderCenterX - renderRadius,
                    renderCenterY - renderRadius,
                    renderRadius * 2f,
                    renderRadius * 2f
            ));
        }

        @Override
        public void update(float delta) {
            radiusIncrement += delta * 4f;
            if (radiusIncrement > Math.PI) radiusIncrement -= radiusIncrement;
        }

        @Override
        public void render(Canvas canvas) {
            canvas.drawCircle(
                    renderCenterX,
                    renderCenterY,
                    (float) (renderRadius + Math.sin(radiusIncrement) * 5f),
                    paint
            );
            canvas.drawBitmap(
                    texture,
                    renderCenterX - texture.getWidth() / 2f,
                    renderCenterY - texture.getHeight() / 2f,
                    null
            );
            super.render(canvas);
        }
    }

    class SettingsButton extends Control {
        private float renderCenterX;
        private float renderCenterY;
        private float renderRadius;
        private float radiusIncrement;

        @Override
        public void init() {
            renderRadius = 40f;
            renderCenterX = Config.RENDER_WIDTH - renderRadius - 30f;
            renderCenterY = Config.RENDER_HEIGHT - renderRadius - 20f;
            radiusIncrement = 0f;

            texture = loadTextureFromAssets("start_menu/icon_settings.png");
            texture = Bitmap.createScaledBitmap(texture, (int) (texture.getWidth() * 0.8f), (int) (texture.getHeight() * 0.8f), true);

            (paint = new Paint()).setColor(Pallete.ACCENT_LIGHT);
            paint.setAntiAlias(true);
            setArea(new Area(
                    renderCenterX - renderRadius,
                    renderCenterY - renderRadius,
                    renderRadius * 2f,
                    renderRadius * 2f
            ));
        }

        @Override
        public void update(float delta) {
            radiusIncrement += delta * 4f;
            if (radiusIncrement > Math.PI) radiusIncrement -= radiusIncrement;
        }

        @Override
        public void render(Canvas canvas) {
            canvas.drawCircle(
                    renderCenterX,
                    renderCenterY,
                    (float) (renderRadius + Math.sin(radiusIncrement) * 5f),
                    paint
            );
            canvas.drawBitmap(
                    texture,
                    renderCenterX - texture.getWidth() / 2f,
                    renderCenterY - texture.getHeight() / 2f,
                    null
            );
            super.render(canvas);
        }
    }

    class ScoreGUI extends GameObject {
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
            score = "Name unknown";
            highscore = String.valueOf((int) GameState.highscore);
        }

        @Override
        public void render(Canvas canvas) {
            canvas.drawText(score, 40f, 80f, scorePaint);
            canvas.drawText(highscore, 90f, 140f, highScorePaint);
            canvas.drawBitmap(hsIcon, 45f, 110f, null);
        }
    }
}
