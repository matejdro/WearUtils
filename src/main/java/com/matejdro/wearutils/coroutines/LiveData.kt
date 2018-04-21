package com.matejdro.wearutils.coroutines

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.suspendCancellableCoroutine
import kotlinx.coroutines.experimental.withContext

suspend fun <T> LiveData<T>.awaitFirstValue(): T {
    return withContext(UI) {
        return@withContext suspendCancellableCoroutine<T> { continuation ->
            val observer = Observer<T> {
                if (it != null) {
                    continuation.resume(it)
                }
            }

            continuation.invokeOnCompletion {
                launch(UI) {
                    removeObserver(observer)
                }
            }

            observeForever(observer)
        }
    }
}