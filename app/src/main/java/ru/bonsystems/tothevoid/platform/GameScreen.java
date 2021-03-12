package ru.bonsystems.tothevoid.platform;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

import ru.bonsystems.tothevoid.platform.extend.UIModel;

/**
 * Абстрактное описание игрового экрана.
 * Описаны базовые поля:
 * buffer - Bitmap, в котором хранится содержимое игрового экрана
 * canvas - При помощи него рисуем на buffer
 * transform - Матрица для трансформации buffer во время отрисовки на экран,
 * в основном служит для масштабирования buffer при выводе на экран
 *
 * @author Kolomeytsev Anton
 * @see ru.bonsystems.tothevoid.platform.GameObject
 */
public abstract class GameScreen extends GameObject implements View.OnTouchListener {
    protected Bitmap buffer;
    protected Canvas canvas;
    protected Matrix transform;
    protected Paint paint;
    protected UIModel uiModel;

    public GameScreen() {
        buffer = Bitmap.createBitmap((int) Config.RENDER_WIDTH, (int) Config.RENDER_HEIGHT, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(buffer);
        paint = new Paint();
        (transform = new Matrix()).setScale(1 / Config.scaleFactor, 1 / Config.scaleFactor);
    }

    public abstract void update(float delta);

    public abstract void render();

    public void renderTo(Canvas canvas) {
        canvas.drawBitmap(buffer, transform, paint);
    }

    /**
     * Переопределенный от GameObject метод, который не должен быть тут(
     *
     * @param canvas - ненужная ссылка на Canvas
     */
    public void render(Canvas canvas) {
        // do nothing
    }

    public abstract void onShow();

    public abstract void onHide();

    public boolean onPop() {
        return true;
    }

    public Bitmap getBuffer() {
        return buffer;
    }

    public Matrix getTransform() {
        return transform;
    }

    public Paint getPaint() {
        return paint;
    }

    @Override
    public abstract boolean onTouch(View view, MotionEvent motionEvent);
}
