package com.fh.kaernten.estimotelocate.testcases;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import com.estimote.coresdk.observation.region.beacon.BeaconRegion;
import com.estimote.coresdk.recognition.packets.Beacon;
import com.estimote.coresdk.service.BeaconManager;
import com.fh.kaernten.estimotelocate.EstimoteApplication;
import com.fh.kaernten.estimotelocate.alarm.AlarmBroadcastReceiver;
import com.fh.kaernten.estimotelocate.helper.TestType;
import com.fh.kaernten.estimotelocate.helper.TrilaterationAverage;
import com.fh.kaernten.estimotelocate.interfaces.MobileTest;
import com.fh.kaernten.estimotelocate.objects.Room;
import com.fh.kaernten.estimotelocate.objects.TestResult;
import com.fh.kaernten.estimotelocate.objects.TestResultDao;
import com.fh.kaernten.estimotelocate.objects.ThreeDimensionalVector;
import com.fh.kaernten.estimotelocate.test_objects.HallwayBuilder;

import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.SingularMatrixException;

import java.util.List;

/**
 * Abstract Test for exactness of trilateration
 */
abstract class AbstractLocationTest implements MobileTest, BeaconManager.BeaconRangingListener {

    private RealVector actualLocation;
    private Room testRoom;
    private Context applicationContext;
    private Integer testCounter;
    private Intent wakeLockIntent;
    private TrilaterationAverage averageHelper;

    @Override
    public abstract TestType getType();

    protected abstract Room defineTestRoom(Context context);

    @Override
    public void startTest(Context context, AlarmManager alarmManager, PendingIntent alarmPendingIntent) {
        testCounter = null;
        actualLocation = HallwayBuilder.getTestLocation3(getType());
        testRoom = defineTestRoom(context);
        averageHelper = new TrilaterationAverage(getType());
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + 5000L, alarmPendingIntent);
        ((EstimoteApplication) context.getApplicationContext()).getBeaconManager().setRangingListener(this);
    }

    @Override
    public void stopTest(Context context) {
        stopTestAndReleaseLock(context);
    }

    @Override
    public void testAndSaveCurrentValue(Intent wakeLockIntent, Context applicationContext) {
        testCounter = 0;
        this.wakeLockIntent = wakeLockIntent;
        this.applicationContext = applicationContext;
        ((EstimoteApplication) applicationContext).startRanging();
    }

    @Override
    public void onBeaconsDiscovered(BeaconRegion beaconRegion, List<Beacon> list) {
        if (testCounter < 100) {
            testCounter++;
            findAndSaveCurrentLocation(list);
        } else {
            stopTestAndReleaseLock(applicationContext);
        }
    }

    private void findAndSaveCurrentLocation(List<Beacon> list) {
        TestResult testResult = new TestResult(applicationContext, getType());
        trilaterateUser(list, testResult);
        setInfoOnExactMeasurement(testResult);
        setLinearSolution(list, testResult);
        getTestResultDao(applicationContext).insert(testResult);
        updateActivityIfPossible();
    }

    private void setLinearSolution(List<Beacon> list, TestResult testResult) {
        RealVector linear = averageHelper.getLinearSolution(testRoom.filterForBeaconsInThisRoom(list));
        if (linear != null) {
            ThreeDimensionalVector linearDb = new ThreeDimensionalVector(applicationContext, linear);
            testResult.setLinearId(linearDb.getId());
        }
    }

    private void trilaterateUser(List<Beacon> list, TestResult testResult) {
        RealVector average = averageHelper.getNewAverage(testRoom.filterForBeaconsInThisRoom(list));
        if (average != null) {
            ThreeDimensionalVector averageDb = new ThreeDimensionalVector(applicationContext, average);
            testResult.setCorrectedId(averageDb.getId());
            if (average.getDimension() == actualLocation.getDimension()) {
                testResult.setDistanceToActualLocation(actualLocation.getDistance(average));
            }
        }
    }

    private void setInfoOnExactMeasurement(TestResult testResult) {
        LeastSquaresOptimizer.Optimum optimum = averageHelper.getLastExplicitResult();
        if (optimum != null) {
            RealVector point = optimum.getPoint();
            ThreeDimensionalVector calculated = new ThreeDimensionalVector(applicationContext, point);
            testResult.setCalculatedId(calculated.getId());
            try {
                Double sigma = optimum.getSigma(0).getNorm();
                testResult.setNormOfStandardDeviation(sigma);
            } catch (SingularMatrixException e) {
                // matrix may be singular in certain cases (dependent on measurement)
            }
        }
    }

    private TestResultDao getTestResultDao(Context context) {
        return ((EstimoteApplication) context.getApplicationContext()).getDaoSession().getTestResultDao();
    }

    private void stopTestAndReleaseLock(Context context) {
        ((EstimoteApplication) context.getApplicationContext()).stopRanging();
        applicationContext = null;
        testCounter = null;
        if (wakeLockIntent != null) {
            AlarmBroadcastReceiver.completeWakefulIntent(wakeLockIntent);
        }
        wakeLockIntent = null;
    }

    private void updateActivityIfPossible() {
        if (applicationContext != null
                && ((EstimoteApplication) applicationContext).getCurrentActivity() != null) {
            ((EstimoteApplication) applicationContext).getCurrentActivity().updateCallback();
        }
    }
}