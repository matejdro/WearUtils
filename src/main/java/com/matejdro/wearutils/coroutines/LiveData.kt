package com.matejdro.wearutils.coroutines

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import kotlinx.coroutines.experimental.suspendCancellableCoroutine

suspend fun <T> LiveData<T>.awaitFirstValue() : T {
    return suspendCancellableCoroutine { continuation ->
        val observer = Observer<T> {
            if (it != null) {
                continuation.resume(it)
            }
        }

        continuation.invokeOnCompletion {
            removeObserver(observer)
        }

        observeForever(observer)
    }
}