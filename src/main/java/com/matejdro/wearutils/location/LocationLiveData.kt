package com.matejdro.wearutils.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import androidx.lifecycle.LiveData
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

/**
 * LiveData that provides user's location.
 */
class LocationLiveData(context: Context,
                       private val locationRequest: LocationRequest) : LiveData<Location>(){
    private val locationClient = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    override fun onActive() {
        locationClient.requestLocationUpdates(locationRequest, callback, null)
    }

    override fun onInactive() {
        locationClient.removeLocationUpdates(callback)
    }

    private val callback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            val newLocation = result.lastLocation ?: return
            postValue(newLocation)
        }
    }
}
