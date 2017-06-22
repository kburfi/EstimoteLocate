package com.fh.kaernten.estimotelocate.test_objects;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import com.fh.kaernten.estimotelocate.objects.EstimoteBeacon;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

/**
 * Holder class used to define known beacons for predefined test cases
 */
public class TestBeacon {

    private int major;

    private int minor;

    private int colorRes;

    private RealVector location;

    public TestBeacon(int major, int minor, int colorRes, double[] location) {
        this.major = major;
        this.minor = minor;
        this.colorRes = colorRes;
        this.location = new ArrayRealVector(location);
    }

    public EstimoteBeacon getBeacon(Context context) {
        return new EstimoteBeacon(null, major, minor,
                ContextCompat.getColor(context, colorRes));
    }

    public RealVector getLocation() {
        return location;
    }
}
