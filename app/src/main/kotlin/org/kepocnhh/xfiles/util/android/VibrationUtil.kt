package org.kepocnhh.xfiles.util.android

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlin.time.Duration

@Suppress("Deprecation")
internal fun Context.getDefaultVibrator(): Vibrator {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
        return getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }
    val manager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
    return manager.defaultVibrator
}

@Suppress("Deprecation")
internal fun Vibrator.vibrate(duration: Duration) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrate(VibrationEffect.createOneShot(duration.inWholeMilliseconds, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        vibrate(duration.inWholeMilliseconds)
    }
}
