package com.fh.kaernten.estimotelocate.helper;

import android.support.annotation.Nullable;

import com.fh.kaernten.estimotelocate.interfaces.Room2Beacon;
import com.fh.kaernten.estimotelocate.objects.ThreeDimensionalVector;
import com.lemmingapex.trilateration.LinearLeastSquaresSolver;
import com.lemmingapex.trilateration.NonLinearLeastSquaresSolver;
import com.lemmingapex.trilateration.TrilaterationFunction;

import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.exception.TooManyIterationsException;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;
import org.apache.commons.math3.linear.RealVector;

import java.util.List;

public class TrilaterationHelper {

    private List<Room2Beacon> beacons;
    private TestType testType;

    private double[][] positions;
    private double[] distances;

    public TrilaterationHelper(TestType type) {
        this.testType = type;
    }

    public boolean trilaterationIsPossible(List<Room2Beacon> beacons) {
        this.beacons = beacons;
        TestDimension dimension = testType.getAccordingDimension();
        if (dimension != null && beacons != null
                && ((beacons.size() >= 3 && dimension == TestDimension.TwoDimensional)
                || (beacons.size() >= 4 && dimension == TestDimension.ThreeDimensional))
                && beacons.get(0).getAccuracy() != null) {
            initializeMatrices();
            return true;
        }
        return false;
    }

    /**
     * @return optimum of trilateration function (containing coordinates extra information like
     * and standard deviation). This method may fail in some cases and return null.
     */
    @Nullable
    public LeastSquaresOptimizer.Optimum getOptimum() {
        if (beacons != null) {
            return solveTrilaterationProblem();
        }
        return null;
    }

    /**
     * Call this for testing only - this tries to find a linear solution which may fail in many cases
     *
     * @return linear solution
     */
    public RealVector getLinearSolution() {
        if (beacons != null) {
            try {
                LinearLeastSquaresSolver solver = new LinearLeastSquaresSolver(
                        new TrilaterationFunction(positions, distances));
                return solver.solve(true);
            } catch (TooManyEvaluationsException | TooManyIterationsException e) {
                // optimizer failed for input values
            }
        }
        return null;
    }

    @Nullable
    private LeastSquaresOptimizer.Optimum solveTrilaterationProblem() {
        try {
            NonLinearLeastSquaresSolver solver = new NonLinearLeastSquaresSolver(
                    new TrilaterationFunction(positions, distances), new LevenbergMarquardtOptimizer());
            return solver.solve(true);
        } catch (TooManyEvaluationsException | TooManyIterationsException e) {
            // optimizer failed for input values
        }
        return null;
    }

    private void initializeMatrices() {
        TestDimension dimension = testType.getAccordingDimension();
        boolean useThreeDimensions = beacons.size() > 3 && dimension != null
                && dimension.equals(TestDimension.ThreeDimensional);
        positions = new double[beacons.size()][useThreeDimensions ? 3 : 2];
        distances = new double[beacons.size()];
        for (int i = 0; i < beacons.size(); i++) {
            Room2Beacon beacon = beacons.get(i);
            ThreeDimensionalVector v = beacon.getLocationInRoom();
            positions[i][0] = v.getxCoordinate();
            positions[i][1] = v.getyCoordinate();
            if (useThreeDimensions) {
                positions[i][2] = v.getzCoordinate();
            }
            distances[i] = beacon.getAccuracy();
        }
    }
}