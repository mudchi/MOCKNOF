package com.mocknof.app.ui.adapter

import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mocknof.app.databinding.ItemHistoryBinding
import com.mocknof.app.model.NotificationHistory
import java.util.Date

class HistoryAdapter : ListAdapter<NotificationHistory, HistoryAdapter.ViewHolder>(DIFF) {

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<NotificationHistory>() {
            override fun areItemsTheSame(a: NotificationHistory, b: NotificationHistory) = a.id == b.id
            override fun areContentsTheSame(a: NotificationHistory, b: NotificationHistory) = a == b
        }
    }

    inner class ViewHolder(private val binding: ItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: NotificationHistory) {
            binding.tvHistTitle.text = item.title
            binding.tvHistApp.text = item.appName
            binding.tvHistBody.text = item.body
            val time = DateFormat.format("HH:mm:ss  dd/MM/yyyy", Date(item.firedAt))
            binding.tvHistTime.text = time
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
