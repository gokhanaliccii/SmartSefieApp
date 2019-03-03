package com.onfido.android.techtask.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator

/**
 * Created by gokhan.alici on 03.03.2019
 */

fun vibrate(context: Context, vibrationDuration: Long = 60) {
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    vibrateWith(vibrator, vibrationDuration)
}

fun vibrateWith(vibrator: Vibrator, vibrationDuration: Long = 60) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(
            VibrationEffect.createOneShot(
                vibrationDuration,
                VibrationEffect.DEFAULT_AMPLITUDE
            )
        ) // New vibrate method for API Level 26 or higher
    } else {
        vibrator.vibrate(vibrationDuration) // Vibrate method for below API Level 26
    }
}