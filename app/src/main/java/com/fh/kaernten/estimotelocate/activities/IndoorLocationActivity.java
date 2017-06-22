package com.fh.kaernten.estimotelocate.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.estimote.coresdk.common.requirements.SystemRequirementsChecker;
import com.estimote.coresdk.observation.region.beacon.BeaconRegion;
import com.estimote.coresdk.recognition.packets.Beacon;
import com.estimote.coresdk.service.BeaconManager;
import com.fh.kaernten.estimotelocate.R;
import com.fh.kaernten.estimotelocate.helper.IntentHelper;
import com.fh.kaernten.estimotelocate.objects.Room;
import com.fh.kaernten.estimotelocate.objects.RoomDao;
import com.fh.kaernten.estimotelocate.views.BeaconLocationView;

import java.util.List;

public class IndoorLocationActivity extends BaseActivity implements BeaconManager.BeaconRangingListener {

    private final static String RADIUS_KEY = "radius";

    private Room chosenRoom;
    private boolean debugVisible = false;

    private BeaconLocationView beaconLocationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RoomDao roomDao = getEstimoteApplication().getDaoSession().getRoomDao();
        Bundle b = getIntent().getExtras();
        if (b != null && b.containsKey(IntentHelper.ROOM_EXTRA)) {
            Long id = b.getLong(IntentHelper.ROOM_EXTRA);
            chosenRoom = roomDao.load(id);
            ActionBar a = getSupportActionBar();
            if (a != null) {
                a.setTitle(chosenRoom.getName());
            }
        }
        initializeView();
        getEstimoteApplication().getBeaconManager().setRangingListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SystemRequirementsChecker.checkWithDefaultDialogs(this);
        getEstimoteApplication().startRanging();
    }

    @Override
    protected void onPause() {
        getEstimoteApplication().stopRanging();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconLocationView.recycle();
    }

    @Override
    public int defineTitle() {
        return R.string.localization;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(RADIUS_KEY, debugVisible);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        debugVisible = savedInstanceState.getBoolean(RADIUS_KEY, false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.location_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.redrawRoom) {
            initializeView();
        } else if (item.getItemId() == R.id.drawDebugInfo) {
            beaconLocationView.invertVisibilityOfDebugInfo();
            debugVisible = !debugVisible;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBeaconsDiscovered(BeaconRegion beaconRegion, List<Beacon> list) {
        if (beaconLocationView != null && !list.isEmpty()) {
            beaconLocationView.setBeacons(chosenRoom.filterForBeaconsInThisRoom(list));
        }
    }

    private void initializeView() {
        if (beaconLocationView != null) beaconLocationView.recycle();
        beaconLocationView = new BeaconLocationView(this);
        beaconLocationView.setRoom(chosenRoom);
        setContentView(beaconLocationView);
    }
}