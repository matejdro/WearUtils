package com.matejdro.wearutils.lifecycle;


import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;

public class EmptyObserver<T> implements Observer<T> {
    @Override
    public void onChanged(@Nullable T t) {
    }
}
