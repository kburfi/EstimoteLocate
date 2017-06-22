package com.fh.kaernten.estimotelocate.helper;


import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.fh.kaernten.estimotelocate.activities.BaseActivity;
import com.fh.kaernten.estimotelocate.objects.EstimoteBeacon;
import com.fh.kaernten.estimotelocate.objects.Room;


public class IntentHelper {

    public static final String ROOM_EXTRA = "room";
    public static final String BEACON_EXTRA = "beacon";
    public static final int REQUEST_CODE_BEACON = 10;

    public static void startActivity(BaseActivity from, Class target) {
        startActivity(from, target, null, null);
    }

    public static void startActivity(BaseActivity from, Class target, Room room) {
        startActivity(from, target, room, null);
    }

    public static void startActivity(BaseActivity from, Class target, Room room, EstimoteBeacon estimoteBeacon) {
        Intent i = buildIntent(from, target, room, estimoteBeacon);
        from.startActivity(i);
    }

    public static void startActivityForResult(BaseActivity from, Class target, Room room) {
        Intent i = buildIntent(from, target, room, null);
        from.startActivityForResult(i, REQUEST_CODE_BEACON);
    }

    @NonNull
    public static Intent buildIntent(Context from, Class target) {
        return buildIntent(from, target, null, null);
    }

    @NonNull
    public static Intent buildIntent(Context from, Class target, Room room, EstimoteBeacon estimoteBeacon) {
        Intent i = new Intent(from, target);
        if (room != null) {
            i.putExtra(ROOM_EXTRA, room.getId());
        }
        if (estimoteBeacon != null) {
            i.putExtra(BEACON_EXTRA, estimoteBeacon.getId());
        }
        return i;
    }
}