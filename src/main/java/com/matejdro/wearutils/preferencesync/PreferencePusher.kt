package com.matejdro.wearutils.preferencesync

import android.content.Context
import android.content.SharedPreferences
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.PendingResult
import com.google.android.gms.wearable.DataApi.DataItemResult
import com.google.android.gms.wearable.DataItem
import com.google.android.gms.wearable.DataMap
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import com.matejdro.wearutils.coroutines.await
import timber.log.Timber

object PreferencePusher {
    private const val TAG = "PreferencePusher"
    fun pushPreferences(connectedApiClient: GoogleApiClient, preferences: SharedPreferences, wearSchemePrefix: String, urgent: Boolean): PendingResult<DataItemResult> {
        val putDataMapRequest = PutDataMapRequest.create(wearSchemePrefix)
        val dataMap = putDataMapRequest.dataMap
        for ((key, value) in preferences.all) {
            putIntoDataMap(dataMap, key, value!!)
        }
        if (urgent) putDataMapRequest.setUrgent()
        return Wearable.DataApi.putDataItem(connectedApiClient, putDataMapRequest.asPutDataRequest())
    }

    suspend fun pushPreferences(context: Context, preferences: SharedPreferences, wearSchemePrefix: String, urgent: Boolean): DataItem {
        val dataClient = Wearable.getDataClient(context)
        val putDataMapRequest = PutDataMapRequest.create(wearSchemePrefix)
        val dataMap = putDataMapRequest.dataMap
        for ((key, value) in preferences.all) {
            putIntoDataMap(dataMap, key, value!!)
        }
        if (urgent) putDataMapRequest.setUrgent()
        return dataClient.putDataItem(putDataMapRequest.asPutDataRequest()).await()
    }

    private fun putIntoDataMap(dataMap: DataMap, key: String, value: Any) {
        when (value) {
            is Int -> dataMap.putInt(key, value)
            is Long -> dataMap.putLong(key, value)
            is Boolean -> dataMap.putBoolean(key, value)
            is Float -> dataMap.putFloat(key, value)
            is String -> dataMap.putString(key, value)
            else -> Timber.w("putIntoDataMap: Unknown type of the object: " + value.javaClass.name)
        }
    }
}
