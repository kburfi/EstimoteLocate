package com.fh.kaernten.estimotelocate.test_objects;

import android.content.Context;

import com.fh.kaernten.estimotelocate.R;
import com.fh.kaernten.estimotelocate.helper.TestType;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

/**
 * Defines dimensions of room and its beacons
 */
public class HallwayBuilder extends AbstractRoomBuilder {

    private final static double[] TEST_ROOM_DIM = {2.73d, 1.77d};

    private final static TestBeacon[] BEACONS = {
            new TestBeacon(65435, 52355, R.color.blueEstimote, new double[]{0d, 0.63d, 1.7d}),
            new TestBeacon(65258, 31998, R.color.blueEstimote, new double[]{2.73d, 0.55d, 1.62d}),
            new TestBeacon(20819, 34080, R.color.greenEstimote, new double[]{1.3d, 0d, 1d}),
            new TestBeacon(35936, 15297, R.color.purpleEstimote, new double[]{1.56d, 1.27d, 1.66d})
    };

    public HallwayBuilder(Context context) {
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

    public static RealVector getTestLocation3(TestType type) {
        int dimension = type.getDimensionAsInt();
        RealVector actualLocation = new ArrayRealVector(dimension);
        actualLocation.setEntry(0, 0.5d);
        actualLocation.setEntry(1, 0.5d);
        if (dimension == 3) actualLocation.setEntry(2, 0d);
        return actualLocation;
    }
}