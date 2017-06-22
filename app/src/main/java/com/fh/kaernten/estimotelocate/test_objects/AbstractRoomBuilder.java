package com.fh.kaernten.estimotelocate.test_objects;

import android.content.Context;

import com.fh.kaernten.estimotelocate.EstimoteApplication;
import com.fh.kaernten.estimotelocate.objects.DaoSession;
import com.fh.kaernten.estimotelocate.objects.EstimoteBeacon;
import com.fh.kaernten.estimotelocate.objects.EstimoteBeaconDao;
import com.fh.kaernten.estimotelocate.objects.Room;
import com.fh.kaernten.estimotelocate.objects.RoomDao;

/**
 * Loads test room with predefined size and known beacons
 * Created by Kristian on 22.06.2017.
 */
public abstract class AbstractRoomBuilder {

    private Context context;
    private RoomDao roomDao;
    private EstimoteBeaconDao estimoteBeaconDao;

    private int numberOfBeacons;
    private Room testRoom;

    public AbstractRoomBuilder(Context context) {
        this.context = context;
        DaoSession d = ((EstimoteApplication) context.getApplicationContext()).getDaoSession();
        roomDao = d.getRoomDao();
        estimoteBeaconDao = d.getEstimoteBeaconDao();
    }

    /**
     * @param numberOfBeacons defines numberOfBeacons of test
     * @return testing room
     */
    public Room getRoomForTesting(int numberOfBeacons) {
        this.numberOfBeacons = numberOfBeacons;
        testRoom = roomDao.queryBuilder().where(RoomDao.Properties.UsedForAutomatedTesting.eq(true))
                .build().unique();
        if (testRoom != null) {
            // old testing room is always deleted because it could have been changed
            testRoom.deleteReferencedBeaconsAndSelf(context);
        }
        buildNewTestRoom();
        return testRoom;
    }

    protected abstract double[] defineRoomDimension();

    protected abstract TestBeacon[] defineBeacons();

    private void buildNewTestRoom() {
        testRoom = new Room();
        testRoom.setName("Test room");
        double[] room = defineRoomDimension();
        testRoom.setWidth(room[0]);
        testRoom.setHeight(room[1]);
        testRoom.setUsedForAutomatedTesting(true);
        roomDao.insert(testRoom);
        addKnownBeacons();
    }

    private void addKnownBeacons() {
        TestBeacon[] beacons = defineBeacons();
        for (int i = 0; i < beacons.length; i++) {
            if (i < 3 || numberOfBeacons >= 4) {
                EstimoteBeacon b = initializeBeacon(beacons[i].getBeacon(context));
                testRoom.createNewRelationToEstimoteBeacon(context, b, beacons[i].getLocation());
            }
        }
    }

    /**
     * Beacons may have already been added - this makes sure no duplicate is inserted
     *
     * @param beaconToFind beacon to search for in database
     * @return known beacon from database
     */
    private EstimoteBeacon initializeBeacon(EstimoteBeacon beaconToFind) {
        EstimoteBeacon b = estimoteBeaconDao.queryBuilder()
                .where(EstimoteBeaconDao.Properties.Major.eq(beaconToFind.getMajor()),
                        EstimoteBeaconDao.Properties.Minor.eq(beaconToFind.getMinor()))
                .build().unique();
        if (b != null) {
            return b;
        } else {
            estimoteBeaconDao.insert(beaconToFind);
            return beaconToFind;
        }
    }
}