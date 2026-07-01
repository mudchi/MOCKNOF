package com.mocknof.app.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mocknof.app.databinding.ItemTemplateBinding
import com.mocknof.app.model.NotificationTemplate

class TemplateAdapter(
    private val onFire: (NotificationTemplate) -> Unit,
    private val onEdit: (NotificationTemplate) -> Unit,
    private val onDelete: (NotificationTemplate) -> Unit
) : ListAdapter<NotificationTemplate, TemplateAdapter.ViewHolder>(DIFF) {

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<NotificationTemplate>() {
            override fun areItemsTheSame(a: NotificationTemplate, b: NotificationTemplate) = a.id == b.id
            override fun areContentsTheSame(a: NotificationTemplate, b: NotificationTemplate) = a == b
        }
    }

    inner class ViewHolder(private val binding: ItemTemplateBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: NotificationTemplate) {
            binding.tvTitle.text = item.title
            binding.tvSubtitle.text = "${item.appName}  ·  ch: ${item.channelId}"
            binding.tvBody.text = item.body

            val tags = buildList {
                if (item.delaySeconds > 0) add("⏰ ${item.delaySeconds}s")
                if (item.repeatCount > 1) add("🔁 ×${item.repeatCount}")
                if (item.ongoing) add("📌 ongoing")
                if (item.actions.isNotEmpty()) add("${item.actions.size} actions")
                if (item.progress >= 0 || item.progress == -2) add("📊 progress")
            }
            binding.tvTags.text = tags.joinToString("  ")

            binding.btnFire.setOnClickListener { onFire(item) }
            binding.btnEdit.setOnClickListener { onEdit(item) }
            binding.btnDelete.setOnClickListener { onDelete(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTemplateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
