package com.fh.kaernten.estimotelocate.interfaces;

public interface MovableObject {

    /**
     * called when this object was moved
     *
     * @param x new x coordinate
     * @param y new y coordinate
     */
    void setNewCoordinates(float x, float y);

    /**
     * @param touchX      touched x coordinate
     * @param touchY      touched y coordinate
     * @param touchRadius acceptance radius of touch
     * @return true when touched point is within MovableObject
     */
    boolean wasTouched(float touchX, float touchY, float touchRadius);
}
