package org.kepocnhh.xfiles.module.observer

import android.app.Service
import android.content.Intent
import android.os.IBinder

internal class ObserverService : Service() {
    private fun onStartCommand(intent: Intent) {
        // todo
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) onStartCommand(intent)
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
