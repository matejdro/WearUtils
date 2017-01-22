package com.matejdro.wearutils.preferencesync;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Service that implements receiver part of the preference syncing between phone and watch.
 *
 * To use, create implementation and add it to manifest:
 *
 * {@code         <service android:name=".PreferenceReceiverService"
                        tools:ignore="ExportedService">
                        <intent-filter>
                            <action android:name="com.google.android.gms.wearable.DATA_CHANGED" />
                            <data android:scheme="wear" android:host="*" android:pathPrefix="/Settings" />
                        </intent-filter>
                    </service>
   }
 */
public abstract class PreferenceReceiverService extends WearableListenerService
{
    private static final String TAG = "PreferenceReceiver";

    private final String preferencesPrefix;

    public PreferenceReceiverService(String preferencesPrefix)
    {
        this.preferencesPrefix = preferencesPrefix;
    }

    protected abstract SharedPreferences.Editor getDestinationPreferences();

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer)
    {
        SharedPreferences.Editor preferenceEditor = getDestinationPreferences();

        for (DataEvent dataEvent : dataEventBuffer)
        {
            DataItem dataItem = dataEvent.getDataItem();
            if (!dataItem.getUri().getPath().equals(preferencesPrefix))
                continue;


            DataMap dataMap = DataMapItem.fromDataItem(dataItem).getDataMap();
            for (String key : dataMap.keySet())
            {
                putIntoSharedPreferences(preferenceEditor, key, dataMap.get(key));
            }
        }

        preferenceEditor.apply();
    }

    private static void putIntoSharedPreferences(SharedPreferences.Editor editor, String key, Object value)
    {
        if (value instanceof Integer)
            editor.putInt(key, (Integer) value);
        else if (value instanceof Long)
            editor.putLong(key, (Long) value);
        else if (value instanceof Boolean)
            editor.putBoolean(key, (Boolean) value);
        else if (value instanceof Float)
            editor.putFloat(key, (Float) value);
        else if (value instanceof String)
            editor.putString(key, (String) value);
        else
            Log.w(TAG, "putIntoSharedPreferences: Unknown type of the object: " + value.getClass().getName());
    }
}
