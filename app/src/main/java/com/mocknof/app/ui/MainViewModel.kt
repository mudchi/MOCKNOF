package com.mocknof.app.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mocknof.app.model.NotificationHistory
import com.mocknof.app.model.NotificationTemplate
import com.mocknof.app.utils.NotificationHelper
import com.mocknof.app.utils.PrefsStorage
import java.util.UUID

class MainViewModel(app: Application) : AndroidViewModel(app) {

    private val ctx: Context get() = getApplication()

    private val _templates = MutableLiveData<List<NotificationTemplate>>()
    val templates: LiveData<List<NotificationTemplate>> = _templates

    private val _history = MutableLiveData<List<NotificationHistory>>()
    val history: LiveData<List<NotificationHistory>> = _history

    private val _fireEvent = MutableLiveData<FireResult>()
    val fireEvent: LiveData<FireResult> = _fireEvent

    data class FireResult(val success: Boolean, val message: String)

    init {
        loadTemplates()
        loadHistory()
    }

    fun loadTemplates() {
        _templates.value = PrefsStorage.loadTemplates(ctx)
    }

    fun loadHistory() {
        _history.value = PrefsStorage.loadHistory(ctx)
    }

    fun saveTemplate(template: NotificationTemplate) {
        PrefsStorage.saveTemplate(ctx, template)
        loadTemplates()
    }

    fun deleteTemplate(id: String) {
        PrefsStorage.deleteTemplate(ctx, id)
        loadTemplates()
    }

    fun clearHistory() {
        PrefsStorage.clearHistory(ctx)
        loadHistory()
    }

    /**
     * Fire a single shot or schedule based on delaySeconds.
     */
    fun fireTemplate(template: NotificationTemplate) {
        try {
            if (template.delaySeconds > 0) {
                NotificationHelper.scheduleNotification(ctx, template)
                _fireEvent.value = FireResult(
                    true,
                    "⏰ Scheduled in ${template.delaySeconds}s — \"${template.title}\""
                )
            } else {
                for (i in 1..template.repeatCount) {
                    val notifId = NotificationHelper.fireNow(ctx, template)
                    val hist = NotificationHistory(
                        id = UUID.randomUUID().toString(),
                        templateId = template.id,
                        appName = template.appName,
                        title = template.title,
                        body = template.body,
                        notificationId = notifId
                    )
                    PrefsStorage.addHistory(ctx, hist)
                    if (i < template.repeatCount) {
                        Thread.sleep(template.repeatIntervalSeconds * 1000L)
                    }
                }
                loadHistory()
                _fireEvent.value = FireResult(
                    true,
                    "🚀 Sent ×${template.repeatCount} — \"${template.title}\""
                )
            }
        } catch (e: SecurityException) {
            _fireEvent.value = FireResult(false, "❌ Permission denied — POST_NOTIFICATIONS not granted")
        } catch (e: Exception) {
            _fireEvent.value = FireResult(false, "❌ Error: ${e.message}")
        }
    }

    fun cancelAll() {
        NotificationHelper.cancelAll(ctx)
        _fireEvent.value = FireResult(true, "🗑 All notifications cancelled")
    }
}
