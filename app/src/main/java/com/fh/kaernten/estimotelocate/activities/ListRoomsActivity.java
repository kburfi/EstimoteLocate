package com.fh.kaernten.estimotelocate.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.AppCompatTextView;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.fh.kaernten.estimotelocate.R;
import com.fh.kaernten.estimotelocate.adapter.RoomAdapter;
import com.fh.kaernten.estimotelocate.helper.IntentHelper;
import com.fh.kaernten.estimotelocate.objects.Room;
import com.fh.kaernten.estimotelocate.objects.RoomDao;

import org.greenrobot.greendao.query.Query;

import java.util.ArrayList;
import java.util.List;


public class ListRoomsActivity extends BaseActivity implements AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener, AbsListView.MultiChoiceModeListener {

    private Query<Room> roomQuery;

    private ListView listView;
    private AppCompatTextView noDataTextView;
    private RoomAdapter roomAdapter;
    private int selectedItemCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_rooms_activity);
        ActionBar a = getSupportActionBar();
        if (a != null) {
            a.setDisplayHomeAsUpEnabled(false);
        }
        initializeView();
        roomQuery = getEstimoteApplication().getDaoSession().getRoomDao()
                .queryBuilder().orderAsc(RoomDao.Properties.Name).build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRooms();
    }

    @Override
    public int defineTitle() {
        return -1;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_rooms_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.testOptions) {
            IntentHelper.startActivity(this, TestActivity.class);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        Room chosenRoom = roomAdapter.getItem(position);
        IntentHelper.startActivity(ListRoomsActivity.this,
                IndoorLocationActivity.class, chosenRoom);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
        listView.setItemChecked(position, !roomAdapter.positionIsChecked(position));
        return true;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        selectedItemCount = 1;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.room_contextual_menu, menu);
        setStatusBarColor(R.color.darkGrey);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        menu.findItem(R.id.editRoom).setVisible(selectedItemCount == 1);
        menu.findItem(R.id.assignBeacons).setVisible(selectedItemCount == 1);
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        roomAdapter.clearSelection();
        setStatusBarColor(R.color.colorPrimaryDark);
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.editRoom:
                IntentHelper.startActivity(ListRoomsActivity.this,
                        DefineRoomActivity.class, roomAdapter.getSelectedItems().get(0));
                break;

            case R.id.deleteRooms:
                deleteSelectedRooms();
                loadRooms();
                break;

            case R.id.assignBeacons:
                IntentHelper.startActivity(ListRoomsActivity.this,
                        MapRoomToBeaconActivity.class, roomAdapter.getSelectedItems().get(0));
                break;
        }
        mode.finish();
        return false;
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
        if (checked) {
            roomAdapter.setSelected(position);
        } else {
            roomAdapter.removeSelection(position);
        }
        mode.setTitle(roomAdapter.getSizeOfSelectedItems() + " " + getString(R.string.selected));
        updateContextMenuIfCountChanged(mode);
    }

    private void updateContextMenuIfCountChanged(ActionMode mode) {
        int newSize = roomAdapter.getSizeOfSelectedItems();
        if (selectedItemCount != newSize) {
            selectedItemCount = newSize;
            mode.invalidate();
        }
    }

    private void deleteSelectedRooms() {
        List<Room> selectedRooms = roomAdapter.getSelectedItems();
        for (Room r : selectedRooms) {
            r.deleteReferencedBeaconsAndSelf(this);
        }
    }

    private void initializeView() {
        listView = (ListView) findViewById(R.id.list);
        noDataTextView = (AppCompatTextView) findViewById(R.id.listOutputTV);
        FloatingActionButton b = (FloatingActionButton) findViewById(R.id.add_button);
        b.setOnClickListener(v -> IntentHelper.startActivity(ListRoomsActivity.this,
                DefineRoomActivity.class, null));
        roomAdapter = new RoomAdapter(this, new ArrayList<>());
        listView.setAdapter(roomAdapter);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(this);
    }

    private void loadRooms() {
        List<Room> allRooms = roomQuery.list();
        if (allRooms.isEmpty()) {
            setVisibility(false);
        } else {
            roomAdapter.setItems(allRooms);
            setVisibility(true);
        }
    }

    private void setVisibility(boolean listVisible) {
        listView.setVisibility(listVisible ? View.VISIBLE : View.GONE);
        noDataTextView.setVisibility(listVisible ? View.GONE : View.VISIBLE);
    }
}