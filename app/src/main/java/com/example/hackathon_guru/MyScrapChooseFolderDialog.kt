package com.example.hackathon_guru

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hackathon_guru.databinding.ActivityMyScrapChooseFolderDialogBinding

class MyScrapChooseFolderDialog : DialogFragment() {

    private lateinit var binding: ActivityMyScrapChooseFolderDialogBinding
    private lateinit var folderList: MutableList<String>
    private lateinit var dialogContext: Context

    override fun onCreateDialog(savedInstanceState: Bundle?): AlertDialog {
        binding = ActivityMyScrapChooseFolderDialogBinding.inflate(requireActivity().layoutInflater)
        val dialog = AlertDialog.Builder(dialogContext)
            .setView(binding.root)
            .create()

        // RecyclerView 설정
        val folderRecyclerView = binding.folderRecyclerViewOption
        val optionAdapter = OptionAdapter(dialogContext, folderList)
        folderRecyclerView.adapter = optionAdapter
        folderRecyclerView.layoutManager = LinearLayoutManager(dialogContext)

        // 버튼 클릭 리스너 설정
        binding.closeButton.setOnClickListener {
            dialog.dismiss()
        }

        binding.addButton.setOnClickListener {
            // 폴더 추가 작업 처리
            dialog.dismiss()
        }

        return dialog
    }

    fun setFolderList(list: MutableList<String>) {
        folderList = list
    }

    fun setContext(context: Context) {
        dialogContext = context
    }
}
