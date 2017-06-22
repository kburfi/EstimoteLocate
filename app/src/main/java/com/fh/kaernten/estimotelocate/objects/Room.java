package com.fh.kaernten.estimotelocate.objects;

import android.content.Context;

import com.estimote.coresdk.observation.region.RegionUtils;
import com.estimote.coresdk.recognition.packets.Beacon;
import com.fh.kaernten.estimotelocate.helper.NumberHelper;
import com.fh.kaernten.estimotelocate.interfaces.Room2Beacon;

import org.apache.commons.math3.linear.RealVector;
import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.ArrayList;
import java.util.List;

/**
 * Rooms are defined as rectangles which may contain n beacons (Room2EstimoteBeacon)
 */
@Entity
public class Room {

    @Id
    private Long id;

    private String name;

    private Double width;

    private Double height;

    private boolean usedForAutomatedTesting;

    @ToMany(referencedJoinProperty = "roomId")
    private List<Room2EstimoteBeacon> beaconsInThisRoom;

    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /**
     * Used for active entity operations.
     */
    @Generated(hash = 740313876)
    private transient RoomDao myDao;

    @Generated(hash = 1278782255)
    public Room(Long id, String name, Double width, Double height, boolean usedForAutomatedTesting) {
        this.id = id;
        this.name = name;
        this.width = width;
        this.height = height;
        this.usedForAutomatedTesting = usedForAutomatedTesting;
    }

    @Generated(hash = 703125385)
    public Room() {
    }

    public Double getWidth() {
        return width;
    }

    public Double getHeight() {
        return height;
    }

    public String getName() {
        return this.name;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setWidth(Double width) {
        this.width = width;
    }

    public void setHeight(Double height) {
        this.height = height;
    }


    public String getTextRepresentation(Context context) {
        return getName().trim()
                + " (" + NumberHelper.formatDecimal(context, getWidth())
                + "m * " + NumberHelper.formatDecimal(context, getHeight()) + "m)";
    }

    public boolean hasRelationTo(EstimoteBeacon estimoteBeacon) {
        if (estimoteBeacon != null) {
            List<Room2EstimoteBeacon> beaconsInThisRoom = getBeaconsInThisRoom();
            if (!beaconsInThisRoom.isEmpty()) {
                for (Room2EstimoteBeacon relation : beaconsInThisRoom) {
                    if (relation.getKnownBeaconId().equals(estimoteBeacon.getId())) return true;
                }
            }
        }
        return false;
    }

    public void refreshSelfAndAllReferencedBeacons() {
        List<Room2EstimoteBeacon> beacons = getBeaconsInThisRoom();
        for (Room2EstimoteBeacon b : beacons) {
            b.refresh();
        }
        this.refresh();
    }

    public void updateSelfAndAllReferencedBeacons(Context context) {
        List<Room2EstimoteBeacon> beacons = getBeaconsInThisRoom();
        for (Room2EstimoteBeacon b : beacons) {
            b.updateSelf(context);
        }
        this.update();
    }

    public void deleteReferencedBeaconsAndSelf(Context context) {
        List<Room2EstimoteBeacon> referencedBeacons = getBeaconsInThisRoom();
        if (referencedBeacons != null && !referencedBeacons.isEmpty()) {
            for (Room2EstimoteBeacon r : referencedBeacons) {
                r.deleteSelf(context);
            }
            referencedBeacons.clear();
        }
        delete();
    }

    public void createNewRelationToEstimoteBeacon(Context context, EstimoteBeacon estimoteBeacon, RealVector knownPosition) {
        Room2EstimoteBeacon newRelation = new Room2EstimoteBeacon(context, this, estimoteBeacon.getId(), knownPosition);
        final DaoSession daoSession = this.daoSession;
        if (daoSession == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        List<Room2EstimoteBeacon> list = getBeaconsInThisRoom();
        Room2EstimoteBeaconDao targetDao = daoSession.getRoom2EstimoteBeaconDao();
        targetDao.insert(newRelation);
        list.add(newRelation);
    }

    public boolean getUsedForAutomatedTesting() {
        return this.usedForAutomatedTesting;
    }

    public void setUsedForAutomatedTesting(boolean usedForAutomatedTesting) {
        this.usedForAutomatedTesting = usedForAutomatedTesting;
    }

    public List<Room2Beacon> filterForBeaconsInThisRoom(List<Beacon> discoveredBeacons) {
        List<Room2Beacon> room2Beacons = new ArrayList<>();
        if (discoveredBeacons != null && !discoveredBeacons.isEmpty()
                && !getBeaconsInThisRoom().isEmpty()) {
            for (Room2EstimoteBeacon relation : getBeaconsInThisRoom()) {
                for (Beacon b : discoveredBeacons) {
                    if (relation.getEstimoteBeacon().matches(b)) {

                        relation.setAccuracy(RegionUtils.computeAccuracy(b));
                        room2Beacons.add(relation);
                    }
                }
            }
        }
        return room2Beacons;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 335509890)
    public List<Room2EstimoteBeacon> getBeaconsInThisRoom() {
        if (beaconsInThisRoom == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            Room2EstimoteBeaconDao targetDao = daoSession.getRoom2EstimoteBeaconDao();
            List<Room2EstimoteBeacon> beaconsInThisRoomNew = targetDao._queryRoom_BeaconsInThisRoom(id);
            synchronized (this) {
                if (beaconsInThisRoom == null) {
                    beaconsInThisRoom = beaconsInThisRoomNew;
                }
            }
        }
        return beaconsInThisRoom;
    }

    /**
     * Resets a to-many relationship, making the next get call to query for a fresh result.
     */
    @Generated(hash = 1936183443)
    public synchronized void resetBeaconsInThisRoom() {
        beaconsInThisRoom = null;
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1185512297)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getRoomDao() : null;
    }
}