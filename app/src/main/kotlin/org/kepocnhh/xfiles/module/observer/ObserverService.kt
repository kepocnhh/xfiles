package org.kepocnhh.xfiles.module.observer

import android.app.Notification
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.kepocnhh.xfiles.util.android.ForegroundUtil
import java.util.concurrent.atomic.AtomicInteger

internal class ObserverService : Service() {
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    private fun onStartTimer() {

    }

    private fun onStartCommand(intent: Intent) {
        val intentAction = intent.action ?: TODO("No intent action!")
        if (intentAction.isEmpty()) TODO("Intent action is empty!")
        when (Action.values().firstOrNull { it.name == intentAction }) {
            Action.START_TIMER -> onStartTimer()
            null -> {
                when (intentAction) {
                    ForegroundUtil.ACTION_START_FOREGROUND -> {
                        val notificationId = intent.getIntExtra("notificationId", -1)
                        val notification = intent.getParcelableExtra<Notification>("notification")
                        startForeground(notificationId, notification)
                    }
                    ForegroundUtil.ACTION_STOP_FOREGROUND -> {
                        val notificationBehavior = intent.getIntExtra("notificationBehavior", -1)
                        when (notificationBehavior) {
                            STOP_FOREGROUND_REMOVE -> {
                                // noop
                            }
                            else -> TODO("Behavior \"$notificationBehavior\" unsupported!")
                        }
                        stopForeground(notificationBehavior)
                    }
                    else -> TODO("Unknown action: $intentAction!")
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) onStartCommand(intent)
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    enum class Action {
        START_TIMER
    }

    companion object {
        private val indexes = AtomicInteger(0)
        val TIMER_NOTIFICATION_ID = indexes.incrementAndGet()

        private fun intent(context: Context, action: Action): Intent {
            val intent = Intent(context, ObserverService::class.java)
            intent.action = action.name
            return intent
        }

        fun startTimer(context: Context) {
            val intent = intent(context, Action.START_TIMER)
            context.startService(intent)
        }
    }
}
