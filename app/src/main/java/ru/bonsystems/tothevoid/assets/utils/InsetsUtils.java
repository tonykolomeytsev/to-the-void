package ru.bonsystems.tothevoid.assets.utils;

import android.view.View;

import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import ru.bonsystems.tothevoid.platform.Config;

public class InsetsUtils {

    private static void doOnApplyWindowInsets(View view, InsetsListener f) {
        InitialPadding initialPadding = new InitialPadding(view);
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            f.applyInsets(v, insets, initialPadding);
            return insets;
        });
        requestApplyInsetsWhenAttached(view);
    }

    private static void requestApplyInsetsWhenAttached(View view) {
        if (view.isAttachedToWindow()) {
            view.requestApplyInsets();
        } else {
            view.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {

                @Override
                public void onViewDetachedFromWindow(View v) {
                    /* no-op */
                }

                @Override
                public void onViewAttachedToWindow(View v) {
                    v.removeOnAttachStateChangeListener(this);
                    v.requestApplyInsets();
                }
            });
        }
    }

    @FunctionalInterface
    private interface InsetsListener {
        void applyInsets(View v, WindowInsetsCompat wic, InitialPadding ip);
    }

    private static class InitialPadding {
        final int left;
        final int top;
        final int right;
        final int bottom;

        public InitialPadding(View view) {
            this.left = view.getPaddingLeft();
            this.top = view.getPaddingTop();
            this.right = view.getPaddingRight();
            this.bottom = view.getPaddingBottom();
        }
    }

    public static void addSystemTopPadding(View view) {
        doOnApplyWindowInsets(view, (v, insets, padding) -> {
            Config.systemTopPadding = padding.top + insets.getSystemWindowInsetTop();
        });
    }
}
