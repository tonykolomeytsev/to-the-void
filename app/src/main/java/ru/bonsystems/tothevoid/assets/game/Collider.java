package ru.bonsystems.tothevoid.assets.game;

import ru.bonsystems.tothevoid.assets.game.gameplay.Enemy;
import ru.bonsystems.tothevoid.assets.utils.Vector3f;
import ru.bonsystems.tothevoid.platform.Config;

/**
 * Created by Kolomeytsev Anton on 24.02.2016.
 */
public class Collider {
    private final float buffer_player_full_height;
    private float buffer_quarter_screen_width;
    private Vector3f[] asteroidPoints;
    private Vector3f asteroidCenter;
    private Vector3f[] playerPoints;
    private Runnable clashListener;

    public Collider(Runnable clashListener){
        buffer_quarter_screen_width = (Config.rndWidth / 4f) + 100f;
        buffer_player_full_height = 32f;
        playerPoints = new Vector3f[3];
        this.clashListener = clashListener;
    }

    public boolean collideWithBullet(Player player, Enemy.Bullet bullet) {
        Vector3f[] points = player.getCollisionPoints();
        if (points[0].getX() < bullet.getX())
            if (points[0].getY() < bullet.getY())
                if (points[2].getX() > bullet.getX())
                    if (points[1].getY() > bullet.getY()) {
                        clashListener.run();
                        return true;
                    }

        return false;
    }

    public boolean collideObjects(Asteroid asteroid, Player player) {
        if (asteroid.getX() > buffer_quarter_screen_width) return false;
        if (asteroid.getY() > player.getY() + buffer_player_full_height) return false;
        if (asteroid.getY() + (asteroid.getMaxCornerRadius() * 2) < player.getY()) return false;

        asteroidCenter = asteroid.getCenter();
        asteroidPoints = asteroid.getPoints();

        playerPoints = copyOf(player.getCollisionPoints());
        for (int i = 0; i < playerPoints.length; i++){
            playerPoints[i].setX(playerPoints[i].getX() - asteroid.getX());
            playerPoints[i].setY(playerPoints[i].getY() - asteroid.getY());
        }

        playerPoints.toString();

        for (int i = 0; i < asteroidPoints.length; i++){
            int index2 = i + 1;
            if ((index2) == asteroidPoints.length) index2 = 0;
            for (int j = 0; j < playerPoints.length; j++){
                if (foo(asteroidCenter, asteroidPoints[i], asteroidPoints[index2], playerPoints[j])){
                    clashListener.run();
                    return true;
                }
            }
        }

        return false;
    }

    private Vector3f[] copyOf(Vector3f[] points) {
        Vector3f[] response = new Vector3f[points.length];
        for (int i = 0; i < response.length; i++) {
            response[i] = new Vector3f(points[i].getX(), points[i].getY(), points[i].getZ());
        }
        return response;
    }

    /**
     * Важно! Функция рассчета должна возвращать целое значение,
     * иначе всё идет в сракотень!
     * @param a
     * @param b
     * @param c
     * @param d
     * @return столкнулись или не стулкнулись
     */
    private boolean foo(Vector3f a, Vector3f b, Vector3f c, Vector3f d) {
        int s = area(a, b, c);
        if (s >= area(a, b, d) + area(a, d, c) + area(b, d, c)) {
            return true;
        }
        return false;
    }

    private int area(Vector3f a, Vector3f b, Vector3f c) {
        return (int) Math.abs((a.getX() - c.getX()) * (b.getY() - c.getY()) + (b.getX() - c.getX()) * (c.getY() - a.getY()));
    }

    /// пули

    /**
     *
    public boolean collideWithBullet(Player player, Bullet bullet) {
        //if (GameState.isHyperSpace) return false;
        if (player.isBanged()) return false;

        if (bullet.getX() > player.getX())
            if (bullet.getY() > player.getY())
                if (bullet.getY() < player.getY() + (player.getHalfHeight() << 1))
                    if (bullet.getX() < player.getX() + (player.getHalfWidth() << 1))
                        return true;

        if (player.getX() > bullet.getX())
            if (player.getY() > bullet.getY())
                if (player.getY() < bullet.getY() + bullet.getSize())
                    if (player.getX() < bullet.getX() + bullet.getSize())
                        return true;

        return false;
    }
    * @param player
    * @param bullet
    * @return
    * */
}
