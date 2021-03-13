package ru.bonsystems.tothevoid.assets.game;

import org.jetbrains.annotations.NotNull;

import ru.bonsystems.tothevoid.assets.game.gameplay.Enemy;
import ru.bonsystems.tothevoid.assets.utils.Vector3f;
import ru.bonsystems.tothevoid.platform.Config;

/**
 * Created by Kolomeytsev Anton on 24.02.2016.
 */
public class Collisions {
    private final float buffer_player_full_height;
    private final float quarterScreenWidth;
    private final Runnable clashListener;

    public Collisions(@NotNull Runnable clashListener) {
        quarterScreenWidth = (Config.RENDER_WIDTH / 4f) + 100f;
        buffer_player_full_height = 32f;
        this.clashListener = clashListener;
    }

    public void collideWithBullet(@NotNull Player player, @NotNull Enemy.Bullet bullet) {
        Vector3f[] points = player.getCollisionPoints();
        if (points[0].getX() < bullet.getX())
            if (points[0].getY() < bullet.getY())
                if (points[2].getX() > bullet.getX())
                    if (points[1].getY() > bullet.getY()) // карьерная лестница
                        clashListener.run();
    }

    public boolean collideObjects(@NotNull Asteroid asteroid, @NotNull Player player) {
        if (asteroid.getX() > quarterScreenWidth) return false;
        if (asteroid.getY() > player.getY() + buffer_player_full_height) return false;
        if (asteroid.getY() + (asteroid.getMaxCornerRadius() * 2) < player.getY()) return false;

        Vector3f asteroidCenter = asteroid.getCenter();
        Vector3f[] asteroidPoints = asteroid.getPoints();

        Vector3f[] playerPoints = player.getCollisionPoints();
        for (Vector3f playerPoint : playerPoints) {
            playerPoint.setX(playerPoint.getX() - asteroid.getX());
            playerPoint.setY(playerPoint.getY() - asteroid.getY());
        }

        for (int i = 0; i < asteroidPoints.length; i++) {
            int index2 = i + 1;
            if ((index2) == asteroidPoints.length) index2 = 0;
            for (Vector3f playerPoint : playerPoints) {
                if (foo(asteroidCenter, asteroidPoints[i], asteroidPoints[index2], playerPoint)) {
                    clashListener.run();
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Важно! Функция рассчета должна возвращать целое значение, иначе алгоритм начинает сбоить!
     * В функцию передаются отдельные точки ракеты и астероида, с которым можно столкнуться.
     *
     * @param a координаты центра астероида
     * @param b координаты N-го угла астероида
     * @param c координаты (N + 1)-го угла астароида
     * @param d координата одной из угловых точек ракеты
     * @return столкнулись или не стулкнулись
     */
    private boolean foo(Vector3f a, Vector3f b, Vector3f c, Vector3f d) {
        int s = area(a, b, c);
        return s >= area(a, b, d) + area(a, d, c) + area(b, d, c);
    }

    private int area(Vector3f a, Vector3f b, Vector3f c) {
        return (int) Math.abs((a.getX() - c.getX()) * (b.getY() - c.getY()) + (b.getX() - c.getX()) * (c.getY() - a.getY()));
    }
}
