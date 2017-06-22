package com.fh.kaernten.estimotelocate.interfaces;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.fh.kaernten.estimotelocate.helper.TestType;

import java.io.Serializable;

/**
 * Defines instrumentation tests which run without computer being connected.
 * Created by Kristian on 24.05.2017.
 */
public interface MobileTest extends Serializable {

    TestType getType();

    void startTest(Context context,
                   AlarmManager alarmManager,
                   PendingIntent alarmPendingIntent);

    void stopTest(Context context);

    /**
     * @param wakeLockIntent     after test is complete the intent <b>must</b> be released by calling
     *                           AlarmBroadcastReceiver.completeWakefulIntent(wakeLockIntent);
     * @param applicationContext context of application
     */
    void testAndSaveCurrentValue(Intent wakeLockIntent, Context applicationContext);
}