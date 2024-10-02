package com.matejdro.wearutils.companion

import android.content.Intent
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import com.google.android.gms.wearable.internal.zzgp

abstract class CompanionStarterService: WearableListenerService() {
    override fun onMessageReceived(event: MessageEvent) {
        println("starter onMessageReceived $event")
        val intent = createCompanionServiceIntent().apply {
            if (!WearableCompanionService.active) {
                putExtra(WearableCompanionService.EXTRA_PASSED_MESSAGE, event as zzgp)
            }
        }

        startForegroundService(intent)
    }

    protected abstract fun createCompanionServiceIntent(): Intent
}
