package com.matejdro.wearutils.lifecycle;


import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

public class EmptyObserver<T> implements Observer<T> {
    @Override
    public void onChanged(@Nullable T t) {
    }
}
