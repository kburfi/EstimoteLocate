package com.fh.kaernten.estimotelocate.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.fh.kaernten.estimotelocate.R;
import com.fh.kaernten.estimotelocate.helper.IntentHelper;
import com.fh.kaernten.estimotelocate.interfaces.MovableObject;
import com.fh.kaernten.estimotelocate.interfaces.ObjectMovedCallback;
import com.fh.kaernten.estimotelocate.objects.Room;
import com.fh.kaernten.estimotelocate.objects.Room2EstimoteBeacon;
import com.fh.kaernten.estimotelocate.objects.ThreeDimensionalVector;
import com.fh.kaernten.estimotelocate.views.BeaconListItem;
import com.fh.kaernten.estimotelocate.views.BeaconLocationView;
import com.fh.kaernten.estimotelocate.views.FloatingPointSeekBar;


public class MapRoomToBeaconActivity extends BaseActivity implements ObjectMovedCallback, FloatingPointSeekBar.ProgressChangedListener, ActionMode.Callback {

    private Room room;
    private Room2EstimoteBeacon currentlySelectedBeacon;
    private ActionMode activeActionMode;

    private BeaconLocationView locationView;
    private LinearLayout manualInputLayout;
    private FloatingPointSeekBar width;
    private FloatingPointSeekBar height;
    private FloatingPointSeekBar depth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getIntent().getExtras();
        if (b != null) {
            if (b.containsKey(IntentHelper.ROOM_EXTRA)) {
                Long id = b.getLong(IntentHelper.ROOM_EXTRA);
                room = getEstimoteApplication().getDaoSession().getRoomDao()
                        .load(id);
            }
        }
        initializeView();
    }

    @Override
    public int defineTitle() {
        return R.string.assign_beacons;
    }

    @Override
    public void onBackPressed() {
        room.refreshSelfAndAllReferencedBeacons();
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.map_beacon_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add) {
            IntentHelper.startActivityForResult(MapRoomToBeaconActivity.this,
                    ListBeaconsActivity.class, room);
        } else if (item.getItemId() == R.id.save) {
            room.updateSelfAndAllReferencedBeacons(this);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IntentHelper.REQUEST_CODE_BEACON && resultCode == Activity.RESULT_OK) {
            updateViewForMovableObject(null);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onObjectMoved(MovableObject movedObject) {
        updateViewForMovableObject(movedObject);
    }

    @Override
    public void onProgressChanged(FloatingPointSeekBar seekBar, double actualValue, boolean fromUser) {
        if (seekBar != null && currentlySelectedBeacon != null && width != null && height != null) {
            ThreeDimensionalVector v = currentlySelectedBeacon.getLocationInRoom();
            if (seekBar.getId() == width.getId()) {
                v.setxCoordinate(actualValue);
            } else if (seekBar.getId() == height.getId()) {
                v.setyCoordinate(actualValue);
            } else {
                v.setzCoordinate(actualValue);
            }
            locationView.defineMovableObjects(room.getBeaconsInThisRoom());
            locationView.invalidate();
        }
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        activeActionMode = mode;
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.delete_menu, menu);
        setStatusBarColor(R.color.darkGrey);
        BeaconListItem b = new BeaconListItem(MapRoomToBeaconActivity.this);
        mode.setCustomView(b);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        View v = mode.getCustomView();
        if (v != null && v instanceof BeaconListItem && currentlySelectedBeacon != null) {
            ((BeaconListItem) v).setBeaconHeadline(currentlySelectedBeacon.getEstimoteBeacon());
            return true;
        }
        return false; // Return false if nothing is done
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                deleteCurrentRelation();
                mode.finish(); // Action picked, so close the CAB
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        setStatusBarColor(R.color.colorPrimaryDark);
        activeActionMode = null;
        updateViewForMovableObject(null);
    }

    private void updateViewForMovableObject(MovableObject movableObject) {
        locationView.setBeacons(room.getBeaconsInThisRoom());
        locationView.defineMovableObjects(room.getBeaconsInThisRoom());
        if (movableObject != null) {
            updateActionMode(movableObject);
            setProgressForBeacon();
            manualInputLayout.setVisibility(View.VISIBLE);
        } else {
            currentlySelectedBeacon = null;
            if (activeActionMode != null) {
                activeActionMode.finish();
            }
            manualInputLayout.setVisibility(View.INVISIBLE);
        }
    }

    private void initializeView() {
        setContentView(R.layout.map_room_to_beacon);
        locationView = (BeaconLocationView) findViewById(R.id.locationView);
        manualInputLayout = (LinearLayout) findViewById(R.id.manualInputLayout);
        locationView.setRoom(room);
        width = (FloatingPointSeekBar) findViewById(R.id.widthSeekBar);
        height = (FloatingPointSeekBar) findViewById(R.id.heightSeekBar);
        depth = (FloatingPointSeekBar) findViewById(R.id.depthSeekBar);
        initializeRoomValues();
        updateViewForMovableObject(null);
    }

    private void initializeRoomValues() {
        width.setMax(room.getWidth());
        height.setMax(room.getHeight());
        depth.setMax(6f);
    }

    private void updateActionMode(MovableObject movableObject) {
        boolean beaconHasChanged = !movableObject.equals(currentlySelectedBeacon);
        currentlySelectedBeacon = (Room2EstimoteBeacon) movableObject;
        if (activeActionMode != null && beaconHasChanged) {
            activeActionMode.invalidate();
        } else if (activeActionMode == null) {
            startSupportActionMode(this);
        }
    }

    private void setProgressForBeacon() {
        ThreeDimensionalVector v = currentlySelectedBeacon.getLocationInRoom();
        width.setSelectedValue(v.getxCoordinate());
        height.setSelectedValue(v.getyCoordinate());
        depth.setSelectedValue(v.getzCoordinate());
    }

    private void deleteCurrentRelation() {
        room.getBeaconsInThisRoom().remove(currentlySelectedBeacon);
        currentlySelectedBeacon.deleteSelf(this);
        updateViewForMovableObject(null);
    }
}