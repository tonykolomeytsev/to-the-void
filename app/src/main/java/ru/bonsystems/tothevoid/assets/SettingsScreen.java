package ru.bonsystems.tothevoid.assets;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

import ru.bonsystems.tothevoid.R;
import ru.bonsystems.tothevoid.assets.transitions.StartTransition;
import ru.bonsystems.tothevoid.assets.utils.Pallete;
import ru.bonsystems.tothevoid.platform.Config;
import ru.bonsystems.tothevoid.platform.Controller;
import ru.bonsystems.tothevoid.platform.GameObject;
import ru.bonsystems.tothevoid.platform.GameScreen;
import ru.bonsystems.tothevoid.platform.extend.Log;

/**
 * Created by Kolomeytsev Anton on 25.02.2016.
 */
public class SettingsScreen extends GameScreen implements View.OnTouchListener {


    private Label header;
    private Label[] prefs;

    public SettingsScreen() {

    }

    @Override
    public void init() {
        prefs = new Label[3];
        header = new Label(Controller.getInstance().getResources().getString(R.string.settings_header), 60f, 40f, 80f);

        String label1 = Controller.getInstance().getResources().getString(R.string.pref_1);
        String label2 = Controller.getInstance().getResources().getString(R.string.pref_2);
        String label3 = Controller.getInstance().getResources().getString(R.string.pref_3);
        String label4 = Controller.getInstance().getString(R.string.debug_mode);
        prefs[0] = new Label(label1 + " " + (int)Config.scrWidth + " x " + (int)Config.scrHeight, 30f, 40f, 160f);
        prefs[1] = new Label(label2 + " " + (int)Config.RENDER_WIDTH + " x " + (int)Config.RENDER_HEIGHT, 30f, 40f, 220f);
        prefs[2] = new Label(label3, 30f, 40f, 280f);
    }

    @Override
    public void update(float delta) {
    }

    @Override
    public void render() {
        canvas.drawColor(Pallete.SPACE_LIGHT);
        header.render(canvas);
        for (int i = 0; i < prefs.length; i++) {
            prefs[i].render(canvas);
        }
    }

    @Override
    public void onShow() {
        Log.i(this, "onShow");
    }

    @Override
    public void onHide() {

    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            Controller.getInstance().getPresenter().setOnTouchListener(null);
            Controller.getInstance().getRoot().popScreen(new StartTransition());
        }
        return true;
    }

    class Label extends GameObject {
        final String label;

        public Label(String label, float size) {
            this.label = label;
            (paint = new Paint()).setColor(Pallete.ACCENT_LIGHT);
            paint.setAntiAlias(true);
            paint.setTypeface(loadTypefaceFromAssets("fonts/caviar.ttf"));
            paint.setTextSize(size);
        }

        public Label(String label, float size, float x, float y) {
            this(label, size);
            setPosition(x, y);
        }

        public Label setPosition(float x, float y) {
            this.x = x;
            this.y = y;
            return this;
        }

        @Deprecated
        @Override
        public void update(float delta) {
        }

        @Override
        public void render(Canvas canvas) {
            canvas.drawText(label, x, y, paint);
        }
    }
}
