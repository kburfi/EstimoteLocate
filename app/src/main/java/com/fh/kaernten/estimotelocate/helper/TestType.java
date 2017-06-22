package com.fh.kaernten.estimotelocate.helper;

import android.content.Context;

import com.fh.kaernten.estimotelocate.R;

import org.greenrobot.greendao.converter.PropertyConverter;

import java.io.Serializable;

/**
 * Defines possible type of tests
 * Created by Kristian on 24.05.2017.
 */
public enum TestType implements Serializable {

    UNKNOWN(0),
    BATTERY_RUNTIME(1),
    ACCURACY_2D(2),
    ACCURACY_3D(3),
    MOVEMENT_MONITORING(4),
    ALWAYS_RANGING(5);

    public TestDimension getAccordingDimension() {
        switch (this) {
            case ACCURACY_2D:
            case MOVEMENT_MONITORING:
            case ALWAYS_RANGING:
                return TestDimension.TwoDimensional;
            case ACCURACY_3D:
                return TestDimension.ThreeDimensional;
            default:
                return null;
        }
    }

    public int getDimensionAsInt() {
        return getAccordingDimension() == TestDimension.ThreeDimensional ? 3 : 2;
    }

    public String getName(Context context) {
        switch (this) {
            case BATTERY_RUNTIME:
                return context.getString(R.string.test_battery);
            case ACCURACY_2D:
                return context.getString(R.string.test_accuracy_2D);
            case ACCURACY_3D:
                return context.getString(R.string.test_accuracy_3D);
            case MOVEMENT_MONITORING:
                return context.getString(R.string.test_movement);
            case ALWAYS_RANGING:
                return context.getString(R.string.test_ranging);
            case UNKNOWN:
            default:
                return "";
        }
    }

    private final int dbId;

    TestType(int dbId) {
        this.dbId = dbId;
    }

    public int getDbId() {
        return dbId;
    }

    public static class TestTypeConverter implements PropertyConverter<TestType, Integer> {

        @Override
        public TestType convertToEntityProperty(Integer databaseValue) {
            if (databaseValue == null) {
                return null;
            }
            for (TestType role : TestType.values()) {
                if (role.getDbId() == databaseValue) {
                    return role;
                }
            }
            return UNKNOWN;
        }

        @Override
        public Integer convertToDatabaseValue(TestType entityProperty) {
            return entityProperty == null ? null : entityProperty.getDbId();
        }
    }
}