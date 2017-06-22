package com.fh.kaernten.estimotelocate.objects;

import android.content.Context;

import com.fh.kaernten.estimotelocate.EstimoteApplication;
import com.fh.kaernten.estimotelocate.helper.NumberHelper;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * This class is a holder for RealVector so that it can be a GreenDao entity (impossible directly)
 */
@Entity
public class ThreeDimensionalVector {

    @Id
    private Long id;

    private Double xCoordinate;

    private Double yCoordinate;

    private Double zCoordinate;

    private transient RealVector vector;

    /**
     * Constructor which also inserts this entity directly into database
     *
     * @param context current activity
     * @param v       vector to save
     */
    public ThreeDimensionalVector(Context context, RealVector v) {
        vector = v;
        xCoordinate = v.getEntry(0);
        if (v.getDimension() > 1) yCoordinate = v.getEntry(1);
        if (v.getDimension() > 2) zCoordinate = v.getEntry(2);
        ThreeDimensionalVectorDao dao = getMyDao(context);
        dao.insert(this);
    }

    @Generated(hash = 1411996853)
    public ThreeDimensionalVector(Long id, Double xCoordinate, Double yCoordinate, Double zCoordinate) {
        this.id = id;
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        this.zCoordinate = zCoordinate;
    }

    @Generated(hash = 676653500)
    public ThreeDimensionalVector() {
    }

    public RealVector getAsVector() {
        if (vector == null) {
            int size = 1 + (yCoordinate != null ? +1 : +0) + (zCoordinate != null ? +1 : +0);
            vector = new ArrayRealVector(size);
            vector.setEntry(0, xCoordinate);
            vector.setEntry(1, yCoordinate);
            if (size > 2) vector.setEntry(2, zCoordinate);
        }
        return vector;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getxCoordinate() {
        return this.xCoordinate;
    }

    public void setxCoordinate(Double xCoordinate) {
        this.xCoordinate = xCoordinate;
    }

    public Double getyCoordinate() {
        return this.yCoordinate;
    }

    public void setyCoordinate(Double yCoordinate) {
        this.yCoordinate = yCoordinate;
    }

    public Double getzCoordinate() {
        return this.zCoordinate;
    }

    public void setzCoordinate(Double zCoordinate) {
        this.zCoordinate = zCoordinate;
    }

    public String toString(Context context) {
        return NumberHelper.formatDecimal(context, xCoordinate)
                + (yCoordinate != null ? ", " + NumberHelper.formatDecimal(context, yCoordinate) : "")
                + (zCoordinate != null ? ", " + NumberHelper.formatDecimal(context, zCoordinate) : "");
    }

    public void deleteSelf(Context context) {
        ThreeDimensionalVectorDao dao = getMyDao(context);
        dao.delete(this);
    }

    public void updateSelf(Context context) {
        ThreeDimensionalVectorDao dao = getMyDao(context);
        dao.update(this);
    }

    private ThreeDimensionalVectorDao getMyDao(Context context) {
        return ((EstimoteApplication) context.getApplicationContext())
                .getDaoSession().getThreeDimensionalVectorDao();
    }

    public Double getXCoordinate() {
        return this.xCoordinate;
    }

    public void setXCoordinate(Double xCoordinate) {
        this.xCoordinate = xCoordinate;
    }

    public Double getYCoordinate() {
        return this.yCoordinate;
    }

    public void setYCoordinate(Double yCoordinate) {
        this.yCoordinate = yCoordinate;
    }

    public Double getZCoordinate() {
        return this.zCoordinate;
    }

    public void setZCoordinate(Double zCoordinate) {
        this.zCoordinate = zCoordinate;
    }
}