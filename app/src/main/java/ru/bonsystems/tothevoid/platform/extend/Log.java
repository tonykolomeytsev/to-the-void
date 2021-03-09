package ru.bonsystems.tothevoid.platform.extend;

/**
 * Created by Kolomeytsev Anton on 23.02.2016.
 */
public class Log {
    public static void i(Object source, Object output) {
        android.util.Log.i((source == null)? "Unknown source": source.getClass().getSimpleName(), (output == null)? null: output.toString());
    }
}
