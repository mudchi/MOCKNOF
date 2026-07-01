package com.mocknof.app.model

/**
 * A saved record of a notification that was fired.
 */
data class NotificationHistory(
    val id: String,
    val templateId: String,
    val appName: String,
    val title: String,
    val body: String,
    val firedAt: Long = System.currentTimeMillis(),
    val notificationId: Int = 0
)
