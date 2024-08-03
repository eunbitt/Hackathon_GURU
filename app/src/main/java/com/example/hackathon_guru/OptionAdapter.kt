package com.example.hackathon_guru

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.recyclerview.widget.RecyclerView
import com.example.hackathon_guru.databinding.ItemScrapFolderOptionBinding

class OptionAdapter(
    private val folderList: List<String>,
    private val onFolderSelected: (String) -> Unit
) : RecyclerView.Adapter<OptionAdapter.ViewHolder>() {

    private var selectedPosition = -1

    inner class ViewHolder(private val binding: ItemScrapFolderOptionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(folderName: String, position: Int) {
            binding.radioButton.text = folderName
            binding.radioButton.isChecked = position == selectedPosition
            binding.radioButton.setOnClickListener {
                selectedPosition = position
                notifyDataSetChanged()
                onFolderSelected(folderName)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemScrapFolderOptionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(folderList[position], position)
    }

    override fun getItemCount(): Int {
        return folderList.size
    }
}
