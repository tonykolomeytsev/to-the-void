package ru.bonsystems.tothevoid.assets.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

import java.util.HashMap;
import java.util.Random;

import ru.bonsystems.tothevoid.assets.utils.Vector3f;
import ru.bonsystems.tothevoid.platform.Config;
import ru.bonsystems.tothevoid.platform.GameObject;

/**
 * Created by Kolomeytsev Anton on 23.02.2016.
 */
public class Asteroid extends GameObject{
    private final static float MAX_RADIUS = 130f;
    private float maxCornerRadius = 0;
    private float maxX = 0f, maxY = 0f, minX = MAX_RADIUS, minY = MAX_RADIUS;

    private int cornersCount;
    private float angleVelocity;
    private float xVelocity;
    private float yVelocity;
    private float x;
    private float y;
    private Vector3f center;
    private Vector3f ncenter = new Vector3f();
    private Vector3f[] cornersPoints;
    private Path polygon = new Path();
    private Canvas canvas;

    private final static Paint xferPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final static Paint clearPaint = new Paint();
    public final static HashMap<String, Bitmap> textures = new HashMap<>();
    private final static Random RANDOM = new Random();
    private static boolean firstLaunch = true;
    private boolean alive;


    public Asteroid(){
        /**
         * В конструкторе храним ссылку на StateManager
         * После первого создания создаём Paint белого цвета
         * При первом создании запускаем generate() - то есть генерируем сам астероид
         * */
        if (firstLaunch) {
            xferPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            // textures заполняется при запуске
            firstLaunch = false;
        }

        alive = true;
        transform = new Matrix();
        (paint = new Paint()).setAntiAlias(true);

        generate();
    }

    @Override
    public void update(float delta) {
        incrementRotationAndPositionVariables(delta);
        rotateCollisionPoints();

        transform.setRotate(angle, center.getX(), center.getX());
        transform.postTranslate(x, y - Camera.getInstance().getY());

        checkFlightOffTheScreen();
    }

    /*
    * Наконец, астероидам позволено самим себя рисовать! Теперь Добби свободен!
    * Собственно, сама перерисовка, создана для того чтобы астероиды не лагали, сделано с любовью и по правилам ООП
    * */
    @Override
    public void render(Canvas canvas) {
        if (x < MAX_RADIUS + Config.RENDER_WIDTH) canvas.drawBitmap(texture, transform, paint);
    }

    private void generate(){
        /**
         * Логическая часть
         * Геренрация всех точек и бла-бла
         * */
        determineTheSizePositionAndAngles();
        createAngles();
        findTheCenter();
        moveAllPointsToTheCenter();

        /**
         * Графическая часть
         * Использование сгенерированных данных для рисования своего скина (Астероида)
         * Короче, изображение астероида bitmap создаётся тут
         * */
        generateBitmap();
    }

    private void determineTheSizePositionAndAngles(){
        /*
        * Задаём количество углов будущего астероида
        * */
        cornersCount = RANDOM.nextInt(5) + 3;
        /*
        * Генерируем его угловую скорость.
        * Я забыл, что тут написано. Главное - работает.
        * */
        angleVelocity = (float) ((RANDOM.nextFloat() * Math.PI) - Math.PI / 2f) * 12f;
        /*
        * Задаём координаты астероида
        * Нужно поместить его за пределы экрана, но не за пределы оперативной памяти, всё пропорционально размерам экрана
        * */
        x = (Config.RENDER_WIDTH + MAX_RADIUS) + (RANDOM.nextFloat() * MAX_RADIUS * 3f);
        y = RANDOM.nextFloat() * (Camera.getInstance().getAreaHeight() - MAX_RADIUS);
        /*
        * Скорости для астероидов.
        * Их нужно сделать пропорциональными тоже
        * */
        xVelocity = 400f + GameState.gameSpeed * 200f + RANDOM.nextFloat() * 200f;// RANDOM.nextInt(15) + 25;
        yVelocity = 0;
    }

    private void createAngles(){
        /*
        * Объявляем точки с координатами углов астероида
        * Количество углов сгенерировано в предыдущей процедуре
        * */
        cornersPoints = new Vector3f[cornersCount];
        /*
         * Циклом проходимся по каждой переменной, хранящей информацию об угле
         * Тип переменной Vector2i, вынесенный в отдельный класс. Используется вместо Point из-за специфики архитектуры приложения,
         * а если говорить поточней, то при использовании Point, будет невозможна обработка коллизии, если астероиды будут вращаться
         * */
        for (int i = 0; i < cornersCount; i++) {
            cornersPoints[i] = new Vector3f();
            /*
            * переменная angle тут лишь временная. Используется для расчёта позиции угла в Декартовых координатах
            * */
            float angle = (float)((Math.PI * 2) / cornersCount) * i;
            /*
            * Тот самый специфичный .size, которого нет в Point. Является неизменным полем до самой смерти астероида в небытии отрицательных значений X
            * Это расстояние от условноо центра астероида до угла
            * */
            cornersPoints[i].setZ(MAX_RADIUS - (RANDOM.nextFloat() * (MAX_RADIUS * 0.5f)));
            /*
            * Полученное расстояние .z мы помножаем на синус и косинус нашего angle, чтобы получить Y и X угла
            * */
            cornersPoints[i].setX((float) (cornersPoints[i].getZ() * Math.cos(angle)));
            cornersPoints[i].setY((float) (cornersPoints[i].getZ() * Math.sin(angle)));
            /*
            * Заодно ищем максимальное расстояние до угла
            * Это понадобится для оптимизиции коллизии и генерации текстуры
            * */

            if (maxCornerRadius < cornersPoints[i].getZ()) maxCornerRadius = cornersPoints[i].getZ();
        }

        angle = (float) (RANDOM.nextFloat() * Math.PI * 2f);
    }

    private void findTheCenter(){
        /*
        * Находим условный центр фигуры
        * Вокруг него мы будем вращать и ещё... что-то
        * */
        center = new Vector3f();
        center.setX(maxCornerRadius);
        center.setY(maxCornerRadius);

    }

    /**
     * Сдвигаем все координаты углов так, чтобы
     * они стали вокруг центра.
     * Да, они и так были вокруг центра, пока центр был в точке (0; 0), но некоторые точки имели отрицательные координаты,
     * а это недопустимо в расчётах коллизии. Всё, что меньше нуля всегда при пересечении возвратит false,
     * поэтому я смещаю все координаты на максимальную возможную координату, т.к. астероид ещё будет вращаться
     **/
    private void moveAllPointsToTheCenter(){
        for (int i = 0; i < cornersCount; i++) {
            cornersPoints[i].setX(cornersPoints[i].getX() + center.getX());
            cornersPoints[i].setY(cornersPoints[i].getY() + center.getY());
        }
    }

    private void generateBitmap() {
        texture = Bitmap.createBitmap((int) (maxCornerRadius * 2f), (int) (maxCornerRadius * 2f), Bitmap.Config.ARGB_8888);
        canvas = new Canvas(texture);

        polygon.reset();
        polygon.moveTo(cornersPoints[0].getX(), cornersPoints[0].getY());
        for (int i = 0; i < cornersCount - 1; i++){
            polygon.lineTo(cornersPoints[i].getX(), cornersPoints[i].getY());
        }
        polygon.lineTo(cornersPoints[cornersCount - 1].getX(), cornersPoints[cornersCount - 1].getY());

        /*
        * Делаем эффект фотошопа "обтравка по фигуре"
        * xFerPaint закэширован мною для оптимизации
        **/
        Bitmap scaledDoge = null;
        if (GameState.jokeDoge) {
            scaledDoge = Bitmap.createScaledBitmap(textures.get("doge"), (int) (maxCornerRadius * 2f), (int) (maxCornerRadius * 2f), true);
        } else {
            final String textureName = String.format("%dx_corners_%d", cornersCount, RANDOM.nextInt(1));
            scaledDoge = Bitmap.createScaledBitmap(textures.get(textureName), (int) (maxCornerRadius * 2f), (int) (maxCornerRadius * 2f), true);
        }
        canvas.drawPath(polygon, paint);
        canvas.drawBitmap(scaledDoge, 0, 0, xferPaint);
    }

    /*
    * Обновление состояния, т.е. всё теперь сделано с отдделением Отображения от Логики
    * По непонятным причинам, без этого игра лагает.
    * UPD 23/02/2016: Лагает из-за небезопасной многопоточности. Теперь исправлено
    * */
    private void incrementRotationAndPositionVariables(float delta) {
        angle += angleVelocity * delta; // "доповорот"
        x -= xVelocity * delta;
        y += yVelocity * delta;
    }

    public void rotateCollisionPoints() {
        for (int i = 0; i < cornersCount; i++) {
            float enumerateAngle = (float)((Math.PI * 2) / cornersCount) * i;
            cornersPoints[i].setX((float) (cornersPoints[i].getZ() * Math.cos(enumerateAngle + (angle / 57.2957795786f))) + center.getX());
            cornersPoints[i].setY((float) (cornersPoints[i].getZ() * Math.sin(enumerateAngle + (angle / 57.2957795786f))) + center.getY());
        }
    }

    private void checkFlightOffTheScreen() {
        if (x < (-maxCornerRadius * 2)) alive = false;
    }

    public boolean isAlive() {
        return alive;
    }

    private Paint redPaint = new Paint();
    private void drawDebug(Canvas canvas) {
        redPaint.setColor(Color.RED);
        for (int i = 0; i < cornersCount; i++){
            canvas.drawCircle(cornersPoints[i].getX() + x, cornersPoints[i].getY() + y - Camera.getInstance().getY(), 3, redPaint);
            canvas.drawText("(" + cornersPoints[i].getX() + ";" + cornersPoints[i].getY() + ")", cornersPoints[i].getX() + x, cornersPoints[i].getY() + y - Camera.getInstance().getY(), redPaint);
        }
        canvas.drawCircle(center.getX() + x, center.getY() + y - Camera.getInstance().getY(), 3, redPaint);
        canvas.drawText("(" + center.getX() + ";" + center.getY() + ")", center.getX() + x, center.getY() + y - Camera.getInstance().getY(), redPaint);
    }

    /*
    * Имплементированные методы, которые могут понадобится в случае потребы в няшных эффектах частиц и взрывах
    * Скорее всего, здесь есть что-то важное.
    * */
    public Vector3f[] getPoints() {
        return cornersPoints;
    }

    public Vector3f getCenter() {
        return center;
    }

    public float getXVelocity() {
        return xVelocity;
    }

    public float getMaxCornerRadius() {
        return maxCornerRadius;
    }

    public float getX() {
        return x;
    }

    public Asteroid setX(float x) {
        this.x = x;
        return this;
    }

    public float getY() {
        return y;
    }

    public void clash() {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        paint.setColor(Color.CYAN);
        canvas.drawPath(polygon, paint);
    }
}
