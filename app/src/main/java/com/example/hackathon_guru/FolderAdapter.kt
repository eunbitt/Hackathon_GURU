package com.example.hackathon_guru

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.hackathon_guru.databinding.ItemScrapFolderBinding

class FolderAdapter(private val folderList: MutableList<String>) : RecyclerView.Adapter<FolderAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemScrapFolderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(folderName: String) {
            binding.textScrapFolderItemName.text = folderName
            binding.ScrapFolderItem.setOnClickListener {
                val context = binding.root.context
                val intent = Intent(context, MyScrapDetailActivity::class.java)
                intent.putExtra("FOLDER_NAME", folderName)
                context.startActivity(intent)
            }
            binding.removeFolderButton.setOnClickListener {
                showDeleteConfirmationDialog(folderName)
            }
        }

        private fun showDeleteConfirmationDialog(folderName: String) {
            val context = binding.root.context
            AlertDialog.Builder(context)
                .setTitle("Delete Item")
                .setMessage("Are you sure you want to delete '$folderName'?")
                .setPositiveButton("Yes") { _, _ ->
                    deleteFolder(folderName)
                }
                .setNegativeButton("No", null)
                .show()
        }

        private fun deleteFolder(folderName: String) {
            val position = folderList.indexOf(folderName)
            if (position != -1) {
                folderList.removeAt(position)
                notifyItemRemoved(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemScrapFolderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(folderList[position])
    }

    override fun getItemCount(): Int {
        return folderList.size
    }

    fun addFolder(folderName: String) {
        folderList.add(folderName)
        notifyItemInserted(folderList.size - 1)
    }
}