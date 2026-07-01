package com.mocknof.app.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mocknof.app.databinding.ItemActionBinding
import com.mocknof.app.model.NotificationAction

class ActionEditorAdapter(
    private val onRemove: (Int) -> Unit
) : RecyclerView.Adapter<ActionEditorAdapter.ViewHolder>() {

    private val items = mutableListOf<NotificationAction>()

    fun submitList(list: MutableList<NotificationAction>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemActionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(action: NotificationAction, index: Int) {
            binding.tvActionLabel.text = action.label
            binding.tvActionType.text = action.type.name
            binding.btnRemoveAction.setOnClickListener { onRemove(index) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemActionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position], position)

    override fun getItemCount() = items.size
}
