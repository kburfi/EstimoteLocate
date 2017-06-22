package com.fh.kaernten.estimotelocate.testcases;

import android.content.Context;

import com.fh.kaernten.estimotelocate.helper.TestType;
import com.fh.kaernten.estimotelocate.objects.Room;
import com.fh.kaernten.estimotelocate.test_objects.HallwayBuilder;

/**
 * Test with three beacons for two dimensional trilateration
 * Created by Kristian on 30.05.2017.
 */
public class AccuracyTest2D extends AbstractLocationTest {

    @Override
    public TestType getType() {
        return TestType.ACCURACY_2D;
    }

    @Override
    protected Room defineTestRoom(Context context) {
        return new HallwayBuilder(context).getRoomForTesting(3);
    }
}