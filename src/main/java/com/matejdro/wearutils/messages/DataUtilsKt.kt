package com.matejdro.wearutils.messages

import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataItemAsset
import com.matejdro.wearutils.coroutines.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun DataClient.getByteArrayAsset(asset: DataItemAsset): ByteArray {
    val inputStream = this.getFdForAsset(asset).await().inputStream

    return withContext(Dispatchers.IO) {
        inputStream.use {
            it.readBytes()
        }
    }
}
