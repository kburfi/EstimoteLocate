package com.fh.kaernten.estimotelocate;

import android.app.Application;

import com.estimote.coresdk.observation.region.beacon.BeaconRegion;
import com.estimote.coresdk.service.BeaconManager;
import com.fh.kaernten.estimotelocate.activities.BaseActivity;
import com.fh.kaernten.estimotelocate.interfaces.MobileTest;
import com.fh.kaernten.estimotelocate.objects.DaoMaster;
import com.fh.kaernten.estimotelocate.objects.DaoSession;

import org.greenrobot.greendao.database.Database;

import java.util.UUID;

public class EstimoteApplication extends Application {

    private static final UUID ESTIMOTE_PROXIMITY_UUID = UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D");
    private static final BeaconRegion ALL_ESTIMOTE_BEACONS_REGION = new BeaconRegion("rid", ESTIMOTE_PROXIMITY_UUID, null, null);

    private BaseActivity mCurrentActivity = null;
    private DaoSession daoSession;
    private BeaconManager beaconManager;

    private MobileTest runningTest;

    @Override
    public void onCreate() {
        super.onCreate();
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "estimote_db");
        Database db = helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
        beaconManager = new BeaconManager(getApplicationContext());
    }

    public BaseActivity getCurrentActivity(){
        return mCurrentActivity;
    }

    public void setCurrentActivity(BaseActivity mCurrentActivity){
        this.mCurrentActivity = mCurrentActivity;
    }

    public BeaconManager getBeaconManager() {
        return beaconManager;
    }

    public void startRanging() {
        beaconManager.connect(() -> beaconManager.startRanging(ALL_ESTIMOTE_BEACONS_REGION));
    }

    public void stopRanging() {
        beaconManager.stopRanging(ALL_ESTIMOTE_BEACONS_REGION);
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    public MobileTest getRunningTest() {
        return runningTest;
    }

    public void setRunningTest(MobileTest runningTest) {
        this.runningTest = runningTest;
    }
}