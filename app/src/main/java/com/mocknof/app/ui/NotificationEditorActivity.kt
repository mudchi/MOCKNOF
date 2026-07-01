package com.mocknof.app.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.mocknof.app.R
import com.mocknof.app.databinding.ActivityEditorBinding
import com.mocknof.app.model.ActionType
import com.mocknof.app.model.NotificationAction
import com.mocknof.app.model.SmallIconType
import com.mocknof.app.ui.adapter.ActionEditorAdapter

class NotificationEditorActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_TEMPLATE_ID = "template_id"
    }

    private lateinit var binding: ActivityEditorBinding
    private val vm: EditorViewModel by viewModels()
    private lateinit var actionAdapter: ActionEditorAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val templateId = intent.getStringExtra(EXTRA_TEMPLATE_ID)
        vm.loadTemplate(templateId)
        supportActionBar?.title = if (templateId == null) "New Template" else "Edit Template"

        setupSpinners()
        setupActionsList()
        observeViewModel()
        setupButtons()
    }

    private fun setupSpinners() {
        // Importance
        val importanceLabels = vm.importanceOptions.map { it.second }
        val importanceAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, importanceLabels)
        importanceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerImportance.adapter = importanceAdapter

        // Category
        val categoryLabels = vm.categoryOptions.map { it.second }
        val catAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoryLabels)
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategory.adapter = catAdapter

        // Small icon
        val iconLabels = SmallIconType.values().map { it.name }
        val iconAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, iconLabels)
        iconAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerIcon.adapter = iconAdapter
    }

    private fun setupActionsList() {
        actionAdapter = ActionEditorAdapter(
            onRemove = { index -> vm.removeAction(index) }
        )
        binding.rvActions.adapter = actionAdapter
    }

    private fun observeViewModel() {
        vm.template.observe(this) { t ->
            if (!binding.etTitle.hasFocus()) binding.etTitle.setText(t.title)
            if (!binding.etBody.hasFocus()) binding.etBody.setText(t.body)
            if (!binding.etBigText.hasFocus()) binding.etBigText.setText(t.bigText)
            if (!binding.etAppName.hasFocus()) binding.etAppName.setText(t.appName)
            if (!binding.etPackageName.hasFocus()) binding.etPackageName.setText(t.packageName)
            if (!binding.etChannelId.hasFocus()) binding.etChannelId.setText(t.channelId)
            if (!binding.etChannelName.hasFocus()) binding.etChannelName.setText(t.channelName)
            if (!binding.etDelay.hasFocus()) binding.etDelay.setText(t.delaySeconds.toString())
            if (!binding.etRepeatCount.hasFocus()) binding.etRepeatCount.setText(t.repeatCount.toString())
            if (!binding.etRepeatInterval.hasFocus()) binding.etRepeatInterval.setText(t.repeatIntervalSeconds.toString())
            if (!binding.etBadge.hasFocus()) binding.etBadge.setText(t.badgeCount.toString())
            if (!binding.etColor.hasFocus()) binding.etColor.setText(t.colorHex)
            if (!binding.etGroup.hasFocus()) binding.etGroup.setText(t.groupKey)
            if (!binding.etProgress.hasFocus()) binding.etProgress.setText(t.progress.toString())

            binding.switchOngoing.isChecked = t.ongoing
            binding.switchAutoCancel.isChecked = t.autoCancel
            binding.switchVibrate.isChecked = t.vibrate
            binding.switchSound.isChecked = t.sound

            val impIdx = vm.importanceOptions.indexOfFirst { it.first == t.importance }
            if (impIdx >= 0) binding.spinnerImportance.setSelection(impIdx)

            val catIdx = vm.categoryOptions.indexOfFirst { it.first == t.category }
            if (catIdx >= 0) binding.spinnerCategory.setSelection(catIdx)

            val iconIdx = SmallIconType.values().indexOf(t.smallIconType)
            if (iconIdx >= 0) binding.spinnerIcon.setSelection(iconIdx)

            actionAdapter.submitList(t.actions.toMutableList())
        }
    }

    private fun setupButtons() {
        binding.btnAddAction.setOnClickListener { showAddActionDialog() }

        binding.btnSave.setOnClickListener {
            collectFields()
            val saved = vm.saveAndClose()
            if (saved != null) {
                Toast.makeText(this, "Saved \"${saved.title}\"", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK)
                finish()
            }
        }

        binding.btnSaveAndFire.setOnClickListener {
            collectFields()
            val saved = vm.saveAndClose()
            if (saved != null) {
                val resultIntent = Intent().putExtra("fire_template_id", saved.id)
                setResult(RESULT_OK, resultIntent)
                finish()
            }
        }
    }

    private fun collectFields() {
        vm.updateTemplate {
            title = binding.etTitle.text.toString().ifBlank { "Test Notification" }
            body = binding.etBody.text.toString()
            bigText = binding.etBigText.text.toString()
            appName = binding.etAppName.text.toString().ifBlank { "Mock App" }
            packageName = binding.etPackageName.text.toString().ifBlank { "com.mock.app" }
            channelId = binding.etChannelId.text.toString().ifBlank { "default" }
            channelName = binding.etChannelName.text.toString().ifBlank { "Default Channel" }
            delaySeconds = binding.etDelay.text.toString().toIntOrNull() ?: 0
            repeatCount = binding.etRepeatCount.text.toString().toIntOrNull()?.coerceAtLeast(1) ?: 1
            repeatIntervalSeconds = binding.etRepeatInterval.text.toString().toIntOrNull() ?: 5
            badgeCount = binding.etBadge.text.toString().toIntOrNull() ?: 0
            colorHex = binding.etColor.text.toString()
            groupKey = binding.etGroup.text.toString()
            progress = binding.etProgress.text.toString().toIntOrNull() ?: -1
            ongoing = binding.switchOngoing.isChecked
            autoCancel = binding.switchAutoCancel.isChecked
            vibrate = binding.switchVibrate.isChecked
            sound = binding.switchSound.isChecked
            importance = vm.importanceOptions[binding.spinnerImportance.selectedItemPosition].first
            category = vm.categoryOptions[binding.spinnerCategory.selectedItemPosition].first
            smallIconType = SmallIconType.values()[binding.spinnerIcon.selectedItemPosition]
        }
    }

    private fun showAddActionDialog() {
        val types = ActionType.values().map { it.name }.toTypedArray()
        var selectedType = 0
        var labelInput = ""

        val view = layoutInflater.inflate(R.layout.dialog_add_action, null)
        val etLabel = view.findViewById<android.widget.EditText>(R.id.et_action_label)
        val spinner = view.findViewById<android.widget.Spinner>(R.id.spinner_action_type)
        spinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, types)

        AlertDialog.Builder(this)
            .setTitle("Add Action Button")
            .setView(view)
            .setPositiveButton("Add") { _, _ ->
                labelInput = etLabel.text.toString().ifBlank { "Action" }
                selectedType = spinner.selectedItemPosition
                vm.addAction(
                    NotificationAction(
                        label = labelInput,
                        type = ActionType.values()[selectedType]
                    )
                )
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) { finish(); return true }
        return super.onOptionsItemSelected(item)
    }
}
