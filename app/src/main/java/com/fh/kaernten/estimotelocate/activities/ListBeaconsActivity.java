package com.fh.kaernten.estimotelocate.activities;

import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.estimote.coresdk.common.requirements.SystemRequirementsChecker;
import com.estimote.coresdk.observation.region.beacon.BeaconRegion;
import com.estimote.coresdk.recognition.packets.Beacon;
import com.estimote.coresdk.service.BeaconManager;
import com.fh.kaernten.estimotelocate.R;
import com.fh.kaernten.estimotelocate.adapter.BeaconAdapter;
import com.fh.kaernten.estimotelocate.helper.IntentHelper;
import com.fh.kaernten.estimotelocate.helper.EstimoteBeaconColorHelper;
import com.fh.kaernten.estimotelocate.interfaces.BeaconColorCallback;
import com.fh.kaernten.estimotelocate.objects.EstimoteBeacon;
import com.fh.kaernten.estimotelocate.objects.Room;
import com.fh.kaernten.estimotelocate.objects.RoomDao;

import java.util.ArrayList;
import java.util.List;


public class ListBeaconsActivity extends BaseActivity implements BeaconManager.BeaconRangingListener,
        AdapterView.OnItemClickListener, BeaconColorCallback,
        AdapterView.OnItemLongClickListener {

    private Room room;

    private ListView listView;
    private AppCompatTextView noDataTextView;
    private BeaconAdapter beaconAdapter;

    private boolean finishAfterColorWasChosen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getIntent().getExtras();

        RoomDao roomDao = getEstimoteApplication().getDaoSession().getRoomDao();
        if (b != null && b.containsKey(IntentHelper.ROOM_EXTRA)) {
            Long id = b.getLong(IntentHelper.ROOM_EXTRA);
            room = roomDao.load(id);
        }
        setContentView(R.layout.list_layout);
        initializeView();
        finishAfterColorWasChosen = false;
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
    public int defineTitle() {
        return R.string.list_beacons;
    }

    @Override
    public void onBeaconsDiscovered(BeaconRegion beaconRegion, List<Beacon> list) {
        if (!list.isEmpty()) {
            beaconAdapter.setItems(list);
        }
        setVisibility(!beaconAdapter.isEmpty()); // adapter may filter beacons for room!
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        Beacon selectedBeacon = beaconAdapter.getItem(position);
        EstimoteBeacon estimoteBeacon = beaconAdapter.getBeaconIfFoundInAdapter(selectedBeacon);
        if (estimoteBeacon == null) {
            finishAfterColorWasChosen = true;
            showColorPicker(selectedBeacon, null);
        } else {
            createNewRelationToRoomAndFinish(estimoteBeacon);
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
        finishAfterColorWasChosen = false;
        Beacon selectedBeacon = beaconAdapter.getItem(position);
        EstimoteBeacon estimoteBeacon = beaconAdapter.getBeaconIfFoundInAdapter(selectedBeacon);
        showColorPicker(selectedBeacon, estimoteBeacon);
        return true;
    }

    @Override
    public void onBeaconColorUpdated(EstimoteBeacon estimoteBeacon) {
        if (finishAfterColorWasChosen) {
            createNewRelationToRoomAndFinish(estimoteBeacon);
        } else {
            beaconAdapter.loadBeacons();
            beaconAdapter.notifyDataSetInvalidated();
        }
    }

    private void initializeView() {
        listView = (ListView) findViewById(R.id.list);
        noDataTextView = (AppCompatTextView) findViewById(R.id.listOutputTV);
        beaconAdapter = new BeaconAdapter(this, new ArrayList<>(), room);
        setVisibility(false);
        listView.setAdapter(beaconAdapter);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
    }

    private void setVisibility(boolean listVisible) {
        listView.setVisibility(listVisible ? View.VISIBLE : View.GONE);
        noDataTextView.setVisibility(listVisible ? View.GONE : View.VISIBLE);
    }

    private void showColorPicker(Beacon selectedBeacon, EstimoteBeacon estimoteBeacon) {
        new EstimoteBeaconColorHelper(selectedBeacon, estimoteBeacon, ListBeaconsActivity.this)
                .showColorPicker();
    }

    private void createNewRelationToRoomAndFinish(EstimoteBeacon estimoteBeacon) {
        room.createNewRelationToEstimoteBeacon(this, estimoteBeacon, null);
        setResult(RESULT_OK);
        finish();
    }
}