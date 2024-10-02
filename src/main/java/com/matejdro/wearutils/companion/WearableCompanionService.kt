package com.matejdro.wearutils.companion

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import androidx.annotation.CallSuper
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.internal.zzgp
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import java.lang.ref.WeakReference

abstract class WearableCompanionService : Service(), MessageClient.OnMessageReceivedListener {
    protected val coroutineScope = MainScope()

    protected lateinit var messageClient: MessageClient

    private var ackTimeoutHandler = AckTimeoutHandler(WeakReference(this))

    override fun onCreate() {
        println("oncreate")
        super.onCreate()

        messageClient = Wearable.getMessageClient(applicationContext)

        startForeground(NOTIFICATION_ID_WEARABLE_COMPANION, createOngoingNotification())

        messageClient.addListener(
            this,
            Uri.parse("wear://*/"),
            MessageClient.FILTER_PREFIX
        )

        startTimeout()

        active = true
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.getParcelableExtra(EXTRA_PASSED_MESSAGE, zzgp::class.java)?.let {
            println("companion got passed")
            onMessageReceived(it)
        }

        return Service.START_STICKY
    }

    @CallSuper
    override fun onMessageReceived(event: MessageEvent) {
        println("companion received $event")

        startTimeout()

        when (event.path) {
            CompanionConnection.CONN_MESSAGE_STOP -> {
                stopSelf()
            }
        }
    }

    protected abstract fun createOngoingNotification(): Notification

    private fun startTimeout() {
        ackTimeoutHandler.removeMessages(MESSAGE_STOP_SELF)
        ackTimeoutHandler.sendMessageDelayed(
            Message.obtain().apply { what = MESSAGE_STOP_SELF },
            CompanionConnection.KEEPALIVE_TIMEOUT.inWholeMilliseconds
        )
    }

    override fun onDestroy() {
        println("ondestroy")
        active = false
        super.onDestroy()

        ackTimeoutHandler.removeCallbacksAndMessages(null)
        coroutineScope.cancel()
        messageClient.removeListener(this)

    }

    private class AckTimeoutHandler(val service: WeakReference<WearableCompanionService>) :
        Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            if (msg.what == MESSAGE_STOP_SELF) {
                service.get()?.stopSelf()
            }
        }
    }

    companion object {
        const val NOTIFICATION_ID_WEARABLE_COMPANION = 92957

        internal const val EXTRA_PASSED_MESSAGE = "PASSED_MESSAGE"

        private const val MESSAGE_STOP_SELF = 0

        var active = false
    }
}
