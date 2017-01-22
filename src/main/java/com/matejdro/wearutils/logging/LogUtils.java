package com.matejdro.wearutils.logging;

import android.os.Bundle;

import timber.log.Timber;

public class LogUtils {
    public static void dumpBundle(Bundle bundle)
    {
        Timber.d("BundleDump: (%d entries)", bundle.size());
        for (String key : bundle.keySet())
        {
            Object value = bundle.get(key);
            if (value instanceof Bundle)
            {
                Timber.d("%s :", key);
                dumpBundle((Bundle) value);
            }
            else
            {
                Timber.d("%s: %s", key, String.valueOf(value));
            }
        }
    }
}
