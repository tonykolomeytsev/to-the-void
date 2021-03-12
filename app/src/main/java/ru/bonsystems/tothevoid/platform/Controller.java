package ru.bonsystems.tothevoid.platform;

import android.app.Activity;
import android.app.Application;
import android.graphics.Canvas;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import java.util.Stack;

import ru.bonsystems.tothevoid.R;

/**
 * Created by Kolomeytsev Anton
 */
public class Controller extends Application implements SurfaceHolder.Callback, Runnable {
    private static Controller ourInstance; // = new Controller();
    private Root root;
    private SurfaceView presenter;
    private Thread performThread;
    private boolean isRunning = false;

    public static Controller getInstance() {
        return ourInstance;
    }

    public Controller() {
    }

    /**
     * Происходит раньше создания Activity
     */
    @Override
    public void onCreate() {
        super.onCreate();
        ourInstance = this;
    }

    /**
     * Activity сообщает о своём появлении передавая ссылку на себя контроллеру
     *
     * @param activity - ссылка на Activity
     */
    public void onActivityCreate(AppCompatActivity activity) {
        Config.setUpByActivity(activity);
        activity.getLifecycle().addObserver(new GameLifecycleObserver());

        keepScreenOn(activity);

        presenter = new SurfaceView(activity);
        presenter.getHolder().addCallback(this);

        ((LinearLayout) activity.findViewById(R.id.surface_content)).addView(presenter);
        if (root == null) root = new Root();
    }

    private void keepScreenOn(Activity activity) {
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public Root getRoot() {
        return root;
    }

    public SurfaceView getPresenter() {
        return presenter;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        resume();
    }

    private void resume() {
        final boolean isApplicationAlreadyActive = performThread != null;
        if (!isApplicationAlreadyActive && !isRunning) {
            isRunning = true;
            performThread = new Thread(this);
            performThread.setName("MiniPlatform MainLoop");
            performThread.setPriority(Thread.MAX_PRIORITY);
            performThread.start();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        pause();
    }

    private void pause() {
        final boolean isApplicationAlreadyActive = performThread != null;
        try {
            if (isApplicationAlreadyActive && isRunning) {
                isRunning = false;
                performThread.join();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            performThread = null;
        }
    }

    /**
     * Это то, что будет происходить когда Activity в состоянии Active
     */
    @Override
    public void run() {
        Canvas canvas;
        final SurfaceHolder surfaceHolder = presenter.getHolder();
        long startTime, elapsedTime = 0;
        while (isRunning) {
            startTime = System.currentTimeMillis();
            canvas = null;
            try {
                canvas = surfaceHolder.lockCanvas(null);
                synchronized (surfaceHolder) {
                    root.run(canvas, elapsedTime / 1000f);
                }
                elapsedTime = System.currentTimeMillis() - startTime;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (canvas != null) surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

    static class GameLifecycleObserver implements LifecycleObserver {

        private Stack<GameScreen> getScreenStack() {
            return Controller.getInstance().root.getScreenStack();
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_START)
        public void onStart() {
            onResume();
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
        public void onResume() {
            if (getScreenStack().size() > 1) getScreenStack().peek().onShow();
            Controller.getInstance().resume();
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        public void onPause() {
            if (getScreenStack().size() > 1) getScreenStack().peek().onHide();
            Controller.getInstance().pause();
        }
    }
}
