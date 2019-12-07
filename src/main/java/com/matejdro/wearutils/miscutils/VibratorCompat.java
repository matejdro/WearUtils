package com.matejdro.wearutils.miscutils;

import android.Manifest;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.annotation.RequiresPermission;

public class VibratorCompat {
    @RequiresPermission(Manifest.permission.VIBRATE)
    public static void vibrate(Vibrator vibrator, long timeMilliseconds) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(timeMilliseconds, -1));
        } else {
            vibrator.vibrate(timeMilliseconds);
        }
    }
}
