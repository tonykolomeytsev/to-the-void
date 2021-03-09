package ru.bonsystems.tothevoid.platform;

import android.app.Activity;
import android.graphics.Point;

/**
 * Created by Kolomeytsev Anton
 * Куча нужных всем статических значений, определяемых еще при загрузке приложения
 */
public class Config {
    public static float scrWidth;
    public static float scrHeight;
    public final static float RENDER_WIDTH = 720f;
    public static float RENDER_HEIGHT;
    public static float scaleFactor;


    public static void setUpByActivity(Activity activity){
        Point point = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(point);
        scrWidth = point.x;
        scrHeight = point.y;
        scaleFactor = (RENDER_WIDTH / scrWidth);
        RENDER_HEIGHT = scaleFactor * scrHeight;
    }
}
