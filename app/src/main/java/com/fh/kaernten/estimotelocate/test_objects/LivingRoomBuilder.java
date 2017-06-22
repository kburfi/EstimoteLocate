package com.fh.kaernten.estimotelocate.test_objects;

import android.content.Context;

import com.fh.kaernten.estimotelocate.R;
import com.fh.kaernten.estimotelocate.helper.TestType;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;


/**
 * Defines dimensions of room and its beacons
 */
public class LivingRoomBuilder extends AbstractRoomBuilder {

    private final static double[] TEST_ROOM_DIM = {7.04d, 3.86d};

    private final static TestBeacon[] BEACONS = {
            new TestBeacon(65435, 52355, R.color.blueEstimote, new double[]{3.13d, 3.5d, 1.63d}),
            new TestBeacon(20819, 34080, R.color.greenEstimote, new double[]{0.36d, 1d, 1.7d}),
            new TestBeacon(38357, 24908, R.color.purpleEstimote, new double[]{7.04d, 1.5d, 1.6d}),
            new TestBeacon(35936, 15297, R.color.purpleEstimote, new double[]{4.4d, 1.7d, 2.6d})
    };

    public LivingRoomBuilder(Context context) {
        super(context);
    }

    @Override
    protected double[] defineRoomDimension() {
        return TEST_ROOM_DIM;
    }

    @Override
    protected TestBeacon[] defineBeacons() {
        return BEACONS;
    }

    public static RealVector getTestLocation1(TestType type) {
        int dimension = type.getDimensionAsInt();
        RealVector actualLocation = new ArrayRealVector(dimension);
        actualLocation.setEntry(0, 2d);
        actualLocation.setEntry(1, 0.5d);
        if (dimension == 3) actualLocation.setEntry(2, 0.94d);
        return actualLocation;
    }

    public static RealVector getTestLocation2(TestType type) {
        int dimension = type.getDimensionAsInt();
        RealVector actualLocation = new ArrayRealVector(dimension);
        actualLocation.setEntry(0, 6.64d);
        actualLocation.setEntry(1, 3d);
        if (dimension == 3) actualLocation.setEntry(1, 0d);
        return actualLocation;
    }
}