package com.fh.kaernten.estimotelocate.helper;


import android.content.Context;

import com.estimote.coresdk.recognition.packets.Beacon;
import com.fh.kaernten.estimotelocate.EstimoteApplication;
import com.fh.kaernten.estimotelocate.R;
import com.fh.kaernten.estimotelocate.interfaces.BeaconColorCallback;
import com.fh.kaernten.estimotelocate.objects.EstimoteBeacon;
import com.fh.kaernten.estimotelocate.objects.EstimoteBeaconDao;

import petrov.kristiyan.colorpicker.ColorPicker;

public class EstimoteBeaconColorHelper implements ColorPicker.OnFastChooseColorListener {

    private Beacon beacon;
    private EstimoteBeacon estimoteBeacon;

    private Context context;
    private EstimoteBeaconDao estimoteBeaconDao;

    public EstimoteBeaconColorHelper(Beacon beacon, EstimoteBeacon estimoteBeacon, Context context) {
        this.beacon = beacon;
        this.estimoteBeacon = estimoteBeacon;
        this.context = context;
        estimoteBeaconDao = ((EstimoteApplication) context.getApplicationContext())
                .getDaoSession().getEstimoteBeaconDao();
    }

    public void showColorPicker() {
        ColorPicker colorPicker = new ColorPicker(context);
        colorPicker.setTitle(context.getString(R.string.select_beacon_color) + " with major " + beacon.getMajor());
        colorPicker.setColors(context.getResources().getIntArray(R.array.estimote_colors));
        colorPicker.setOnFastChooseColorListener(this);
        colorPicker.setRoundColorButton(true);
        colorPicker.show();
    }

    @Override
    public void setOnFastChooseColorListener(int position, int color) {
        updateOrInsertBeacon(color);
        if (context instanceof BeaconColorCallback) {
            ((BeaconColorCallback) context).onBeaconColorUpdated(estimoteBeacon);
        }
    }

    @Override
    public void onCancel() {
        // irrelevant use case
    }

    private void updateOrInsertBeacon(int color) {
        if (estimoteBeacon != null) {
            estimoteBeacon.setBeaconColor(color);
            estimoteBeaconDao.update(estimoteBeacon);
        } else {
            estimoteBeacon = new EstimoteBeacon(beacon, color);
            estimoteBeaconDao.insert(estimoteBeacon);
        }
    }
}
