package com.fh.kaernten.estimotelocate.objects;

import com.estimote.coresdk.recognition.packets.Beacon;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class EstimoteBeacon {

    @Id
    private Long id;

    private int major;

    private int minor;

    private int beaconColor;

    public EstimoteBeacon(Beacon beacon, int color) {
        this.major = beacon.getMajor();
        this.minor = beacon.getMinor();
        this.beaconColor = color;
    }

    @Generated(hash = 703418787)
    public EstimoteBeacon(Long id, int major, int minor, int beaconColor) {
        this.id = id;
        this.major = major;
        this.minor = minor;
        this.beaconColor = beaconColor;
    }

    @Generated(hash = 2048632141)
    public EstimoteBeacon() {
    }

    public boolean matches(Beacon beacon) {
        return beacon != null
                && beacon.getMajor() == major
                && beacon.getMinor() == minor;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getMajor() {
        return this.major;
    }

    public void setMajor(int major) {
        this.major = major;
    }

    public int getMinor() {
        return this.minor;
    }

    public void setMinor(int minor) {
        this.minor = minor;
    }

    public int getBeaconColor() {
        return this.beaconColor;
    }

    public void setBeaconColor(int beaconColor) {
        this.beaconColor = beaconColor;
    }

}