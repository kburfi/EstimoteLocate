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
import com.fh.kaernten.estimotelocate.test_objects.LivingRoomBuilder;

import org.apache.commons.math3.linear.RealVector;

import java.util.List;

/**
 * Tests position in background every 15 minutes until battery is empty
 * For every test 5 values are saved, where the first must be non-null
 * Created by Kristian on 07.06.2017.
 */
public class MovementMonitoringTest implements MobileTest, BeaconManager.BeaconRangingListener {

    private RealVector actualLocation;
    private Room testRoom;
    private Context applicationContext;
    private Integer testCounter;
    private Intent wakeLockIntent;
    private TrilaterationAverage averageHelper;

    @Override
    public TestType getType() {
        return TestType.MOVEMENT_MONITORING;
    }

    @Override
    public void startTest(Context context, AlarmManager alarmManager, PendingIntent alarmPendingIntent) {
        testCounter = null;
        actualLocation = LivingRoomBuilder.getTestLocation1(getType());
        testRoom = new LivingRoomBuilder(context).getRoomForTesting(3);
        averageHelper = new TrilaterationAverage(getType());
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + AlarmManager.INTERVAL_FIFTEEN_MINUTES,
                AlarmManager.INTERVAL_FIFTEEN_MINUTES, alarmPendingIntent);
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
        if (testCounter < 5) {
            findAndSaveCurrentLocation(list);
        } else {
            stopTestAndReleaseLock(applicationContext);
        }
    }

    private void findAndSaveCurrentLocation(List<Beacon> list) {
        RealVector average = averageHelper.getNewAverage(testRoom.filterForBeaconsInThisRoom(list));
        if (average != null) {
            testCounter++;  // starting with first non null result!
            TestResult testResult = new TestResult(applicationContext, getType());
            ThreeDimensionalVector averageDb = new ThreeDimensionalVector(applicationContext, average);
            testResult.setCorrectedId(averageDb.getId());
            testResult.setDistanceToActualLocation(actualLocation.getDistance(average));
            getTestResultDao(applicationContext).insert(testResult);
            updateActivityIfPossible();
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