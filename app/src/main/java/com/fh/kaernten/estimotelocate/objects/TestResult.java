package com.fh.kaernten.estimotelocate.objects;

import android.content.Context;

import com.fh.kaernten.estimotelocate.helper.BatteryHelper;
import com.fh.kaernten.estimotelocate.helper.DateHelper;
import com.fh.kaernten.estimotelocate.helper.NumberHelper;
import com.fh.kaernten.estimotelocate.helper.TestType;
import com.fh.kaernten.estimotelocate.interfaces.WritableAsCsv;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToOne;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Used for testing accuracy of trilateration
 */
@Entity
public class TestResult implements WritableAsCsv {

    public static String[] PROPERTY_NAMES = {
            TestResultDao.Properties.Time.columnName,
            TestResultDao.Properties.BatteryLevel.columnName,
            TestResultDao.Properties.NormOfStandardDeviation.columnName,
            TestResultDao.Properties.DistanceToActualLocation.columnName,
            "Linear X",
            "Linear Y",
            "Linear Z",
            "Calculated X",
            "Calculated Y",
            "Calculated Z",
            "Corrected X",
            "Corrected Y",
            "Corrected Z"
    };

    @Id
    private Long id;

    @Convert(converter = TestType.TestTypeConverter.class, columnType = Integer.class)
    private TestType testType;

    @Convert(converter = DateHelper.CalendarConverter.class, columnType = Long.class)
    private Calendar time;

    private float batteryLevel;

    private Long linearId;
    @ToOne(joinProperty = "linearId")
    private ThreeDimensionalVector linearPosition;

    private Long calculatedId;
    @ToOne(joinProperty = "calculatedId")
    private ThreeDimensionalVector calculatedPosition;

    private Long correctedId;
    @ToOne(joinProperty = "correctedId")
    private ThreeDimensionalVector correctedPosition;

    private Double normOfStandardDeviation;

    private Double distanceToActualLocation;

    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /**
     * Used for active entity operations.
     */
    @Generated(hash = 793752935)
    private transient TestResultDao myDao;

    @Generated(hash = 1724090358)
    private transient Long calculatedPosition__resolvedKey;

    @Generated(hash = 573918357)
    private transient Long correctedPosition__resolvedKey;

    @Generated(hash = 1442632946)
    private transient Long linearPosition__resolvedKey;

    /**
     * Constructor which automatically sets current battery level and time
     *
     * @param context used to read battery level
     */
    public TestResult(Context context, TestType type) {
        setTestType(type);
        setTime(new GregorianCalendar());
        setBatteryLevel(BatteryHelper.getCurrentLevel(context));
    }

    @Generated(hash = 106033544)
    public TestResult(Long id, TestType testType, Calendar time, float batteryLevel, Long linearId, Long calculatedId, Long correctedId,
                      Double normOfStandardDeviation, Double distanceToActualLocation) {
        this.id = id;
        this.testType = testType;
        this.time = time;
        this.batteryLevel = batteryLevel;
        this.linearId = linearId;
        this.calculatedId = calculatedId;
        this.correctedId = correctedId;
        this.normOfStandardDeviation = normOfStandardDeviation;
        this.distanceToActualLocation = distanceToActualLocation;
    }

    @Generated(hash = 808538852)
    public TestResult() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getNormOfStandardDeviation() {
        return this.normOfStandardDeviation;
    }

    public void setNormOfStandardDeviation(Double normOfStandardDeviation) {
        this.normOfStandardDeviation = normOfStandardDeviation;
    }

    public float getBatteryLevel() {
        return this.batteryLevel;
    }

    public void setBatteryLevel(float batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public TestType getTestType() {
        return this.testType;
    }

    public void setTestType(TestType testType) {
        this.testType = testType;
    }

    public Calendar getTime() {
        return this.time;
    }

    public void setTime(Calendar time) {
        this.time = time;
    }

    public String printResult(Context context) {
        return DateHelper.format(context, getTime())
                + " with battery level " + getBatteryLevel() * 100 + "%";
    }

    public String printDetails(Context context) {
        StringBuilder b = new StringBuilder();
        if (getCorrectedPosition() != null) {
            b.append("Pos: ");
            b.append(getCorrectedPosition().toString(context));
        }
        if (getDistanceToActualLocation() != null) {
            b.append("\n").append(NumberHelper.formatDecimal(context, getDistanceToActualLocation()));
        }
        return b.toString();
    }

    @Override
    public String[] getHeadlinesAsArray(Context context) {
        return PROPERTY_NAMES;
    }

    @Override
    public String[] getValuesAsArray(Context context) {
        return new String[]{
                DateHelper.format(context, getTime()),
                NumberHelper.localizedDoubleToString(context, (double) getBatteryLevel()),
                NumberHelper.localizedDoubleToString(context, getNormOfStandardDeviation()),
                NumberHelper.localizedDoubleToString(context, getDistanceToActualLocation()),
                getLinearPosition() != null ? NumberHelper.localizedDoubleToString(context, getLinearPosition().getxCoordinate()) : null,
                getLinearPosition() != null ? NumberHelper.localizedDoubleToString(context, getLinearPosition().getyCoordinate()) : null,
                getLinearPosition() != null ? NumberHelper.localizedDoubleToString(context, getLinearPosition().getzCoordinate()) : null,
                getCalculatedPosition() != null ? NumberHelper.localizedDoubleToString(context, getCalculatedPosition().getxCoordinate()) : null,
                getCalculatedPosition() != null ? NumberHelper.localizedDoubleToString(context, getCalculatedPosition().getyCoordinate()) : null,
                getCalculatedPosition() != null ? NumberHelper.localizedDoubleToString(context, getCalculatedPosition().getzCoordinate()) : null,
                getCorrectedPosition() != null ? NumberHelper.localizedDoubleToString(context, getCorrectedPosition().getxCoordinate()) : null,
                getCorrectedPosition() != null ? NumberHelper.localizedDoubleToString(context, getCorrectedPosition().getyCoordinate()) : null,
                getCorrectedPosition() != null ? NumberHelper.localizedDoubleToString(context, getCorrectedPosition().getzCoordinate()) : null,
        };
    }

    public Long getCalculatedId() {
        return this.calculatedId;
    }

    public void setCalculatedId(Long calculatedId) {
        this.calculatedId = calculatedId;
    }

    public Long getCorrectedId() {
        return this.correctedId;
    }

    public void setCorrectedId(Long correctedId) {
        this.correctedId = correctedId;
    }

    public Double getDistanceToActualLocation() {
        return this.distanceToActualLocation;
    }

    public void setDistanceToActualLocation(Double distanceToActualLocation) {
        this.distanceToActualLocation = distanceToActualLocation;
    }

    /**
     * To-one relationship, resolved on first access.
     */
    @Generated(hash = 227659695)
    public ThreeDimensionalVector getCalculatedPosition() {
        Long __key = this.calculatedId;
        if (calculatedPosition__resolvedKey == null || !calculatedPosition__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ThreeDimensionalVectorDao targetDao = daoSession.getThreeDimensionalVectorDao();
            ThreeDimensionalVector calculatedPositionNew = targetDao.load(__key);
            synchronized (this) {
                calculatedPosition = calculatedPositionNew;
                calculatedPosition__resolvedKey = __key;
            }
        }
        return calculatedPosition;
    }

    /**
     * called by internal mechanisms, do not call yourself.
     */
    @Generated(hash = 951325064)
    public void setCalculatedPosition(ThreeDimensionalVector calculatedPosition) {
        synchronized (this) {
            this.calculatedPosition = calculatedPosition;
            calculatedId = calculatedPosition == null ? null : calculatedPosition.getId();
            calculatedPosition__resolvedKey = calculatedId;
        }
    }

    /**
     * To-one relationship, resolved on first access.
     */
    @Generated(hash = 1915037803)
    public ThreeDimensionalVector getCorrectedPosition() {
        Long __key = this.correctedId;
        if (correctedPosition__resolvedKey == null || !correctedPosition__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ThreeDimensionalVectorDao targetDao = daoSession.getThreeDimensionalVectorDao();
            ThreeDimensionalVector correctedPositionNew = targetDao.load(__key);
            synchronized (this) {
                correctedPosition = correctedPositionNew;
                correctedPosition__resolvedKey = __key;
            }
        }
        return correctedPosition;
    }

    /**
     * called by internal mechanisms, do not call yourself.
     */
    @Generated(hash = 369491892)
    public void setCorrectedPosition(ThreeDimensionalVector correctedPosition) {
        synchronized (this) {
            this.correctedPosition = correctedPosition;
            correctedId = correctedPosition == null ? null : correctedPosition.getId();
            correctedPosition__resolvedKey = correctedId;
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

    public void deleteSelf(Context context) {
        if (getCalculatedPosition() != null) {
            getCalculatedPosition().deleteSelf(context);
        }
        if (getCorrectedPosition() != null) {
            getCorrectedPosition().deleteSelf(context);
        }
        delete();
    }

    public Long getLinearId() {
        return this.linearId;
    }

    public void setLinearId(Long linearId) {
        this.linearId = linearId;
    }

    /**
     * To-one relationship, resolved on first access.
     */
    @Generated(hash = 1194337428)
    public ThreeDimensionalVector getLinearPosition() {
        Long __key = this.linearId;
        if (linearPosition__resolvedKey == null || !linearPosition__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ThreeDimensionalVectorDao targetDao = daoSession.getThreeDimensionalVectorDao();
            ThreeDimensionalVector linearPositionNew = targetDao.load(__key);
            synchronized (this) {
                linearPosition = linearPositionNew;
                linearPosition__resolvedKey = __key;
            }
        }
        return linearPosition;
    }

    /**
     * called by internal mechanisms, do not call yourself.
     */
    @Generated(hash = 1284392562)
    public void setLinearPosition(ThreeDimensionalVector linearPosition) {
        synchronized (this) {
            this.linearPosition = linearPosition;
            linearId = linearPosition == null ? null : linearPosition.getId();
            linearPosition__resolvedKey = linearId;
        }
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 378595240)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getTestResultDao() : null;
    }
}