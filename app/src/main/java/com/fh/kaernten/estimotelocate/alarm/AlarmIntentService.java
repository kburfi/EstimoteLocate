package com.fh.kaernten.estimotelocate.alarm;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.fh.kaernten.estimotelocate.EstimoteApplication;


public class AlarmIntentService extends IntentService {

    public AlarmIntentService() {
        this("AlarmIntentService");
    }

    public AlarmIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            EstimoteApplication e = (EstimoteApplication) getApplicationContext();
            if (e.getRunningTest() != null) {
                e.getRunningTest().testAndSaveCurrentValue(intent, getApplicationContext());
            }
        }
    }
}