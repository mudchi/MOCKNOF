package com.mocknof.app.utils

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.SystemClock
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.RemoteInput
import com.mocknof.app.R
import com.mocknof.app.model.ActionType
import com.mocknof.app.model.NotificationTemplate
import com.mocknof.app.model.SmallIconType
import com.mocknof.app.service.NotificationActionReceiver
import com.mocknof.app.service.NotificationAlarmReceiver
import java.util.concurrent.atomic.AtomicInteger

object NotificationHelper {

    private val idCounter = AtomicInteger(1000)

    fun nextNotificationId(): Int = idCounter.incrementAndGet()

    // ──────────────────────────────────────────────────────────
    // Channel management
    // ──────────────────────────────────────────────────────────

    /**
     * Ensures a notification channel exists for the given template.
     * Channel ID is namespaced as "<packageName>_<channelId>" so each
     * fake app gets its own isolated channel group.
     */
    fun ensureChannel(context: Context, template: NotificationTemplate): String {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "${template.packageName}_${template.channelId}"

        if (nm.getNotificationChannel(channelId) == null) {
            val channel = NotificationChannel(
                channelId,
                template.channelName,
                template.importance
            ).apply {
                description = "Mock channel for ${template.appName}"
                enableVibration(template.vibrate)
                if (!template.sound) setSound(null, null)
            }
            nm.createNotificationChannel(channel)
        }
        return channelId
    }

    fun deleteChannel(context: Context, packageName: String, channelId: String) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.deleteNotificationChannel("${packageName}_${channelId}")
    }

    fun listChannels(context: Context): List<NotificationChannel> {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        return nm.notificationChannels
    }

    // ──────────────────────────────────────────────────────────
    // Fire notification
    // ──────────────────────────────────────────────────────────

    /**
     * Build and post a notification immediately from [template].
     * Returns the notification ID used.
     */
    fun fireNow(context: Context, template: NotificationTemplate): Int {
        val channelId = ensureChannel(context, template)
        val notifId = nextNotificationId()

        val builder = buildNotification(context, template, channelId, notifId)

        try {
            NotificationManagerCompat.from(context).notify(notifId, builder.build())
        } catch (e: SecurityException) {
            // POST_NOTIFICATIONS permission not granted — caller should handle UI
            throw e
        }
        return notifId
    }

    /**
     * Schedule notification via AlarmManager after [delaySeconds].
     */
    fun scheduleNotification(context: Context, template: NotificationTemplate) {
        val intent = Intent(context, NotificationAlarmReceiver::class.java).apply {
            putExtra(NotificationAlarmReceiver.EXTRA_TEMPLATE_JSON, templateToJson(template))
        }
        val pi = PendingIntent.getBroadcast(
            context,
            nextNotificationId(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val triggerAt = SystemClock.elapsedRealtime() + template.delaySeconds * 1000L
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && am.canScheduleExactAlarms()) {
            am.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAt, pi)
        } else {
            am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAt, pi)
        }
    }

    // ──────────────────────────────────────────────────────────
    // Builder
    // ──────────────────────────────────────────────────────────

    fun buildNotification(
        context: Context,
        template: NotificationTemplate,
        channelId: String,
        notifId: Int
    ): NotificationCompat.Builder {
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(smallIconRes(template.smallIconType))
            .setContentTitle(template.title)
            .setContentText(template.body)
            .setSubText(template.appName)
            .setOngoing(template.ongoing)
            .setAutoCancel(template.autoCancel)
            .setPriority(template.priority)
            .setNumber(if (template.badgeCount > 0) template.badgeCount else 0)

        // Big text style
        if (template.bigText.isNotBlank()) {
            builder.setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(template.bigText)
                    .setBigContentTitle(template.title)
            )
        }

        // Accent color
        if (template.colorHex.isNotBlank()) {
            try {
                builder.color = Color.parseColor(template.colorHex)
            } catch (_: Exception) { }
        }

        // Category
        if (template.category.isNotBlank()) {
            builder.setCategory(template.category)
        }

        // Group
        if (template.groupKey.isNotBlank()) {
            builder.setGroup(template.groupKey)
        }
        if (template.sortKey.isNotBlank()) {
            builder.setSortKey(template.sortKey)
        }

        // Progress bar
        when {
            template.progress == -2 -> builder.setProgress(0, 0, true)
            template.progress in 0..100 -> builder.setProgress(100, template.progress, false)
        }

        // Action buttons
        template.actions.forEachIndexed { index, action ->
            val actionIntent = Intent(context, NotificationActionReceiver::class.java).apply {
                this.action = when (action.type) {
                    ActionType.DISMISS -> NotificationActionReceiver.ACTION_DISMISS
                    ActionType.REPLY   -> NotificationActionReceiver.ACTION_REPLY
                    ActionType.CUSTOM  -> NotificationActionReceiver.ACTION_CUSTOM
                }
                putExtra(NotificationActionReceiver.EXTRA_NOTIF_ID, notifId)
                putExtra(NotificationActionReceiver.EXTRA_ACTION_LABEL, action.label)
            }
            val actionPi = PendingIntent.getBroadcast(
                context,
                notifId * 10 + index,
                actionIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            if (action.type == ActionType.REPLY) {
                val remoteInput = RemoteInput.Builder(NotificationActionReceiver.KEY_REPLY_TEXT)
                    .setLabel(action.label)
                    .build()
                val replyAction = NotificationCompat.Action.Builder(
                    R.drawable.ic_reply,
                    action.label,
                    actionPi
                ).addRemoteInput(remoteInput).build()
                builder.addAction(replyAction)
            } else {
                builder.addAction(
                    NotificationCompat.Action(
                        R.drawable.ic_action,
                        action.label,
                        actionPi
                    )
                )
            }
        }

        return builder
    }

    // ──────────────────────────────────────────────────────────
    // Helpers
    // ──────────────────────────────────────────────────────────

    private fun smallIconRes(type: SmallIconType): Int = when (type) {
        SmallIconType.EMAIL    -> R.drawable.ic_notif_email
        SmallIconType.CHAT     -> R.drawable.ic_notif_chat
        SmallIconType.ALERT    -> R.drawable.ic_notif_alert
        SmallIconType.DOWNLOAD -> R.drawable.ic_notif_download
        SmallIconType.UPLOAD   -> R.drawable.ic_notif_upload
        SmallIconType.MUSIC    -> R.drawable.ic_notif_music
        SmallIconType.CALL     -> R.drawable.ic_notif_call
        else                   -> R.drawable.ic_notification
    }

    private fun templateToJson(template: NotificationTemplate): String {
        // Simple manual JSON for the key fields used in alarm scheduling
        return com.google.gson.Gson().toJson(template)
    }

    fun cancelNotification(context: Context, notifId: Int) {
        NotificationManagerCompat.from(context).cancel(notifId)
    }

    fun cancelAll(context: Context) {
        NotificationManagerCompat.from(context).cancelAll()
    }
}
