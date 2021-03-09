package ru.bonsystems.tothevoid.assets.utils;

/**
 * Created by Kolomeytsev Anton on 23.02.2016.
 */
public class Pointer<T> {
    private T value;

    public Pointer(T v) {
        this.value = v;
    }

    public Pointer() {

    }

    public T get() {
        return value;
    }

    public void set(T value) {
        this.value = value;
    }
}
