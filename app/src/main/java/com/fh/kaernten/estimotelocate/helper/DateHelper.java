package com.fh.kaernten.estimotelocate.helper;

import android.content.Context;

import org.greenrobot.greendao.converter.PropertyConverter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;


public class DateHelper {

    /**
     * @param context  used to find locale
     * @param calendar calendar to print
     * @return date in format dd-MMM-yyyy HH:mm
     */
    public static String format(Context context, Calendar calendar) {
        if (calendar != null && context != null) {
            SimpleDateFormat fmt = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss",
                    LocaleHelper.getSystemLocale(context));
            fmt.setCalendar(calendar);
            return fmt.format(calendar.getTime());
        }
        return "";
    }

    /**
     * GreenDao converter class which converts db values (long) to Calendar instances
     */
    public static class CalendarConverter implements PropertyConverter<Calendar, Long> {

        @Override
        public Calendar convertToEntityProperty(Long databaseValue) {
            if (databaseValue == null) {
                return null;
            }
            GregorianCalendar c = new GregorianCalendar();
            c.setTimeInMillis(databaseValue);
            return c;
        }

        @Override
        public Long convertToDatabaseValue(Calendar entityProperty) {
            return entityProperty == null ? null : entityProperty.getTimeInMillis();
        }
    }
}
