package com.mocknof.app.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.mocknof.app.R
import com.mocknof.app.databinding.ActivityMainBinding
import com.mocknof.app.model.NotificationTemplate
import com.mocknof.app.ui.adapter.TemplateAdapter

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val vm: MainViewModel by viewModels()
    private lateinit var adapter: TemplateAdapter

    // Permission launcher for POST_NOTIFICATIONS (Android 13+)
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (!granted) {
            Snackbar.make(
                binding.root,
                "Notification permission is required to fire mock notifications.",
                Snackbar.LENGTH_LONG
            ).setAction("Settings") {
                startActivity(
                    Intent(android.provider.Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                        putExtra(android.provider.Settings.EXTRA_APP_PACKAGE, packageName)
                    }
                )
            }.show()
        }
    }

    private val editorLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            vm.loadTemplates()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        setupRecyclerView()
        setupFab()
        observeViewModel()
        requestNotificationPermissionIfNeeded()
    }

    override fun onResume() {
        super.onResume()
        vm.loadTemplates()
    }

    private fun setupRecyclerView() {
        adapter = TemplateAdapter(
            onFire = { template -> vm.fireTemplate(template) },
            onEdit = { template ->
                editorLauncher.launch(
                    Intent(this, NotificationEditorActivity::class.java)
                        .putExtra(NotificationEditorActivity.EXTRA_TEMPLATE_ID, template.id)
                )
            },
            onDelete = { template ->
                vm.deleteTemplate(template.id)
                Snackbar.make(binding.root, "Deleted \"${template.title}\"", Snackbar.LENGTH_SHORT).show()
            }
        )

        binding.rvTemplates.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
            addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
        }
    }

    private fun setupFab() {
        binding.fabAdd.setOnClickListener {
            editorLauncher.launch(
                Intent(this, NotificationEditorActivity::class.java)
            )
        }
    }

    private fun observeViewModel() {
        vm.templates.observe(this) { list ->
            adapter.submitList(list)
            binding.tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        }

        vm.fireEvent.observe(this) { result ->
            val msg = result.message
            if (result.success) {
                Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT).show()
            } else {
                Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG)
                    .setBackgroundTint(ContextCompat.getColor(this, R.color.error_red))
                    .show()
            }
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_history -> {
            startActivity(Intent(this, HistoryActivity::class.java))
            true
        }
        R.id.action_cancel_all -> {
            vm.cancelAll()
            true
        }
        R.id.action_presets -> {
            startActivity(Intent(this, PresetListActivity::class.java))
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}
