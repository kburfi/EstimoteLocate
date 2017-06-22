package com.fh.kaernten.estimotelocate.helper;

import android.content.Context;
import android.support.annotation.Nullable;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class NumberHelper {

    public static Double roundTo1Decimal(Double f) {
        return new Double(Math.round(f * 10.0) / 10.0);
    }

    public static String localizedDoubleToString(Context c, Double d) {
        if (c != null && d != null) {
            return applyPattern(c, d, "##0.0000000");
//            NumberFormat n = NumberFormat.getInstance(LocaleHelper.getSystemLocale(c));
//            return n.format(d);
        }
        return "";
    }

    public static String formatDecimal(Context c, Double d) {
        if (c != null && d != null) {
            return applyPattern(c, d, "###,##0.0");
        }
        return "";
    }

    @Nullable
    private static String applyPattern(Context c, Double d, String pattern) {
        NumberFormat n = NumberFormat.getInstance(LocaleHelper.getSystemLocale(c));
        n.setRoundingMode(RoundingMode.HALF_UP);
        if (n instanceof DecimalFormat) {
            ((DecimalFormat) n).applyPattern(pattern);
            return n.format(d);
        }
        return "";
    }

    public static Double convertToDouble(String d, double defaultReturnValue) {
        NumberFormat formatter = NumberFormat.getNumberInstance(Locale.ENGLISH);
        try {
            return formatter.parse(d.replaceAll(",", ".")).doubleValue();
        } catch (Exception e) {
            return defaultReturnValue;
        }
    }
}