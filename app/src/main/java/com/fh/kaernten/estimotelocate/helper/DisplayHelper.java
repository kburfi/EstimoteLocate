package com.fh.kaernten.estimotelocate.helper;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

public class DisplayHelper {

    public static int convertPxToDp(int px, Context context) {
        if (context != null) {
            float d = context.getResources().getDisplayMetrics().density;
            return (int) (px * d);
        }
        return -1;
    }

    public static float convertDpToPx(Context context, float valueInDp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }
}