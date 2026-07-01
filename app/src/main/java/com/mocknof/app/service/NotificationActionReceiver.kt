package com.mocknof.app.service

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.app.RemoteInput

/**
 * Handles interactive notification action button taps.
 */
class NotificationActionReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_DISMISS     = "com.mocknof.app.ACTION_DISMISS"
        const val ACTION_REPLY       = "com.mocknof.app.ACTION_REPLY"
        const val ACTION_CUSTOM      = "com.mocknof.app.ACTION_CUSTOM"
        const val EXTRA_NOTIF_ID     = "notif_id"
        const val EXTRA_ACTION_LABEL = "action_label"
        const val KEY_REPLY_TEXT     = "reply_text"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val notifId = intent.getIntExtra(EXTRA_NOTIF_ID, -1)
        val label   = intent.getStringExtra(EXTRA_ACTION_LABEL) ?: ""

        when (intent.action) {
            ACTION_DISMISS -> {
                if (notifId != -1) {
                    val nm = context.getSystemService(Context.NOTIFICATION_SERVICE)
                            as NotificationManager
                    nm.cancel(notifId)
                }
                Toast.makeText(context, "✕ Dismissed: $label", Toast.LENGTH_SHORT).show()
            }

            ACTION_REPLY -> {
                val bundle = RemoteInput.getResultsFromIntent(intent)
                val replyText = bundle?.getCharSequence(KEY_REPLY_TEXT)?.toString() ?: "(empty)"
                Toast.makeText(context, "↩ Reply: $replyText", Toast.LENGTH_LONG).show()
                // Cancel the notification so the inline-reply spinner disappears
                if (notifId != -1) {
                    val nm = context.getSystemService(Context.NOTIFICATION_SERVICE)
                            as NotificationManager
                    nm.cancel(notifId)
                }
            }

            ACTION_CUSTOM -> {
                Toast.makeText(context, "⚡ Action: $label", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
