package com.mocknof.app.ui

import android.app.Application
import android.content.Context
import android.app.NotificationManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mocknof.app.model.NotificationTemplate
import com.mocknof.app.model.NotificationAction
import com.mocknof.app.model.ActionType
import com.mocknof.app.utils.NotificationHelper
import com.mocknof.app.utils.PrefsStorage

class EditorViewModel(app: Application) : AndroidViewModel(app) {

    private val ctx: Context get() = getApplication()

    private val _template = MutableLiveData(NotificationTemplate())
    val template: LiveData<NotificationTemplate> = _template

    fun loadTemplate(id: String?) {
        if (id == null) {
            _template.value = NotificationTemplate()
            return
        }
        val found = PrefsStorage.loadTemplates(ctx).find { it.id == id }
        _template.value = found ?: NotificationTemplate()
    }

    fun updateTemplate(block: NotificationTemplate.() -> Unit) {
        _template.value = _template.value?.copy()?.also(block)
    }

    fun addAction(action: NotificationAction) {
        updateTemplate {
            actions = actions + action
        }
    }

    fun removeAction(index: Int) {
        updateTemplate {
            actions = actions.toMutableList().also { it.removeAt(index) }
        }
    }

    fun saveAndClose(): NotificationTemplate? {
        val t = _template.value ?: return null
        PrefsStorage.saveTemplate(ctx, t)
        NotificationHelper.ensureChannel(ctx, t)
        return t
    }

    val importanceOptions = listOf(
        NotificationManager.IMPORTANCE_MIN to "Min",
        NotificationManager.IMPORTANCE_LOW to "Low",
        NotificationManager.IMPORTANCE_DEFAULT to "Default",
        NotificationManager.IMPORTANCE_HIGH to "High",
        NotificationManager.IMPORTANCE_MAX to "Max"
    )

    val categoryOptions = listOf(
        "" to "None",
        "msg" to "Message",
        "email" to "Email",
        "alarm" to "Alarm",
        "call" to "Call",
        "promo" to "Promo",
        "social" to "Social",
        "event" to "Event",
        "transport" to "Transport",
        "service" to "Service",
        "status" to "Status"
    )
}
