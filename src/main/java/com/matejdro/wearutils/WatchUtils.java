package com.matejdro.wearutils;

import android.content.Context;
import android.provider.Settings;

public class WatchUtils
{
    public static boolean isWatchInTheaterMode(Context context)
    {
        return Settings.Global.getInt(context.getContentResolver(), "theater_mode_on", 0) != 0;
    }
}
