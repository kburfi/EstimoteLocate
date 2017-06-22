package com.fh.kaernten.estimotelocate.testcases;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import com.fh.kaernten.estimotelocate.EstimoteApplication;
import com.fh.kaernten.estimotelocate.alarm.AlarmBroadcastReceiver;
import com.fh.kaernten.estimotelocate.helper.TestType;
import com.fh.kaernten.estimotelocate.interfaces.MobileTest;
import com.fh.kaernten.estimotelocate.objects.TestResult;
import com.fh.kaernten.estimotelocate.objects.TestResultDao;

/**
 * Test used to measure battery runtime of device
 * Created by Kristian on 24.05.2017.
 */
public class BatteryRuntimeTest implements MobileTest {

    @Override
    public TestType getType() {
        return TestType.BATTERY_RUNTIME;
    }

    @Override
    public void startTest(Context context, AlarmManager alarmManager,
                          PendingIntent alarmPendingIntent) {
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + AlarmManager.INTERVAL_FIFTEEN_MINUTES,
                AlarmManager.INTERVAL_FIFTEEN_MINUTES, alarmPendingIntent);
    }

    @Override
    public void stopTest(Context context) {

    }

    @Override
    public void testAndSaveCurrentValue(Intent wakeLockIntent, Context applicationContext) {
        getTestResultDao(applicationContext).insert(new TestResult(applicationContext, TestType.BATTERY_RUNTIME));
        updateActivityIfPossible(applicationContext);
        AlarmBroadcastReceiver.completeWakefulIntent(wakeLockIntent);
    }

    private TestResultDao getTestResultDao(Context context) {
        return ((EstimoteApplication) context.getApplicationContext()).getDaoSession().getTestResultDao();
    }

    private void updateActivityIfPossible(Context applicationContext) {
        if(applicationContext != null
                && ((EstimoteApplication)applicationContext).getCurrentActivity() != null) {
            ((EstimoteApplication)applicationContext).getCurrentActivity().updateCallback();
        }
    }
}