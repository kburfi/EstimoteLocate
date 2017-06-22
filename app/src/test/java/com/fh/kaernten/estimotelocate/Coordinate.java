package com.fh.kaernten.estimotelocate;

import com.fh.kaernten.estimotelocate.interfaces.Room2Beacon;
import com.fh.kaernten.estimotelocate.objects.ThreeDimensionalVector;

import org.apache.commons.math3.linear.RealVector;

/**
 * Test object which works for both iLocate and EstimoteLocate implementations
 * This is only used for testing of old iLocate implementation (which failed)
 */
public class Coordinate implements Room2Beacon {

    public double X;
    public double Y;

    private double distanceToDevice;

    public Coordinate(double x, double y) {
        X = x;
        Y = y;
    }

    public void setDistanceTo(Coordinate to) {
        this.distanceToDevice = getEuclideanDistance(to);
    }

    public double getEuclideanDistance(Coordinate to) {
        return calculateEuclideanDistance(to.X, to.Y);
    }

    public double getEuclideanDistance(RealVector to) {
        return calculateEuclideanDistance(to.getEntry(0), to.getEntry(1));
    }

    private double calculateEuclideanDistance(double x, double y) {
        return Math.sqrt(Math.pow(X - x, 2) + Math.pow(Y - y, 2));
    }

    @Override
    public int getColor() {
        return 0;
    }

    @Override
    public Double getAccuracy() {
        return distanceToDevice;
    }

    @Override
    public ThreeDimensionalVector getLocationInRoom() {
        return new ThreeDimensionalVector(-1L, X, Y, null);
    }
}