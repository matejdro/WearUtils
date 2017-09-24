package com.matejdro.wearutils.logging;

import android.content.Intent;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

public abstract class WearLogRequestReceiver extends WearableListenerService {
    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Intent logSendingIntent = new Intent(this, LogTransmitter.class);
        logSendingIntent.putExtra(LogTransmitter.EXTRA_TARGET_NODE_ID, messageEvent.getSourceNodeId());
        logSendingIntent.putExtra(LogTransmitter.EXTRA_TARGET_PATH, getLogsChannelPath());
        startService(logSendingIntent);
    }

    protected abstract String getLogsChannelPath();
}
