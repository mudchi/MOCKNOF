package com.mocknof.app.ui

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import com.mocknof.app.databinding.ActivityPresetListBinding
import com.mocknof.app.model.NotificationAction
import com.mocknof.app.model.NotificationTemplate
import com.mocknof.app.ui.adapter.TemplateAdapter
import com.mocknof.app.utils.PrefsStorage

/**
 * Shows built-in presets the user can fire or clone into custom templates.
 */
class PresetListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPresetListBinding

    private val vm: MainViewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPresetListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Built-in Presets"

        val presets = buildPresets()
        val adapter = TemplateAdapter(
            onFire = { vm.fireTemplate(it) },
            onEdit = { template ->
                // Clone as custom template
                val clone = template.copy(id = java.util.UUID.randomUUID().toString())
                PrefsStorage.saveTemplate(this, clone)
                Toast.makeText(this, "Cloned \"${clone.title}\" to My Templates", Toast.LENGTH_SHORT).show()
            },
            onDelete = {}
        )
        binding.rvPresets.apply {
            layoutManager = GridLayoutManager(this@PresetListActivity, 1)
            this.adapter = adapter
            addItemDecoration(DividerItemDecoration(context, GridLayoutManager.VERTICAL))
        }
        adapter.submitList(presets)

        vm.fireEvent.observe(this) { result ->
            Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun buildPresets(): List<NotificationTemplate> = listOf(
        NotificationTemplate(
            appName = "LINE",
            packageName = "jp.naver.line.android",
            channelId = "line_message",
            channelName = "Messages",
            importance = 4,
            title = "John Doe",
            body = "Hey! Are you free tomorrow? 😊",
            category = "msg",
            smallIconType = com.mocknof.app.model.SmallIconType.CHAT
        ),
        NotificationTemplate(
            appName = "Gmail",
            packageName = "com.google.android.gm",
            channelId = "gmail_new",
            channelName = "New Mail",
            importance = 3,
            title = "New email from boss@company.com",
            body = "Re: Q4 Budget Review — Please review the attached...",
            bigText = "Re: Q4 Budget Review\n\nPlease review the attached spreadsheet before the meeting on Friday. We need to finalize the numbers.\n\nBest,\nBoss",
            category = "email",
            smallIconType = com.mocknof.app.model.SmallIconType.EMAIL
        ),
        NotificationTemplate(
            appName = "YouTube",
            packageName = "com.google.android.youtube",
            channelId = "yt_live",
            channelName = "Live streams",
            importance = 3,
            title = "TechChannel is live!",
            body = "Google I/O 2024 Keynote — Live now"
        ),
        NotificationTemplate(
            appName = "Grab",
            packageName = "com.grabtaxi.passenger",
            channelId = "grab_order",
            channelName = "Order Updates",
            importance = 4,
            title = "Your driver is arriving",
            body = "Somchai is 2 minutes away · Toyota Camry · กก-1234",
            actions = listOf(
                NotificationAction(label = "Contact Driver", type = com.mocknof.app.model.ActionType.CUSTOM),
                NotificationAction(label = "Cancel", type = com.mocknof.app.model.ActionType.DISMISS)
            )
        ),
        NotificationTemplate(
            appName = "Bank App",
            packageName = "com.bank.mobile",
            channelId = "bank_txn",
            channelName = "Transactions",
            importance = 4,
            title = "Payment Received ✓",
            body = "฿12,500.00 received from ACME Co.",
            colorHex = "#2E7D32",
            smallIconType = com.mocknof.app.model.SmallIconType.ALERT
        ),
        NotificationTemplate(
            appName = "Download Manager",
            packageName = "com.android.providers.downloads",
            channelId = "download",
            channelName = "Downloads",
            importance = 2,
            title = "Downloading update.apk",
            body = "45% — 23 MB of 51 MB",
            ongoing = true,
            autoCancel = false,
            progress = 45,
            smallIconType = com.mocknof.app.model.SmallIconType.DOWNLOAD
        ),
        NotificationTemplate(
            appName = "Incoming Call",
            packageName = "com.android.phone",
            channelId = "call",
            channelName = "Calls",
            importance = 5,
            title = "Incoming call",
            body = "+66 81 234 5678",
            category = "call",
            ongoing = true,
            autoCancel = false,
            smallIconType = com.mocknof.app.model.SmallIconType.CALL,
            actions = listOf(
                NotificationAction(label = "Answer", type = com.mocknof.app.model.ActionType.CUSTOM),
                NotificationAction(label = "Decline", type = com.mocknof.app.model.ActionType.DISMISS)
            )
        ),
        NotificationTemplate(
            appName = "Repeat Stress Test",
            packageName = "com.mocknof.stress",
            channelId = "stress",
            channelName = "Stress Test",
            importance = 3,
            title = "Stress notification",
            body = "This fires 5 times every 2 seconds",
            repeatCount = 5,
            repeatIntervalSeconds = 2
        )
    )

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) { finish(); return true }
        return super.onOptionsItemSelected(item)
    }
}
