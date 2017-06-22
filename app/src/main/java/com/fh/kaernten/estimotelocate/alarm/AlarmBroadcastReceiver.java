package com.fh.kaernten.estimotelocate.alarm;


import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

public class AlarmBroadcastReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        intent.setClass(context, AlarmIntentService.class);
        startWakefulService(context, intent);
    }
}