package com.matejdro.wearutils.lifecycle;

public class LiveDataLifecycleCombiner implements LiveDataLifecycleListener {
    private final LiveDataLifecycleListener targetListener;
    private int activeCount = 0;

    public LiveDataLifecycleCombiner(LiveDataLifecycleListener targetListener) {
        this.targetListener = targetListener;
    }

    public void addLiveData(ListenableLiveData<?> listenableLiveData) {
        listenableLiveData.setListener(this);
    }

    public boolean isActive() {
        return activeCount > 0;
    }

    @Override
    public void onActive() {
        activeCount++;
        if (activeCount > 0 && targetListener != null) {
            targetListener.onActive();
        }
    }

    @Override
    public void onInactive() {
        activeCount--;

        if (activeCount == 0 && targetListener != null) {
            targetListener.onInactive();
        }

    }
}
