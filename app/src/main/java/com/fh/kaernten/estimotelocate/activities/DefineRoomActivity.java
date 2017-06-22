package com.fh.kaernten.estimotelocate.activities;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.fh.kaernten.estimotelocate.R;
import com.fh.kaernten.estimotelocate.helper.IntentHelper;
import com.fh.kaernten.estimotelocate.objects.Room;
import com.fh.kaernten.estimotelocate.objects.RoomDao;
import com.fh.kaernten.estimotelocate.views.FloatingPointSeekBar;


public class DefineRoomActivity extends BaseActivity {

    private AppCompatEditText roomName;
    private FloatingPointSeekBar heightSeekBar;
    private FloatingPointSeekBar widthSeekBar;

    private RoomDao roomDao;
    private Room room;
    private boolean isUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.define_room_activity);
        roomDao = getEstimoteApplication().getDaoSession().getRoomDao();
        Bundle b = getIntent().getExtras();
        if (b != null && b.containsKey(IntentHelper.ROOM_EXTRA)) {
            Long id = b.getLong(IntentHelper.ROOM_EXTRA);
            room = roomDao.load(id);
            isUpdate = true;
        } else {
            room = new Room();
            isUpdate = false;
        }
        initializeRoom();
    }

    @Override
    public int defineTitle() {
        return R.string.edit_room;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.save) {
            if (inputIsValid()) {
                saveValuesToRoom();
                DefineRoomActivity.this.finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder b = new AlertDialog.Builder(DefineRoomActivity.this);
        b.setTitle(R.string.discard)
                .setMessage(R.string.discard_warning)
                .setPositiveButton(R.string.discard, (dialogInterface, i) -> {
                    super.onBackPressed();
                })
                .setNegativeButton(R.string.abort, (dialogInterface, i) -> {
                });
        b.create().show();
    }

    private void initializeRoom() {
        roomName = (AppCompatEditText) findViewById(R.id.roomName);
        heightSeekBar = (FloatingPointSeekBar) findViewById(R.id.heightSeekBar);
        widthSeekBar = (FloatingPointSeekBar) findViewById(R.id.widthSeekBar);
        if (isUpdate) {
            roomName.setText(room.getName());
            heightSeekBar.setSelectedValue(room.getHeight());
            widthSeekBar.setSelectedValue(room.getWidth());
        }
    }

    private boolean inputIsValid() {
        if (roomName.getText() == null || roomName.getText().length() == 0) {
            Toast.makeText(DefineRoomActivity.this, R.string.room_name_warning, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void saveValuesToRoom() {
        room.setName(roomName.getText().toString());
        room.setHeight(heightSeekBar.getSelectedValue());
        room.setWidth(widthSeekBar.getSelectedValue());
        if (isUpdate) {
            roomDao.update(room);
        } else {
            roomDao.insert(room);
        }
    }
}