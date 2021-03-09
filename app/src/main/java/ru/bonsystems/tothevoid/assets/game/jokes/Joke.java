package ru.bonsystems.tothevoid.assets.game.jokes;

import ru.bonsystems.tothevoid.platform.GameObject;

/**
 * Created by Kolomeytsev Anton on 26.02.2016.
 */
public abstract class Joke extends GameObject {
    protected boolean alive;

    public boolean isAlive() {
        return alive;
    }
}
