package ru.bonsystems.tothevoid.platform;

import android.graphics.Canvas;
import android.view.View;

import java.util.Stack;

import ru.bonsystems.tothevoid.assets.LoadingScreen;
import ru.bonsystems.tothevoid.assets.LogotypeScreen;
import ru.bonsystems.tothevoid.platform.extend.Transition;
import ru.bonsystems.tothevoid.platform.extend.transitions.OpacityTransition;

/**
 * Created by Kolomeytsev Anton
 * Модель игрового приложения.
 * Здесь происходит делегация вызовов игровых тактов и обработка переходов между игровыми экранами
 */
public class Root {
    private final Stack<GameScreen> gameScreens = new Stack<>();
    private boolean transitionNow = false;
    private Transition transition;
    private Runnable afterTransition;

    public Root() {
        pushScreen(new LogotypeScreen(), OpacityTransition.getBasic());
    }

    public void run(Canvas canvas, float delta) {
        if (transitionNow) {
            setTouchListener(null);
            transition.update(delta);
            transition.render();
            transition.renderTo(canvas);
        } else {
            GameScreen gameScreen = gameScreens.peek();
            setTouchListener(gameScreen);
            gameScreen.update(delta);
            gameScreen.render();
            gameScreen.renderTo(canvas);
        }
    }

    public Stack<GameScreen> getScreenStack() {
        return gameScreens;
    }

    public void pushScreen(GameScreen screen) {
        screen.onShow();
        if (gameScreens.size() > 1) gameScreens.peek().onHide();
        gameScreens.push(screen);

    }

    public void pushScreen(final GameScreen screen, Transition transition) {
        //if (transitionNow) return;
        {
            this.transition = transition;
            transitionNow = true;
        }
        screen.onShow();
        if (gameScreens.size() > 0) {
            transition.setScreens(gameScreens.peek(), screen);
        } else {
            transition.setScreens(null, screen);
        }
        afterTransition = new Runnable() {
            @Override
            public void run() {
                transitionNow = false;
                if (gameScreens.size() > 0) {
                    gameScreens.peek().onHide();
                    GameScreen prevScreen = gameScreens.peek();
                    if (prevScreen instanceof LoadingScreen) {
                        gameScreens.remove(prevScreen);
                    }
                }
                gameScreens.push(screen);
            }
        };
        transition.after(afterTransition);
    }

    public GameScreen popScreen() {
        if (!gameScreens.peek().onPop()) return null; // спрашиваем разрешения на закрытие экрана
        GameScreen screen = gameScreens.pop();
        screen.onHide();
        if (gameScreens.size() > 0) gameScreens.peek().onShow();
        return screen;
    }

    public GameScreen popScreen(Transition transition) {
        if (!gameScreens.peek().onPop()) return null; // спрашиваем разрешения на закрытие экрана
        //if (transitionNow) return null;
        {
            this.transition = transition;
            transitionNow = true;
        }
        final GameScreen screen = gameScreens.pop();
        if (gameScreens.size() > 0) {
            gameScreens.peek().onShow();
            transition.setScreens(screen, gameScreens.peek());
        } else {
            transition.setScreens(screen, null);
        }
        afterTransition = () -> {
            transitionNow = false;
            screen.onHide();
        };
        transition.after(afterTransition);
        return screen;
    }

    public void changeScreen(GameScreen screen) {
        popScreen();
        pushScreen(screen);
    }

    public void changeScreen(GameScreen screen, Transition transition) {
        transitionNow = true;
        pushScreen(screen, transition);
        popScreen();
    }

    View.OnTouchListener temp = null;
    boolean waiting = false;

    private void setTouchListener(final View.OnTouchListener obj) {
        if (obj == temp) return;
        else {
            if (!waiting) {
                waiting = true;
                async(() -> {
                    try {
                        Thread.sleep(111);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    temp = obj;
                    Controller.getInstance().getPresenter().setOnTouchListener(obj);
                    waiting = false;
                });
            }
        }

    }

    private void setTouchListener(final View.OnTouchListener obj, final long millis) {
        async(() -> {
            try {
                Thread.sleep(millis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Controller.getInstance().getPresenter().setOnTouchListener(obj);
        });
    }

    protected void async(final Runnable loading) {
        new Thread(loading).start();
    }
}
