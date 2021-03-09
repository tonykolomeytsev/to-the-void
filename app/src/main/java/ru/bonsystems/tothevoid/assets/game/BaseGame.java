package ru.bonsystems.tothevoid.assets.game;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Random;

import ru.bonsystems.tothevoid.assets.backgrounds.SpaceBackground;
import ru.bonsystems.tothevoid.assets.game.gameplay.Enemy;
import ru.bonsystems.tothevoid.assets.game.jokes.DeathStar;
import ru.bonsystems.tothevoid.assets.game.jokes.Joke;
import ru.bonsystems.tothevoid.assets.game.particles.ExplosionEffect;
import ru.bonsystems.tothevoid.assets.game.particles.ParticleStorm;
import ru.bonsystems.tothevoid.assets.game.screens.GameOverHighscoreScreen;
import ru.bonsystems.tothevoid.assets.game.screens.GameOverScreen;
import ru.bonsystems.tothevoid.assets.game.screens.PauseScreen;
import ru.bonsystems.tothevoid.assets.utils.Pallete;
import ru.bonsystems.tothevoid.assets.utils.Pointer;
import ru.bonsystems.tothevoid.platform.Config;
import ru.bonsystems.tothevoid.platform.Controller;
import ru.bonsystems.tothevoid.platform.GameObject;
import ru.bonsystems.tothevoid.platform.GameScreen;
import ru.bonsystems.tothevoid.platform.extend.DataStorage;
import ru.bonsystems.tothevoid.platform.extend.UIModel;
import ru.bonsystems.tothevoid.platform.extend.transitions.OpacityTransition;
import ru.bonsystems.tothevoid.platform.extend.uis.Control;

/**
 * Created by Kolomeytsev Anton on 07.01.2016.
 * Класс, в котором содержится весь основной контент игры, её логика, данные.
 */
public class BaseGame extends GameScreen implements View.OnTouchListener {

    private GameObject skyBack;
    private Player player;
    private Enemy enemy;
    private GameObject particleStorm;
    private Collider collider;

    private Control playerControlArea;
    private PlayerControl playerControlProcessor;
    private GameObject scoreGUI;
    private Control debugger;
    private Asteroid[] asteroids;
    private boolean delay = false;
    private boolean gameOver = false;
    private GameObject explosion;
    private Control pauseButton;
    private Joke joke;


    @Override
    public void init() {
        initSystems();
        initGameObjects();
    }

    private void initGameObjects() {
        player = new Player();
        Camera.getInstance().setDirection(player);


        skyBack = new SpaceBackground(2f, 350);
        particleStorm = new ParticleStorm();


        playerControlArea = new Control() {@Override public void update(float delta) {}};
        playerControlArea.setArea(new Control.Area(Config.rndWidth / 2f, 100f, Config.rndWidth / 2f, Config.rndHeight - 100f));
        playerControlArea.addOnTouchListener(playerControlProcessor = new PlayerControl());
        uiModel.addControl(playerControlArea);

        asteroids = new Asteroid[7];
        for (int i = 0; i < asteroids.length; i++) {
            asteroids[i] = new Asteroid();
            asteroids[i].setX(Config.rndWidth * 2f);
        }
        collider = new Collider(new Runnable() {
            @Override
            public void run() {
                DataStorage.getInstance()
                        .set("highscore", String.valueOf(GameState.highscore))
                        .apply();
                makeBoom();
            }
        });
        scoreGUI = new ScoreGUI();

        if (Config.debugMode) {
            debugger = new Control() {
                @Override
                public void update(float delta) {

                }
            };
            debugger.addOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        delay = !delay;
                    }
                    return true;
                }
            }).setArea(new Control.Area(100, 100, 100, 100));
            uiModel.addControl(debugger);
        }

        joke = null;
        final Pointer<Integer> integerPointer = new Pointer<>(0);
        GameState.setScoreHalfThousandListener(new GameState.ScoreHalfThousandListener() {
            @Override
            public void OnScoreEqualsHalfThousand() {
                switch (integerPointer.get()) {
                    case 0:
                        GameState.jokeDoge = true;
                        break;

                    case 1:
                        GameState.jokeDoge = false;
                        launchEnemy();
                        break;

                    case 2:
                        async(new Runnable() { // в асинктаске, потому что картинка может быть тяжелая
                            @Override
                            public void run() {
                                joke = (new DeathStar());
                            }
                        });
                        break;

                    case 5:
                        launchEnemy();
                        break;
                }
                integerPointer.set(integerPointer.get() + 1);
            }
        });
    }

    private void initSystems() {
        GameState.reset();
        Camera.getInstance().setArea(1300, 1000);

        uiModel = new UIModel();
        transform = new Matrix();
        (paint = new Paint()).setAntiAlias(true);

        (pauseButton = new PauseButton()).addOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    Controller.getInstance().getRoot().pushScreen(new PauseScreen());
                }
                return true;
            }
        });
        uiModel.addControl(pauseButton);
    }

    @Override
    public void update(float delta) {
        if (delay) return; // debugMode
        skyBack.update(delta);
        particleStorm.update(delta);
        playerControlProcessor.update(delta);
        player.update(delta);
        scoreGUI.update(delta);

        Camera.getInstance().update(delta);
        updateAsteroids(delta);
        updateEnemy(delta);
        updateExplosion(delta);

        if (joke != null) if (joke.isAlive()) joke.update(delta); else joke = null;

        GameState.gameSpeed += delta / 50f;
        GameState.incrementScore(50f * delta);
    }

    private void updateEnemy(float delta) {
        if (enemy != null) {
            enemy.update(delta);
            ArrayList<Enemy.Bullet> bullets = enemy.getBullets();
            for (Enemy.Bullet bullet : bullets)
                if (bullet.isAlive())
                    collider.collideWithBullet(player, bullet);

            if (enemy.endScenario()) enemy = null;
        }
    }

    private void launchEnemy() {
        enemy = new Enemy(player);
        enemy.update(0.03f);
    }

    private void updateExplosion(float delta) {
        if (gameOver && explosion != null) {
            float x = player.getX();
            player.setPosition(x - (290f + GameState.gameSpeed * 100f + (new Random()).nextFloat() * 100f), player.getY());
            explosion.update(delta);
        }
    }

    private void updateAsteroids(float delta) {
        for (int i = 0; i < asteroids.length; i++) {
            if (!asteroids[i].isAlive()) {
                if (enemy == null) asteroids[i] = new Asteroid();
            }
            asteroids[i].update(delta);
            if (collider != null) {
                if (collider.collideObjects(asteroids[i], player)) {
                    collider = null;
                }
            }
        }
    }

    @Override
    public void render() {
        canvas.drawColor(Pallete.SPACE_DARK);
        if (joke != null) joke.render(canvas);
        skyBack.render(canvas);
        player.render(canvas);
        if (enemy != null) enemy.render(canvas);

        playerControlArea.render(canvas);
        if (Config.debugMode) {
            debugger.render(canvas);
        }

        for (int i = 0; i < asteroids.length; i++) {
            asteroids[i].render(canvas);
        }

        if (gameOver && explosion != null) {
            explosion.render(canvas);
        }

        scoreGUI.render(canvas);
        pauseButton.render(canvas);
        particleStorm.render(canvas);
    }

    private void makeBoom() {
        if (gameOver) return;
        explosion = new ExplosionEffect(player, new ExplosionEffect.CompleteListener() {
            private int i = 0;
            @Override
            public void OnExplosionComplete() {
                i++;
                if (i == 1) {
                    System.out.println(Controller.getInstance().getRoot().getScreenStack());
                    GameScreen gameScreen = GameState.isHighscoreNow() ? new GameOverHighscoreScreen() : new GameOverScreen() ;
                    Controller.getInstance().getRoot().changeScreen(gameScreen, OpacityTransition.getBasic());
                }
            }
        });
        gameOver = true;
    }

    @Override
    public void onShow() {
        playerControlProcessor.reset();
    }

    @Override
    public void onHide() {
        playerControlProcessor.reset();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        uiModel.onTouch(motionEvent);
        return true;
    }

    class PlayerControl implements View.OnTouchListener {
        private float dy, ly, cy, ay;

        public PlayerControl() {
            ay = 0f; dy = 0f;

        }

        public void update(float delta) {
            player.setAcceleration(-ay / 50f);

            float newPosX = Config.rndWidth / 4f;
            float newPosY = player.getY() - ay * delta;
            if (newPosY < 0) newPosX = 0;
            if (newPosY > Camera.getInstance().getAreaHeight()) newPosY = Camera.getInstance().getAreaHeight();
            ay *= 0.8;
            if (dy > -0.5 && dy < 0.5) dy = 0;

            player.setPosition(newPosX, newPosY);
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    ly = motionEvent.getY();
                    break;

                case MotionEvent.ACTION_MOVE:
                    cy = motionEvent.getY();
                    dy = (ly - cy) * 12 * Config.scaleFactor;
                    ay += dy;
                    ly = cy;
                    break;

            }
            return true;
        }

        public void reset() {
            ay = 0f; dy = 0f; ly = 0f; cy = 0f;
        }
    }

    class PauseButton extends Control {


        @Override
        public void init() {
            texture = loadTextureFromAssets("pause_menu/icon_pause.png");
            x = Config.rndWidth - texture.getWidth() - 20f;
            y = 40f;
            area = new Area(x, y, texture.getWidth(), texture.getHeight());
        }

        @Override
        public void render(Canvas canvas) {
            canvas.drawBitmap(texture, x, y, null);
            super.render(canvas);
        }

        @Override
        public void update(float delta) {
        }
    }
}
