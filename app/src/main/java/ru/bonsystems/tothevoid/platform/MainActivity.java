package ru.bonsystems.tothevoid.platform;

import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import ru.bonsystems.tothevoid.R;
import ru.bonsystems.tothevoid.assets.game.BaseGame;
import ru.bonsystems.tothevoid.assets.game.screens.PauseScreen;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Controller.getInstance().onActivityCreate(this);
    }

    @Override
    public void onBackPressed() {
        Root root = Controller.getInstance().getRoot();
        if (root.getScreenStack().peek() instanceof BaseGame) {
            root.pushScreen(new PauseScreen());
        } else if (root.getScreenStack().size() > 1) /* ИМЕННО ЕДИНИЧКА! */ {
            root.popScreen();
        } else {
            super.onBackPressed();
        }
    }

    public void hideSystemUI() {
        View mDecorView = getWindow().getDecorView();
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        mDecorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            getWindow().getDecorView().setAlpha(0.0f);
        }
    }
}
