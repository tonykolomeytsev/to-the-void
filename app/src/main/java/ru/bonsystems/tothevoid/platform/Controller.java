package ru.bonsystems.tothevoid.platform;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.LinearLayout;

import ru.bonsystems.tothevoid.R;

/**
 * Created by Kolomeytsev Anton
 */
public class Controller extends Application implements SurfaceHolder.Callback, Runnable, Application.ActivityLifecycleCallbacks {
    private static Controller ourInstance; // = new Controller();
    private Activity activity;
    /**
     * root - упрощенная замена модели приложения
     * presenter - видимая графическая часть приложения
     */
    private Root root;
    private SurfaceView presenter;

    private boolean appIsActive;
    private int framesPerSecond = 0;

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

        createScreenReceiver();
    }

    private void createScreenReceiver() {
        screenReceiver = new ScreenReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(screenReceiver, intentFilter);
    }

    /**
     * Activity сообщает о своём появлении передавая ссылку на себя контроллеру
     * @param activity - ссылка на Activity
     */
    public void onActivityCreate(Activity activity) {
        this.activity = activity;
        Config.setUpByActivity(activity);
        activity.setContentView(R.layout.activity_main);
        registerActivityLifecycleCallbacks(this);

        keepScreenOn(activity);
        ((MainActivity)activity).hideSystemUI();

        presenter = new SurfaceView(activity);
        presenter.getHolder().addCallback(this);

        ((LinearLayout) activity.findViewById(R.id.surface_content)).addView(presenter);
        if (root == null) root = new Root();
    }

    private void keepScreenOn(Activity activity) {
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public Activity getActivity() {
        return activity;
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
        System.out.println("SURFACE CREATED");
        resume();
    }

    private void resume() {
        Log.d("MiniPlatformState", "RESUME (appIsActive already " + appIsActive + " )");
        if (!appIsActive) {
            appIsActive = true;
            Thread performThread = new Thread(this);
            performThread.setName("MiniPlatform MainLoop");
            performThread.setPriority(Thread.MAX_PRIORITY);
            performThread.start();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        System.out.println("SURFACE DESTROYED");
        pause();
    }

    private void pause() {
        Log.d("MiniPlatformState", "PAUSE");
        appIsActive = false;
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Это то, что будет происходить когда Activity в состоянии Active
     */
    @Override
    public void run() {
        long DELAY = 1000 / Config.FPS;
        Canvas canvas;
        final SurfaceHolder surfaceHolder = presenter.getHolder();
        long startTime, elapsedTime = 0;
        while (appIsActive) {
            startTime = System.currentTimeMillis();
            canvas = null;
            try {
                canvas = surfaceHolder.lockCanvas(null);
                synchronized (surfaceHolder) {
                    root.run(canvas, (elapsedTime < DELAY)?(DELAY / 1000f):(elapsedTime / 1000f));
                }
                elapsedTime = System.currentTimeMillis() - startTime;
                framesPerSecond++;
            } catch (Exception e) {
                System.out.println("ERROR: "+Thread.currentThread());
                e.printStackTrace();
            } finally {
                if (canvas != null) surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
        Log.d("MiniPlatform", "onActivityCreated");
    }

    @Override
    public void onActivityStarted(Activity activity) {
        Log.d("MiniPlatform", "onActivityStarted");
        if (root.getScreenStack().size() > 0) root.getScreenStack().peek().onShow();
        resume();
    }

    @Override
    public void onActivityResumed(Activity activity) {
        Log.d("MiniPlatform", "onActivityResumed");
        if (root.getScreenStack().size() > 0) root.getScreenStack().peek().onShow();
        resume();
    }

    @Override
    public void onActivityPaused(Activity activity) {
        Log.d("MiniPlatform", "onActivityPaused");
        if (root.getScreenStack().size() > 0) root.getScreenStack().peek().onHide();
        pause();
    }

    @Override
    public void onActivityStopped(Activity activity) {
        Log.d("MiniPlatform", "onActivityStopped");
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
        Log.d("MiniPlatform", "onActivitySaveInstanceState");
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        Log.d("MiniPlatform", "onActivityDestroyed");
    }

    private class FPSMeter implements Runnable{

        @Override
        public void run() {
            while (appIsActive && Config.fpsTracking) {
                try {
                    System.out.println("FPS: " + framesPerSecond);
                    framesPerSecond = 0;
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private ScreenReceiver screenReceiver;

    public ScreenReceiver getScreenReceiver() {
        return screenReceiver;
    }

    private class ScreenReceiver extends BroadcastReceiver {
        private boolean screenEnabled = true;

        public boolean isScreenEnabled() {
            return screenEnabled;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                System.out.println("Screen turned off");
                screenEnabled = false;
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                System.out.println("Screen turned on");
                screenEnabled = true;
            }
        }
    }
}
