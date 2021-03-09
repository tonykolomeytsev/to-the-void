package ru.bonsystems.tothevoid.platform.extend;

import android.graphics.Canvas;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

import ru.bonsystems.tothevoid.platform.GameScreen;

/**
 * Created by Kolomeytsev Anton
 */
public abstract class Transition {
    protected TransitionType type;
    protected static BlackScreen blackScreen = new BlackScreen();
    protected GameScreen oldScreen, newScreen;
    protected ArrayList<Runnable> afterAnimation = new ArrayList<>();
    private boolean transitionStarted = false;

    protected enum TransitionType { opacity }

    public void setScreens(GameScreen oldScreen, GameScreen newScreen){
        if (oldScreen != null) this.oldScreen = oldScreen;
        else this.oldScreen = blackScreen;
        if (newScreen != null) this.newScreen = newScreen;
        else this.newScreen = blackScreen;
    }

    public void firstUpdate(float delta){

    }
    public void update(float delta) {
        if (!transitionStarted) {
            transitionStarted = true;
            firstUpdate(delta);
        }
    }
    public abstract void render();
    public abstract void renderTo(Canvas canvas);

    private static class BlackScreen extends GameScreen{

        public BlackScreen(){
            super();
            canvas.drawColor(Color.BLACK);
        }

        @Override
        public void update(float delta) {

        }

        @Override
        public void render() {

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

    public GameScreen getNewScreen() {
        return newScreen;
    }

    public GameScreen getOldScreen() {
        return oldScreen;
    }

    public void after(Runnable r){
        afterAnimation.add(r);
    }

    protected void finish(){
        for (Runnable runnable : afterAnimation) {
            runnable.run();
        }
    }

    protected int elapsed = 0;
    protected void delay(float delta, int milliseconds, Runnable r) {
        elapsed += 1000 * delta;
        if (elapsed >= milliseconds) {
            elapsed -= milliseconds;
            r.run();
        }
    }
    // TODO: 07.01.2016 разрешить множественное использование delay в классе Transition
}
