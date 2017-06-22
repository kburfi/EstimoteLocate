package com.fh.kaernten.estimotelocate.interfaces;


import com.fh.kaernten.estimotelocate.objects.ThreeDimensionalVector;

public interface Room2Beacon {

    /**
     * @return int resource of color
     */
    int getColor();

    /**
     * @return null if accuracy shall not be drawn
     */
    Double getAccuracy();

    /**
     * @return three dimensional location of beacon in room
     */
    ThreeDimensionalVector getLocationInRoom();
}