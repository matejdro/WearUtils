package com.matejdro.wearutils.lifecycle;

import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.CallSuper;

public class ListenableLiveData<T> extends MutableLiveData<T> {
    private LiveDataLifecycleListener listener;

    public void setListener(LiveDataLifecycleListener listener) {
        this.listener = listener;
    }

    @Override
    public void postValue(T value) {
        super.postValue(value);
    }

    @Override
    public void setValue(T value) {
        super.setValue(value);
    }

    @Override
    @CallSuper
    protected void onActive() {
        if (listener != null) {
            listener.onActive();
        }

        super.onActive();
    }

    @Override
    @CallSuper
    protected void onInactive() {
        if (listener != null) {
            listener.onInactive();
        }

        super.onInactive();
    }
}
