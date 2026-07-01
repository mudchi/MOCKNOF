package com.mocknof.app.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.gson.Gson
import com.mocknof.app.model.NotificationTemplate
import com.mocknof.app.utils.NotificationHelper

/**
 * Receives alarms for delayed / repeating notifications.
 */
class NotificationAlarmReceiver : BroadcastReceiver() {

    companion object {
        const val EXTRA_TEMPLATE_JSON = "template_json"
        const val EXTRA_REPEAT_LEFT   = "repeat_left"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val json = intent.getStringExtra(EXTRA_TEMPLATE_JSON) ?: return
        val template = try {
            Gson().fromJson(json, NotificationTemplate::class.java)
        } catch (_: Exception) { return }

        NotificationHelper.fireNow(context, template)

        // Handle repeating
        val repeatLeft = intent.getIntExtra(EXTRA_REPEAT_LEFT, template.repeatCount - 1)
        if (repeatLeft > 0 && template.repeatIntervalSeconds > 0) {
            scheduleNext(context, template, repeatLeft - 1)
        }
    }

    private fun scheduleNext(context: Context, template: NotificationTemplate, repeatLeft: Int) {
        val nextIntent = Intent(context, NotificationAlarmReceiver::class.java).apply {
            putExtra(EXTRA_TEMPLATE_JSON, Gson().toJson(template))
            putExtra(EXTRA_REPEAT_LEFT, repeatLeft)
        }
        val pi = PendingIntent.getBroadcast(
            context,
            NotificationHelper.nextNotificationId(),
            nextIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val triggerAt = android.os.SystemClock.elapsedRealtime() +
                template.repeatIntervalSeconds * 1000L
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAt, pi)
    }
}
