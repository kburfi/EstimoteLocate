package com.fh.kaernten.estimotelocate.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.estimote.coresdk.recognition.packets.Beacon;
import com.fh.kaernten.estimotelocate.EstimoteApplication;
import com.fh.kaernten.estimotelocate.R;
import com.fh.kaernten.estimotelocate.objects.EstimoteBeacon;
import com.fh.kaernten.estimotelocate.objects.EstimoteBeaconDao;
import com.fh.kaernten.estimotelocate.objects.Room;
import com.fh.kaernten.estimotelocate.views.BeaconListItem;

import org.greenrobot.greendao.query.Query;

import java.util.ArrayList;
import java.util.List;


public class BeaconAdapter extends BaseArrayAdapter<Beacon> {

    private final static int LAYOUT_RES = R.layout.beacon_list_item;

    private Room room;
    private List<EstimoteBeacon> estimoteBeacons;
    private Query<EstimoteBeacon> beaconQuery;

    /**
     * @param context    activity context
     * @param beaconList always empty on first call
     * @param room       optional filter for room
     */
    public BeaconAdapter(Context context, List<Beacon> beaconList, Room room) {
        super(context, LAYOUT_RES, beaconList);
        this.room = room;
        initializeBeaconQuery();
        loadBeacons();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = new BeaconListItem(getContext());
        }
        if (items != null && !items.isEmpty() && items.size() > position) {
            Beacon beacon = items.get(position);
            if (beacon != null) {
                ((BeaconListItem) convertView).setBeacon(beacon, getBeaconIfFoundInAdapter(beacon));
            }
        }
        return convertView;
    }

    @Override
    protected int getLayout() {
        return LAYOUT_RES;
    }

    @Override
    public void setItems(List<Beacon> discoveredBeacons) {
        if (room != null) {
            filterForRoom(discoveredBeacons);
        } else {
            super.setItems(discoveredBeacons);
        }
    }

    public void loadBeacons() {
        estimoteBeacons = beaconQuery.list();
    }

    private void initializeBeaconQuery() {
        EstimoteBeaconDao estimoteBeaconDao = ((EstimoteApplication) getContext().getApplicationContext())
                .getDaoSession().getEstimoteBeaconDao();
        beaconQuery = estimoteBeaconDao.queryBuilder().build();
    }

    public EstimoteBeacon getBeaconIfFoundInAdapter(Beacon beacon) {
        if (estimoteBeacons != null) {
            for (EstimoteBeacon estimoteBeacon : estimoteBeacons) {
                if (estimoteBeacon.matches(beacon)) return estimoteBeacon;
            }
        }
        return null;
    }

    private void filterForRoom(List<Beacon> discoveredBeacons) {
        List<Beacon> filteredBeacons = new ArrayList<>();
        for (Beacon b : discoveredBeacons) {
            EstimoteBeacon estimoteBeacon = getBeaconIfFoundInAdapter(b);
            if (!room.hasRelationTo(estimoteBeacon)) {
                filteredBeacons.add(b);
            }
        }
        super.setItems(filteredBeacons);
    }
}