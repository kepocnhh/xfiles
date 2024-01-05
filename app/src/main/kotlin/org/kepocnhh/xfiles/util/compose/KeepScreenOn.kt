package org.kepocnhh.xfiles.util.compose

import android.app.Activity
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import org.kepocnhh.xfiles.util.android.findActivity

@Composable
internal fun KeepScreenOn() {
    val context = LocalContext.current
    val flag = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
    DisposableEffect(Unit) {
        val window = context.findActivity<Activity>()?.window
        window?.addFlags(flag)
        onDispose {
            window?.clearFlags(flag)
        }
    }
}
