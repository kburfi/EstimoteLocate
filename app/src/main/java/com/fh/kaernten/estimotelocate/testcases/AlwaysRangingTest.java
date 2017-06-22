package com.fh.kaernten.estimotelocate.testcases;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.estimote.coresdk.observation.region.beacon.BeaconRegion;
import com.estimote.coresdk.recognition.packets.Beacon;
import com.estimote.coresdk.service.BeaconManager;
import com.fh.kaernten.estimotelocate.EstimoteApplication;
import com.fh.kaernten.estimotelocate.alarm.AlarmBroadcastReceiver;
import com.fh.kaernten.estimotelocate.helper.TestType;
import com.fh.kaernten.estimotelocate.helper.TrilaterationHelper;
import com.fh.kaernten.estimotelocate.interfaces.MobileTest;
import com.fh.kaernten.estimotelocate.objects.Room;
import com.fh.kaernten.estimotelocate.objects.TestResult;
import com.fh.kaernten.estimotelocate.objects.TestResultDao;
import com.fh.kaernten.estimotelocate.objects.ThreeDimensionalVector;
import com.fh.kaernten.estimotelocate.test_objects.LivingRoomBuilder;

import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer;
import org.apache.commons.math3.linear.RealVector;

import java.util.List;

/**
 * Test battery runtime when running indoor localization with ranging in background all the time
 */
public class AlwaysRangingTest implements MobileTest, BeaconManager.BeaconRangingListener {

    private RealVector actualLocation;
    private Room testRoom;
    private Context applicationContext;
    private Intent wakeLockIntent;
    private TrilaterationHelper trilaterationHelper;

    @Override
    public TestType getType() {
        return TestType.ALWAYS_RANGING;
    }

    @Override
    public void startTest(Context context, AlarmManager alarmManager, PendingIntent alarmPendingIntent) {
        actualLocation = LivingRoomBuilder.getTestLocation1(getType());
        testRoom = new LivingRoomBuilder(context).getRoomForTesting(3);
        trilaterationHelper = new TrilaterationHelper(getType());
        applicationContext = context.getApplicationContext();
        ((EstimoteApplication) applicationContext).getBeaconManager().setRangingListener(this);
        ((EstimoteApplication) applicationContext).startRanging();
    }

    @Override
    public void stopTest(Context context) {
        stopTestAndReleaseLock(context);
    }

    @Override
    public void testAndSaveCurrentValue(Intent wakeLockIntent, Context applicationContext) {

    }

    @Override
    public void onBeaconsDiscovered(BeaconRegion beaconRegion, List<Beacon> list) {
        findAndSaveCurrentLocation(list);
    }

    private void findAndSaveCurrentLocation(List<Beacon> list) {
        TestResult testResult = new TestResult(applicationContext, getType());
        trilaterateUser(list, testResult);
        getTestResultDao(applicationContext).insert(testResult);
        updateActivityIfPossible();
    }

    private void trilaterateUser(List<Beacon> list, TestResult testResult) {
        if (trilaterationHelper.trilaterationIsPossible(testRoom.filterForBeaconsInThisRoom(list))) {
            LeastSquaresOptimizer.Optimum explicit = trilaterationHelper.getOptimum();
            RealVector v = explicit.getPoint();
            ThreeDimensionalVector e = new ThreeDimensionalVector(applicationContext, v);
            testResult.setCalculatedId(e.getId());
            testResult.setDistanceToActualLocation(actualLocation.getDistance(v));
        }
    }

    private TestResultDao getTestResultDao(Context context) {
        return ((EstimoteApplication) context.getApplicationContext()).getDaoSession().getTestResultDao();
    }

    private void stopTestAndReleaseLock(Context context) {
        ((EstimoteApplication) context.getApplicationContext()).stopRanging();
        applicationContext = null;
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
