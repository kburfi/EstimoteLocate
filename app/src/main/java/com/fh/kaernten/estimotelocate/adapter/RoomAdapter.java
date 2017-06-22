package com.fh.kaernten.estimotelocate.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.fh.kaernten.estimotelocate.R;
import com.fh.kaernten.estimotelocate.objects.Room;

import java.util.ArrayList;
import java.util.List;

public class RoomAdapter extends BaseArrayAdapter<Room> {

    private static final int LAYOUT_RES = android.R.layout.simple_list_item_2;

    private ArrayList<Integer> selectedEntries;

    public RoomAdapter(Context context, List<Room> items) {
        super(context, LAYOUT_RES, items);
        selectedEntries = new ArrayList<>();
    }

    @Override
    protected void fillItem(int position, Room object, View v) {
        if (positionIsChecked(position)) {
            v.setBackgroundColor(
                    ContextCompat.getColor(getContext(), android.R.color.holo_blue_light));
        } else {
            v.setBackgroundColor(
                    ContextCompat.getColor(getContext(), android.R.color.transparent));
        }
        TextView text = (TextView) v.findViewById(android.R.id.text1);
        text.setText(object.getTextRepresentation(getContext()));
        TextView text2 = (TextView) v.findViewById(android.R.id.text2);
        if (text2 != null) {
            text2.setText(object.getBeaconsInThisRoom().size()
                    + " " + getContext().getString(R.string.beacons));
        }
    }

    @Override
    public void setItems(List<Room> items) {
        super.setItems(items);
        selectedEntries.clear();
    }

    public void setSelected(int position) {
        selectedEntries.add(position);
        notifyDataSetChanged();
    }

    public boolean positionIsChecked(int position) {
        return selectedEntries.contains(position);
    }

    public List<Room> getSelectedItems() {
        List<Room> selectedRooms = new ArrayList<>();
        for (Integer selected : selectedEntries) {
            selectedRooms.add(getItem(selected));
        }
        return selectedRooms;
    }

    public int getSizeOfSelectedItems() {
        return selectedEntries.size();
    }

    public void removeSelection(Integer position) {
        selectedEntries.remove(position);
        notifyDataSetChanged();
    }

    public void clearSelection() {
        selectedEntries.clear();
        notifyDataSetChanged();
    }

    @Override
    protected int getLayout() {
        return LAYOUT_RES;
    }
}