package ru.bonsystems.tothevoid.platform;

import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import ru.bonsystems.tothevoid.R;
import ru.bonsystems.tothevoid.assets.game.BaseGame;
import ru.bonsystems.tothevoid.assets.game.screens.PauseScreen;
import ru.bonsystems.tothevoid.assets.utils.InsetsUtils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Controller.getInstance().onActivityCreate(this);
        hideSystemUI();
        InsetsUtils.addSystemTopPadding(findViewById(R.id.containerView));
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
                mDecorView.getSystemUiVisibility() |
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        getWindow().getDecorView().setAlpha(0.0f);
    }
}
