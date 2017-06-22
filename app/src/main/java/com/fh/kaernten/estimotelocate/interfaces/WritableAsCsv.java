package com.fh.kaernten.estimotelocate.interfaces;

import android.content.Context;

/**
 * Interface for objects that may be exported as csv
 */
public interface WritableAsCsv {

    String[] getHeadlinesAsArray(Context context);

    String[] getValuesAsArray(Context context);
}
