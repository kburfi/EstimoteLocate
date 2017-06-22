package com.fh.kaernten.estimotelocate.helper;

import com.fh.kaernten.estimotelocate.interfaces.MovableObject;


/**
 * fake holder for empty pane which is movable
 */
public class Nothing implements MovableObject {

    @Override
    public void setNewCoordinates(float x, float y) {

    }

    @Override
    public boolean wasTouched(float touchX, float touchY, float touchRadius) {
        return true;
    }
}
