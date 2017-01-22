package com.matejdro.wearutils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Class that allows detection on whether watch is being worn (on-the-body).
 *
 * To enable this functionality, add following code to the manifest:
 * {@code <receiver android:name="com.matejdro.wearutils.WatchOnBodyReceiver">
            <intent-filter>
                <action android:name="android.intent.action.BOOT_COMPLETED" />
            </intent-filter>
            <intent-filter>
                <action android:name="com.google.android.wearable.action.DEVICE_ON_BODY_RECOGNITION" />
            </intent-filter>
        </receiver>}
 */
public class WatchOnBodyReceiver extends BroadcastReceiver {
    private static final String PREFS_FILE_NAME = "com.matejdro.wearutils.watchbody";
    private static final String PREF_NAME = "CurrentBodyState";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            // Reset stored value on boot
            resetValue(context);
        } else if ("com.google.android.wearable.action.DEVICE_ON_BODY_RECOGNITION".equals(intent.getAction())) {
            setStoredValue(context, intent.getBooleanExtra("is_don", true));
        }
    }

    private static void setStoredValue(Context context, boolean storedValue)
    {
        context.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(PREF_NAME, storedValue)
                .apply();
    }

    /**
     * Reset value to "on body".
     * Use when in situations when you are sure user is interacting with the watch
     * to fix possibly false positive.
     */
    public static void resetValue(Context context)
    {
        setStoredValue(context, true);
    }

    public static boolean isWatchOnUsersBody(Context context)
    {
        return context.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE).getBoolean(PREF_NAME, true);
    }
}
