package com.matejdro.wearutils.companionnotice

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

abstract class WearCompanionWatchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            val capabilityClient = Wearable.getCapabilityClient(this@WearCompanionWatchActivity)

            val matchingCapabilities = capabilityClient.getCapability(
                getPhoneAppPresenceCapability(),
                CapabilityClient.FILTER_ALL
            ).await()

            val installedOnWatch = !matchingCapabilities.nodes.isEmpty()
            onWatchAppInstalledResult(installedOnWatch)
        }
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
