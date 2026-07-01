package com.mocknof.app.model

import java.util.UUID

/**
 * Represents a single configurable notification mock.
 */
data class NotificationTemplate(
    val id: String = UUID.randomUUID().toString(),
    var appName: String = "Mock App",           // sender app name shown in status bar
    var packageName: String = "com.mock.app",   // fake package (used for channel grouping)
    var channelId: String = "default",
    var channelName: String = "Default Channel",
    var importance: Int = 3,                    // NotificationManager.IMPORTANCE_DEFAULT
    var title: String = "Test Notification",
    var body: String = "This is a mock notification body",
    var bigText: String = "",                   // if non-empty, use BigTextStyle
    var imageUrl: String = "",                  // local URI or empty
    var badgeCount: Int = 0,
    var ongoing: Boolean = false,
    var autoCancel: Boolean = true,
    var vibrate: Boolean = true,
    var sound: Boolean = true,
    var actions: List<NotificationAction> = emptyList(),
    var delaySeconds: Int = 0,                  // 0 = fire immediately
    var repeatCount: Int = 1,                   // how many times to fire
    var repeatIntervalSeconds: Int = 5,
    var groupKey: String = "",
    var sortKey: String = "",
    var category: String = "",                  // NotificationCompat.CATEGORY_*
    var priority: Int = 0,                      // NotificationCompat.PRIORITY_DEFAULT
    var colorHex: String = "",                  // accent color e.g. "#FF5722"
    var smallIconType: SmallIconType = SmallIconType.DEFAULT,
    var progress: Int = -1,                     // -1 = no progress bar; 0..100 = determinate; -2 = indeterminate
    var savedAt: Long = System.currentTimeMillis()
)

data class NotificationAction(
    val id: String = UUID.randomUUID().toString(),
    var label: String = "Action",
    var type: ActionType = ActionType.DISMISS
)

enum class ActionType { DISMISS, REPLY, CUSTOM }

enum class SmallIconType {
    DEFAULT,    // ic_notification (our app icon)
    EMAIL,
    CHAT,
    ALERT,
    DOWNLOAD,
    UPLOAD,
    MUSIC,
    CALL
}
