package com.example.hackathon_guru

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.hackathon_guru.databinding.ItemScrapFolderBinding
import com.example.hackathon_guru.databinding.ItemScrapFolderOptionBinding
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView

class FolderAdapter(val folderList: MutableList<String>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var selectedFolder: String? = null

    inner class FolderViewHolder(private val binding: ItemScrapFolderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(folderName: String) {
            binding.textScrapFolderItemName.text = folderName
            binding.ScrapFolderItem.setOnClickListener {
                // 폴더 클릭 시, 세부 페이지로 이동
                val context = binding.root.context
                val intent = Intent(context, MyScrapDetailActivity::class.java).apply {
                    putExtra("FOLDER_NAME", folderName)
                }
                context.startActivity(intent)
            }

            binding.MoreButton.setOnClickListener {
                showFolderOptionsDialog(folderName)
            }
        }

        private fun showFolderOptionsDialog(folderName: String) {
            val context = binding.root.context
            val dialogView = LayoutInflater.from(context).inflate(R.layout.activity_my_scrap_folder_more_button, null)

            val dialog = AlertDialog.Builder(context)
                .setView(dialogView)
                .create()

            val folderNameEditText = dialogView.findViewById<EditText>(R.id.ScrapFolderNameEditText)
            folderNameEditText.setText(folderName) // 기존 폴더 이름을 EditText에 세팅

            dialogView.findViewById<ImageButton>(R.id.closeButton).setOnClickListener {
                dialog.dismiss()
            }

            dialogView.findViewById<ImageButton>(R.id.addButton).setOnClickListener {
                val newFolderName = folderNameEditText.text.toString().trim() // 공백 제거
                if (newFolderName.isNotBlank()) {
                    if (isFolderNameDuplicate(newFolderName)) {
                        // 중복된 이름인 경우, 사용자에게 알림 표시
                        showAlertDialog(context, "폴더 이름 중복", "이미 존재하는 폴더 이름입니다.")
                    } else {
                        updateFolderName(folderName, newFolderName)
                        dialog.dismiss()
                    }
                }
            }

            dialogView.findViewById<ImageView>(R.id.DeleteFolder).setOnClickListener {
                showDeleteConfirmationDialog(folderName)
                dialog.dismiss()
            }

            dialog.show()
        }

        private fun updateFolderName(oldName: String, newName: String) {
            val position = folderList.indexOf(oldName)
            if (position != -1) {
                folderList[position] = newName
                notifyItemChanged(position)
            }
        }

        private fun showDeleteConfirmationDialog(folderName: String) {
            val context = binding.root.context
            val dialogView = LayoutInflater.from(context).inflate(R.layout.activity_my_scrap_delete_folder_dialog, null)

            val dialog = AlertDialog.Builder(context)
                .setView(dialogView)
                .create()

            dialogView.findViewById<Button>(R.id.yesButton).setOnClickListener {
                deleteFolder(folderName)
                dialog.dismiss()
            }

            dialogView.findViewById<ImageButton>(R.id.closeButton).setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        }

        private fun deleteFolder(folderName: String) {
            val position = folderList.indexOf(folderName)
            if (position != -1) {
                folderList.removeAt(position)
                notifyItemRemoved(position)
            }
        }

        private fun isFolderNameDuplicate(newName: String): Boolean {
            return folderList.contains(newName)
        }

        private fun showAlertDialog(context: Context, title: String, message: String) {
            AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("확인", null)
                .show()
        }
    }

    inner class FolderOptionViewHolder(private val binding: ItemScrapFolderOptionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(folderName: String, isSelected: Boolean) {
            binding.textView.text = folderName
            binding.radioButton.isChecked = isSelected
            binding.radioButton.setOnClickListener {
                selectedFolder = folderName
                notifyDataSetChanged() // 모든 항목을 다시 그려서 선택 상태를 업데이트
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (selectedFolder == null) 0 else 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 0) {
            val binding = ItemScrapFolderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            FolderViewHolder(binding)
        } else {
            val binding = ItemScrapFolderOptionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            FolderOptionViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is FolderViewHolder) {
            holder.bind(folderList[position])
        } else if (holder is FolderOptionViewHolder) {
            val isSelected = folderList[position] == selectedFolder
            holder.bind(folderList[position], isSelected)
        }
    }

    override fun getItemCount(): Int {
        return folderList.size
    }

    fun addFolder(folderName: String) {
        folderList.add(folderName)
        notifyItemInserted(folderList.size - 1)
    }
}