package com.fh.kaernten.estimotelocate.helper;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;

import java.util.Locale;


public class LocaleHelper {

    @SuppressWarnings("deprecation")
    public static Locale getSystemLocale(Context context) {
        Configuration config = context.getResources().getConfiguration();
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            return config.getLocales().get(0);
        else
            return config.locale;
    }
}
