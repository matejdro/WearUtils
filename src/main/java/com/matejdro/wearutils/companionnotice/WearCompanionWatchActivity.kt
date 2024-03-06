package com.matejdro.wearutils.companionnotice

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.wearable.CapabilityApi
import com.google.android.gms.wearable.CapabilityApi.GetCapabilityResult
import com.google.android.gms.wearable.Wearable

abstract class WearCompanionWatchActivity : AppCompatActivity(),
    GoogleApiClient.ConnectionCallbacks, ResultCallback<GetCapabilityResult> {
    private lateinit var googleApiClient: GoogleApiClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        googleApiClient = GoogleApiClient.Builder(this)
            .addApi(Wearable.API)
            .addConnectionCallbacks(this)
            .build()
    }

    override fun onStart() {
        googleApiClient.connect()
        super.onStart()
    }

    override fun onStop() {
        googleApiClient.disconnect()
        super.onStop()
    }

    override fun onConnected(bundle: Bundle?) {
        Wearable.CapabilityApi.getCapability(
            googleApiClient,
            getPhoneAppPresenceCapability(),
            CapabilityApi.FILTER_ALL
        )
            .setResultCallback(this)
    }

    override fun onConnectionSuspended(i: Int) {}
    override fun onResult(getCapabilityResult: GetCapabilityResult) {
        val installedOnWatch = !getCapabilityResult.capability.nodes.isEmpty()
        onWatchAppInstalledResult(installedOnWatch)
    }

    protected fun onWatchAppInstalledResult(watchAppInstalled: Boolean) {
        if (watchAppInstalled) {
            return
        }
        startActivity(Intent(this, PhoneAppNoticeActivity::class.java))
        finish()
    }

    abstract fun getPhoneAppPresenceCapability(): String
}
