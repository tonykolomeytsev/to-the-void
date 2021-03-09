package ru.bonsystems.tothevoid.assets.utils;

/**
 * Created by Kolomeytsev Anton on 23.02.2016.
 */
public class Vector3f {
    private float x;
    private float y;
    private float z;

    public Vector3f() {
    }

    public Vector3f(float x, float y, float z) {
        this.y = y;
        this.x = x;
        this.z = z;
    }

    public Vector3f setX(float x) {
        this.x = x;
        return this;
    }

    public Vector3f setY(float y) {
        this.y = y;
        return this;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public Vector3f setZ(float z) {
        this.z = z;
        return this;
    }

    public void set(float x, float y, float z) {
        this.y = y;
        this.x = x;
        this.z = z;
    }
}
