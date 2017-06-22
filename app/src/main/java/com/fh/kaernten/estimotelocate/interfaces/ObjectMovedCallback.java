package com.fh.kaernten.estimotelocate.interfaces;


public interface ObjectMovedCallback {

    /**
     * Called when coordinates of movedObject have changed
     *
     * @param movedObject the object which has changed
     */
    void onObjectMoved(MovableObject movedObject);
}