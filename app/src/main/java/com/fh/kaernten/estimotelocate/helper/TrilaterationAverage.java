package com.fh.kaernten.estimotelocate.helper;

import android.support.annotation.Nullable;

import com.fh.kaernten.estimotelocate.interfaces.Room2Beacon;

import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

import java.util.LinkedList;
import java.util.List;

public class TrilaterationAverage {

    private LinkedList<RealVector> fifo;
    private TrilaterationHelper trilaterationHelper;

    private RealVector currentAverage;
    private LeastSquaresOptimizer.Optimum lastResult;

    public TrilaterationAverage(TestType type) {
        this.trilaterationHelper = new TrilaterationHelper(type);
        this.fifo = new LinkedList<>();
    }

    @Nullable
    public RealVector getNewAverage(List<Room2Beacon> beacons) {
        if (trilaterationHelper.trilaterationIsPossible(beacons)) {
            lastResult = trilaterationHelper.getOptimum();
            if (lastResult != null) {
                updateQueue();
                calculateNewAverage();
            }
        }
        return currentAverage;
    }

    public LeastSquaresOptimizer.Optimum getLastExplicitResult() {
        return lastResult;
    }

    public RealVector getLinearSolution(List<Room2Beacon> beacons) {
        if (trilaterationHelper.trilaterationIsPossible(beacons)) {
            return trilaterationHelper.getLinearSolution();
        }
        return null;
    }

    private void updateQueue() {
        if (fifo.size() == 10) {
            fifo.poll();
        }
        fifo.offer(lastResult.getPoint());
    }

    private void calculateNewAverage() {
        currentAverage = new ArrayRealVector(fifo.peek().getDimension());
        for (int index = 0; index < currentAverage.getDimension(); index++) {
            for (RealVector v : fifo) {
                currentAverage.setEntry(index, currentAverage.getEntry(index) + v.getEntry(index));
            }
            currentAverage.setEntry(index, currentAverage.getEntry(index) / fifo.size());
        }
    }
}