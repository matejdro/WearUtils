package com.matejdro.wearutils.coroutines

import com.google.android.gms.tasks.Task
import kotlinx.coroutines.experimental.suspendCancellableCoroutine

suspend fun <T> Task<T>.await(): T {
    return suspendCancellableCoroutine { continuation ->
        addOnCompleteListener {
            if (isSuccessful) {
                continuation.resume(result)
            } else {
                continuation.resumeWithException(exception!!)
            }
        }
    }
}