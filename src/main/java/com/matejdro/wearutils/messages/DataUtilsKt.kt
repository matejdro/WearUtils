package com.matejdro.wearutils.messages

import com.google.android.gms.common.api.ApiException
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataItemAsset
import com.google.android.gms.wearable.WearableStatusCodes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

suspend fun DataClient.getByteArrayAsset(asset: DataItemAsset): ByteArray? {
    return try {
        val inputStream = this.getFdForAsset(asset).await().inputStream

        withContext(Dispatchers.IO) {
            inputStream.use {
                it.readBytes()
            }
        }
    } catch (e: ApiException) {
        if (e.statusCode == WearableStatusCodes.ASSET_UNAVAILABLE) {
            null
        } else {
            throw e
        }
    }
}
