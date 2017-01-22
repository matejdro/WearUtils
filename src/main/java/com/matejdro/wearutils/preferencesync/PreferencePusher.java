package com.matejdro.wearutils.preferencesync;

import android.content.SharedPreferences;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.Map;

import timber.log.Timber;

public class PreferencePusher
{
    private static final String TAG = "PreferencePusher";

    public static PendingResult<DataApi.DataItemResult> pushPreferences(GoogleApiClient connectedApiClient, SharedPreferences preferences, String wearSchemePrefix, boolean urgent)
    {
        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(wearSchemePrefix);
        DataMap dataMap = putDataMapRequest.getDataMap();
        for (Map.Entry<String, ?> preference : preferences.getAll().entrySet())
        {
            putIntoDataMap(dataMap, preference.getKey(), preference.getValue());
        }

        if (urgent)
            putDataMapRequest.setUrgent();

        return Wearable.DataApi.putDataItem(connectedApiClient, putDataMapRequest.asPutDataRequest());
    }

    private static void putIntoDataMap(DataMap dataMap, String key, Object value)
    {
        if (value instanceof Integer)
            dataMap.putInt(key, (Integer) value);
        else if (value instanceof Long)
            dataMap.putLong(key, (Long) value);
        else if (value instanceof Boolean)
            dataMap.putBoolean(key, (Boolean) value);
        else if (value instanceof Float)
            dataMap.putFloat(key, (Float) value);
        else if (value instanceof String)
            dataMap.putString(key, (String) value);
        else
            Timber.w("putIntoDataMap: Unknown type of the object: " + value.getClass().getName());
    }

}

