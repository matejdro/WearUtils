package com.matejdro.wearutils.miscutils

import java.time.Clock
import java.time.LocalDateTime
import java.util.*

/**
 * Class that provides mockable current time.
 */
object TimeProvider {
    fun currentTimeMillis() : Long = currentTimeMillisOverride()
    fun currentDate() : Date = Date(currentTimeMillis())

    var currentTimeMillisOverride : () -> Long = {System.currentTimeMillis()}
}