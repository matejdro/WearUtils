package com.matejdro.wearutils.miscutils

import android.annotation.TargetApi
import android.os.Build
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

/**
 * Class that provides mockable current time.
 */
object TimeProvider {
    fun currentTimeMillis() : Long = currentTimeMillisOverride()
    fun currentDate() : Date = Date(currentTimeMillis())

    @TargetApi(Build.VERSION_CODES.O)
    fun currentLocalDate(): LocalDate = Instant.ofEpochMilli(currentTimeMillis())
            .atZone(ZoneId.systemDefault())
            .toLocalDate()

    var currentTimeMillisOverride : () -> Long = {System.currentTimeMillis()}
}