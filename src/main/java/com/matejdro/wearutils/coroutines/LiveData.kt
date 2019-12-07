package com.matejdro.wearutils.coroutines

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

suspend fun <T> LiveData<T>.awaitFirstValue(): T {
    return withContext(Dispatchers.Main) {
        return@withContext suspendCancellableCoroutine<T> { continuation ->
            val observer = Observer<T> {
                if (it != null) {
                    continuation.resume(it)
                }
            }

            continuation.invokeOnCancellation {
                GlobalScope.launch(Dispatchers.Main) {
                    removeObserver(observer)
                }
            }

            observeForever(observer)
        }
    }
}