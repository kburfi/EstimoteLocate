package com.fh.kaernten.estimotelocate.objects;

import android.content.Context;

import com.fh.kaernten.estimotelocate.interfaces.Room2Beacon;
import com.fh.kaernten.estimotelocate.interfaces.MovableObject;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.annotation.Transient;

@Entity
public class Room2EstimoteBeacon implements Room2Beacon, MovableObject {

    @Id
    private Long id;

    private Long roomId;

    private Long knownBeaconId;

    @ToOne(joinProperty = "knownBeaconId")
    private EstimoteBeacon estimoteBeacon;

    private Long coordinateId;
    @ToOne(joinProperty = "coordinateId")
    private ThreeDimensionalVector locationInRoom;

    /**
     * accuracy is calculated during runtime and changes every other second
     */
    @Transient
    private Double accuracy;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 1978032003)
    private transient Room2EstimoteBeaconDao myDao;

    @Generated(hash = 1089308401)
    private transient Long estimoteBeacon__resolvedKey;

    @Generated(hash = 1664081734)
    private transient Long locationInRoom__resolvedKey;


    public Room2EstimoteBeacon(Context context, Room room, Long knownBeaconId, RealVector position) {
        this.roomId = room.getId();
        this.knownBeaconId = knownBeaconId;
        if (position == null) {
            position = new ArrayRealVector(3);
            position.setEntry(0, room.getWidth() / 2);
            position.setEntry(1, room.getHeight() / 2);
            position.setEntry(2, 1f);
        }
        ThreeDimensionalVector v = new ThreeDimensionalVector(context, position);
        this.coordinateId = v.getId();
    }

    @Generated(hash = 1513209782)
    public Room2EstimoteBeacon(Long id, Long roomId, Long knownBeaconId, Long coordinateId) {
        this.id = id;
        this.roomId = roomId;
        this.knownBeaconId = knownBeaconId;
        this.coordinateId = coordinateId;
    }

    @Generated(hash = 2020766068)
    public Room2EstimoteBeacon() {
    }

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 961072195)
    public ThreeDimensionalVector getLocationInRoom() {
        Long __key = this.coordinateId;
        if (locationInRoom__resolvedKey == null || !locationInRoom__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ThreeDimensionalVectorDao targetDao = daoSession.getThreeDimensionalVectorDao();
            ThreeDimensionalVector locationInRoomNew = targetDao.load(__key);
            synchronized (this) {
                locationInRoom = locationInRoomNew;
                locationInRoom__resolvedKey = __key;
            }
        }
        return locationInRoom;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRoomId() {
        return this.roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public Long getKnownBeaconId() {
        return this.knownBeaconId;
    }

    public void setKnownBeaconId(Long knownBeaconId) {
        this.knownBeaconId = knownBeaconId;
    }

    @Override
    public int getColor() {
        return getEstimoteBeacon().getBeaconColor();
    }

    @Override
    public Double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    @Override
    public void setNewCoordinates(float x, float y) {
        ThreeDimensionalVector v = getLocationInRoom();
        v.setxCoordinate((double) x);
        v.setyCoordinate((double) y);
    }

    @Override
    public boolean wasTouched(float touchX, float touchY, float touchRadius) {
        return (getLocationInRoom().getxCoordinate() - touchX) * (getLocationInRoom().getxCoordinate() - touchX)
                + (getLocationInRoom().getyCoordinate() - touchY) * (getLocationInRoom().getyCoordinate() - touchY)
                <= touchRadius * touchRadius;
    }

    public Long getCoordinateId() {
        return this.coordinateId;
    }

    public void setCoordinateId(Long coordinateId) {
        this.coordinateId = coordinateId;
    }

    public void deleteSelf(Context context) {
        if (locationInRoom != null) locationInRoom.deleteSelf(context);
        delete();
    }

    public void updateSelf(Context context) {
        if (locationInRoom != null) locationInRoom.updateSelf(context);
        update();
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1644859945)
    public void setLocationInRoom(ThreeDimensionalVector locationInRoom) {
        synchronized (this) {
            this.locationInRoom = locationInRoom;
            coordinateId = locationInRoom == null ? null : locationInRoom.getId();
            locationInRoom__resolvedKey = coordinateId;
        }
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

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 1810316863)
    public EstimoteBeacon getEstimoteBeacon() {
        Long __key = this.knownBeaconId;
        if (estimoteBeacon__resolvedKey == null || !estimoteBeacon__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            EstimoteBeaconDao targetDao = daoSession.getEstimoteBeaconDao();
            EstimoteBeacon estimoteBeaconNew = targetDao.load(__key);
            synchronized (this) {
                estimoteBeacon = estimoteBeaconNew;
                estimoteBeacon__resolvedKey = __key;
            }
        }
        return estimoteBeacon;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 2065031134)
    public void setEstimoteBeacon(EstimoteBeacon estimoteBeacon) {
        synchronized (this) {
            this.estimoteBeacon = estimoteBeacon;
            knownBeaconId = estimoteBeacon == null ? null : estimoteBeacon.getId();
            estimoteBeacon__resolvedKey = knownBeaconId;
        }
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1216334712)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getRoom2EstimoteBeaconDao() : null;
    }
}