package ru.bonsystems.tothevoid.platform;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Kolomeytsev Anton
 * Абстрактное описание игрового объекта.
 * Имеются поля:
 * texture - Как ни странно, текстура объекта (может быть и неиспользованной)
 * transform - Графическая трансформация по матрице
 * paint - Параметры отрисовки объекта на экран
 * x, y, angle - зарезервированные параметры объекта
 */
public abstract class GameObject {
    protected Bitmap texture;
    protected Matrix transform;
    protected Paint paint;
    protected float x = 0f, y = 0f, angle = 0f;

    public GameObject() {
        init();
    }

    public void init() {
        /* no-op */
    }

    public abstract void update(float delta);

    public void render(Canvas canvas) {
        canvas.drawBitmap(texture, transform, paint);
    }

    public static Bitmap loadTextureFromAssets(String fileName) {
        Bitmap temporary = null;
        AssetManager assetManager = Controller.getInstance().getResources().getAssets();
        try {
            InputStream inputStream = assetManager.open(fileName);
            temporary = BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return temporary;
    }

    public static Bitmap loadTextureFromAssets(String fileName, Bitmap.Config config) {
        Bitmap temporary = null;
        AssetManager assetManager = Controller.getInstance().getResources().getAssets();
        try {
            InputStream inputStream = assetManager.open(fileName);
            temporary = BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap response = Bitmap.createBitmap(temporary.getWidth(), temporary.getHeight(), config);
        Canvas canvas = new Canvas(response);
        canvas.drawBitmap(temporary, 0f, 0f, null);
        temporary.recycle();
        temporary = null;
        canvas = null;
        return response;
    }

    public static Typeface loadTypefaceFromAssets(String filename) {
        AssetManager assetManager = Controller.getInstance().getResources().getAssets();
        return Typeface.createFromAsset(assetManager, filename);
    }

    protected void async(final Runnable loading) {
        new Thread(loading).start();
    }

    protected void waitIfFaster(final Runnable r, long milliseconds) {
        long elapsedTime, startTime = System.currentTimeMillis();
        r.run();
        elapsedTime = System.currentTimeMillis() - startTime;
        if (elapsedTime < milliseconds) {
            try {
                Thread.sleep(milliseconds - elapsedTime + 1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
