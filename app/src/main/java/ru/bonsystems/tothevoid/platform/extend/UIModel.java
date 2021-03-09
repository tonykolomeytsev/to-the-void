package ru.bonsystems.tothevoid.platform.extend;

import android.view.MotionEvent;

import java.util.ArrayList;

import ru.bonsystems.tothevoid.platform.extend.uis.Control;

/**
 * Created by Kolomeytsev Anton
 */
public class UIModel {
    private ArrayList<Control> controls = new ArrayList<>();

    public UIModel addControl(Control control) {
        controls.add(control);
        return this;
    }

    public UIModel removeControl(Control control) {
        controls.remove(control);
        return this;
    }

    public void onTouch(MotionEvent event) {
        for (Control control : controls) {
            control.onTouch(event);
        }
    }
}
