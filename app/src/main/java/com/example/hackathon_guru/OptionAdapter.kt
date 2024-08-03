package com.example.hackathon_guru

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.hackathon_guru.databinding.ItemScrapFolderOptionBinding

class OptionAdapter(
    context: Context,
    folderList: MutableList<String>
) : BaseFolderAdapter(context, folderList) {

    private var selectedFolder: String? = null

    inner class OptionViewHolder(private val binding: ItemScrapFolderOptionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(folderName: String, isSelected: Boolean) {
            binding.textView.text = folderName
            binding.radioButton.isChecked = isSelected
            binding.radioButton.setOnClickListener {
                selectedFolder = folderName
                notifyDataSetChanged()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return 0 // Assuming OptionAdapter only has one view type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemScrapFolderOptionBinding.inflate(LayoutInflater.from(context), parent, false)
        return OptionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is OptionViewHolder) {
            val folderName = folderList[position]
            val isSelected = folderName == selectedFolder
            holder.bind(folderName, isSelected)
        }
    }

    override fun getItemCount(): Int {
        return folderList.size
    }

    fun updateFolders(newFolders: List<String>) {
        folderList.clear()
        folderList.addAll(newFolders)
        notifyDataSetChanged()
    }
}
