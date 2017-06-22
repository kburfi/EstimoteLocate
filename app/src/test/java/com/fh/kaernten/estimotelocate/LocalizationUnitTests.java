package com.fh.kaernten.estimotelocate;

import com.fh.kaernten.estimotelocate.helper.TestType;
import com.fh.kaernten.estimotelocate.helper.TrilaterationHelper;
import com.fh.kaernten.estimotelocate.interfaces.Room2Beacon;

import org.apache.commons.math3.linear.RealVector;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;


/**
 * Unit test for old iLocate triangulation implementation.
 * Test case is ideal where the measured distances of the beacons are the euclidean distances to the
 * expected result.
 * The result is that iLocate cannot find a good solution (error greater than 1m).
 * As comparison the implementation in this app is exact with 1.4E-14 error.
 */
public class LocalizationUnitTests {
    // iLocate calculates with pixels instead of meters - interpret as 100px = 1m
    private Coordinate b1 = new Coordinate(10, 0);
    private Coordinate b2 = new Coordinate(260, 0);
    private Coordinate b3 = new Coordinate(130, 300);
    private Coordinate expectedResult = new Coordinate(120, 150);
    private List<Room2Beacon> beacons;

    @Test
    public void testExactCaseILocate() throws Exception {
        initializeLocation();
        Coordinate resultOfiLocate = TriangulationCalculation.getPointX(b1, b2, b3,
                b1.getAccuracy(),
                b2.getAccuracy(),
                b3.getAccuracy());
        double distance = expectedResult.getEuclideanDistance(resultOfiLocate);
        assertEquals(0, distance, 0.2d);
    }

    @Test
    public void testExactCaseEstimoteLocate() throws Exception {
        initializeLocation();
        TrilaterationHelper trilaterationHelper = new TrilaterationHelper(TestType.ACCURACY_2D);
        if (!trilaterationHelper.trilaterationIsPossible(beacons)) {
            throw new Exception();
        }
        RealVector linear = trilaterationHelper.getLinearSolution();
        double distance = expectedResult.getEuclideanDistance(linear);
        outputAndTestResult(distance, "Result of linear: " + distance);

        RealVector result = trilaterationHelper.getOptimum().getPoint();
        distance = expectedResult.getEuclideanDistance(result);
        outputAndTestResult(distance, "Result with error correction: " + distance);
    }

    private void outputAndTestResult(double distance, String x) {
        System.out.println(x);
        assertEquals(0, distance, 0.2d);
    }

    private void initializeLocation() {
        beacons = new ArrayList<>();
        beacons.add(b1);
        beacons.add(b2);
        beacons.add(b3);
        b1.setDistanceTo(expectedResult);
        b2.setDistanceTo(expectedResult);
        b3.setDistanceTo(expectedResult);
    }
}
