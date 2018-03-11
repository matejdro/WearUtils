@file:JvmName("MapUtils")

package com.matejdro.wearutils.miscutils

inline fun <T, R> T?.mapIfNotNull(mappingBlock: (T) -> R): R? {
    if (this != null) {
        return mappingBlock(this)
    }

    return null
}