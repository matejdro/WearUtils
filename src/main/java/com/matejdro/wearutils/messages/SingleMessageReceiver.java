package com.matejdro.wearutils.messages;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * A {@link Future} implementation that receives single message over Wear channel.
 */
public class SingleMessageReceiver implements Future<MessageEvent> {
    private final GoogleApiClient googleApiClient;
    private final CountDownLatch waitingLatch;
    private MessageEvent result;

    private boolean cancelled = false;

    public SingleMessageReceiver(GoogleApiClient googleApiClient) {
        this.googleApiClient = googleApiClient;
        waitingLatch = new CountDownLatch(1);
        Wearable.MessageApi.addListener(googleApiClient, messageListener);
    }

    public SingleMessageReceiver(GoogleApiClient googleApiClient, Uri uriFilter, int filterType)
    {
        this.googleApiClient = googleApiClient;
        waitingLatch = new CountDownLatch(1);
        Wearable.MessageApi.addListener(googleApiClient, messageListener, uriFilter, filterType);
    }


    private void finish()
    {
        Wearable.MessageApi.removeListener(googleApiClient, messageListener);
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning)
    {
        if (cancelled || isDone())
            return false;
        cancelled = true;

        finish();

        return false;
    }

    @Override
    public boolean isCancelled()
    {
        return cancelled;
    }

    @Override
    public boolean isDone()
    {
        return waitingLatch.getCount() == 0;
    }

    @Override
    @WorkerThread
    public MessageEvent get() throws InterruptedException
    {
        waitingLatch.await();
        return result;
    }

    @Override
    @WorkerThread
    public MessageEvent get(long timeout, @NonNull TimeUnit unit) throws InterruptedException, TimeoutException
    {
        waitingLatch.await(timeout, unit);
        if (!isDone()) {
            finish();
            throw new TimeoutException();
        }

        return result;
    }

    private final MessageApi.MessageListener messageListener = new MessageApi.MessageListener()
    {
        @Override
        public void onMessageReceived(MessageEvent messageEvent)
        {
            result = messageEvent;
            waitingLatch.countDown();
            finish();
        }
    };

}
