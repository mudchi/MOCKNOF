package com.mocknof.app.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mocknof.app.model.NotificationHistory
import com.mocknof.app.model.NotificationTemplate

/**
 * Lightweight SharedPreferences-backed storage for templates and history.
 */
object PrefsStorage {

    private const val PREFS_TEMPLATES = "mocknof_templates"
    private const val PREFS_HISTORY   = "mocknof_history"
    private const val KEY_TEMPLATES   = "templates"
    private const val KEY_HISTORY     = "history"
    private const val MAX_HISTORY     = 200

    private val gson = Gson()

    // ── Templates ──────────────────────────────────────────────

    fun saveTemplate(context: Context, template: NotificationTemplate) {
        val list = loadTemplates(context).toMutableList()
        val idx = list.indexOfFirst { it.id == template.id }
        if (idx >= 0) list[idx] = template else list.add(0, template)
        persist(context, PREFS_TEMPLATES, KEY_TEMPLATES, list)
    }

    fun loadTemplates(context: Context): List<NotificationTemplate> {
        val json = context.getSharedPreferences(PREFS_TEMPLATES, Context.MODE_PRIVATE)
            .getString(KEY_TEMPLATES, null) ?: return emptyList()
        val type = object : TypeToken<List<NotificationTemplate>>() {}.type
        return try { gson.fromJson(json, type) } catch (_: Exception) { emptyList() }
    }

    fun deleteTemplate(context: Context, id: String) {
        val list = loadTemplates(context).toMutableList()
        list.removeAll { it.id == id }
        persist(context, PREFS_TEMPLATES, KEY_TEMPLATES, list)
    }

    // ── History ────────────────────────────────────────────────

    fun addHistory(context: Context, history: NotificationHistory) {
        val list = loadHistory(context).toMutableList()
        list.add(0, history)
        if (list.size > MAX_HISTORY) list.subList(MAX_HISTORY, list.size).clear()
        persist(context, PREFS_HISTORY, KEY_HISTORY, list)
    }

    fun loadHistory(context: Context): List<NotificationHistory> {
        val json = context.getSharedPreferences(PREFS_HISTORY, Context.MODE_PRIVATE)
            .getString(KEY_HISTORY, null) ?: return emptyList()
        val type = object : TypeToken<List<NotificationHistory>>() {}.type
        return try { gson.fromJson(json, type) } catch (_: Exception) { emptyList() }
    }

    fun clearHistory(context: Context) {
        context.getSharedPreferences(PREFS_HISTORY, Context.MODE_PRIVATE)
            .edit().remove(KEY_HISTORY).apply()
    }

    // ── Internal ───────────────────────────────────────────────

    private fun <T> persist(context: Context, prefsName: String, key: String, list: T) {
        context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
            .edit().putString(key, gson.toJson(list)).apply()
    }
}
