package com.matejdro.wearutils.messages;

import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Channel;
import com.google.android.gms.wearable.ChannelApi;
import com.google.android.gms.wearable.Wearable;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * A {@link Future} implementation that receives single message over Wear channel.
 */
public class SingleChannelReceiver implements Future<Channel> {
    private final GoogleApiClient googleApiClient;
    private final CountDownLatch waitingLatch;
    private Channel result;

    private boolean cancelled = false;

    public SingleChannelReceiver(GoogleApiClient googleApiClient) {
        this.googleApiClient = googleApiClient;
        waitingLatch = new CountDownLatch(1);
        Wearable.ChannelApi.addListener(googleApiClient, channelListener);
    }

    private void finish()
    {
        Wearable.ChannelApi.removeListener(googleApiClient, channelListener);
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
    public Channel get() throws InterruptedException
    {
        waitingLatch.await();
        return result;
    }

    @Override
    @WorkerThread
    public Channel get(long timeout, @NonNull TimeUnit unit) throws InterruptedException, TimeoutException
    {
        waitingLatch.await(timeout, unit);
        if (!isDone()) {
            finish();
            throw new TimeoutException();
        }

        return result;
    }

    private final ChannelApi.ChannelListener channelListener = new ChannelApi.ChannelListener()
    {
        @Override
        public void onChannelOpened(Channel channel) {
            result = channel;
            waitingLatch.countDown();
            finish();

        }

        @Override
        public void onChannelClosed(Channel channel, int i, int i1) {

        }

        @Override
        public void onInputClosed(Channel channel, int i, int i1) {

        }

        @Override
        public void onOutputClosed(Channel channel, int i, int i1) {

        }
    };
}
