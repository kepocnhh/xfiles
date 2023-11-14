package org.kepocnhh.xfiles.util.android

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import org.kepocnhh.xfiles.R

internal object ForegroundUtil {
    private val CHANNEL_ID = "${this::class.java.name}:CHANNEL"
    private const val CHANNEL_NAME = "xfiles:foreground"
    val ACTION_START_FOREGROUND = "${this::class.java.name}:ACTION_START_FOREGROUND"
    val ACTION_STOP_FOREGROUND = "${this::class.java.name}:ACTION_STOP_FOREGROUND"

    private fun NotificationManager.checkChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val channel: NotificationChannel? = getNotificationChannel(CHANNEL_ID)
        if (channel == null) {
            createNotificationChannel(
                NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH,
                ),
            )
        }
    }

    fun notify(context: Context, id: Int, notification: Notification) {
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.checkChannel()
        notificationManager.notify(id, notification)
    }

    private fun Context.builder(title: String): NotificationCompat.Builder {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setSmallIcon(R.drawable.key)
    }

    fun buildNotification(
        context: Context,
        title: String,
    ): Notification {
        return context
            .builder(title)
            .build()
    }

    fun <T : Service> startForeground(
        context: Context,
        type: Class<T>,
        notificationId: Int,
        notification: Notification,
    ) {
        val intent = Intent(context, type)
        intent.action = ACTION_START_FOREGROUND
        intent.putExtra("notificationId", notificationId)
        intent.putExtra("notification", notification)
        context.startService(intent)
    }

    fun <T : Service> stopForeground(
        context: Context,
        type: Class<T>,
        notificationBehavior: Int,
    ) {
        when (notificationBehavior) {
            Service.STOP_FOREGROUND_REMOVE -> {
                // noop
            }
            else -> TODO()
        }
        val intent = Intent(context, type)
        intent.action = ACTION_STOP_FOREGROUND
        intent.putExtra("notificationBehavior", notificationBehavior)
        context.startService(intent)
    }
}

internal inline fun <reified T : Service> Context.notifyAndStartForeground(id: Int, notification: Notification) {
    ForegroundUtil.notify(this, id = id, notification = notification)
    ForegroundUtil.startForeground(
        context = this,
        type = T::class.java,
        notificationId = id,
        notification = notification,
    )
}

internal inline fun <reified T : Service> Context.notifyAndStartForeground(id: Int, title: String) {
    val notification = ForegroundUtil.buildNotification(
        context = this,
        title = title,
    )
    notifyAndStartForeground(id = id, notification = notification)
}

internal inline fun <reified T : Service> Context.stopForeground(
    notificationBehavior: Int = Service.STOP_FOREGROUND_REMOVE,
) {
    ForegroundUtil.stopForeground(
        context = this,
        type = T::class.java,
        notificationBehavior = notificationBehavior,
    )
}
