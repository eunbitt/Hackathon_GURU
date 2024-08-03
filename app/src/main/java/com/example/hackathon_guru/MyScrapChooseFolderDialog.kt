package com.example.hackathon_guru

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hackathon_guru.databinding.ActivityMyScrapChooseFolderDialogBinding

class MyScrapChooseFolderDialog(
    private val folderList: List<String>,
    private val onFolderSelected: (String) -> Unit
) : DialogFragment() {

    private lateinit var binding: ActivityMyScrapChooseFolderDialogBinding
    private lateinit var dialogContext: Context
    private lateinit var optionAdapter: OptionAdapter

    override fun onCreateDialog(savedInstanceState: Bundle?): AlertDialog {
        binding = ActivityMyScrapChooseFolderDialogBinding.inflate(requireActivity().layoutInflater)
        dialogContext = requireActivity()
        val dialog = AlertDialog.Builder(dialogContext)
            .setView(binding.root)
            .create()

        // RecyclerView 설정
        val folderRecyclerView = binding.folderRecyclerViewOption
        optionAdapter = OptionAdapter(folderList) { selectedFolder ->
            onFolderSelected(selectedFolder)
        }
        folderRecyclerView.adapter = optionAdapter
        folderRecyclerView.layoutManager = LinearLayoutManager(dialogContext)

        // 닫기 버튼 클릭 리스너 설정
        binding.closeButton.setOnClickListener {
            dialog.dismiss()
        }

        binding.addButton.setOnClickListener {
            // 선택된 폴더를 콜백으로 전달
            val selectedPosition = optionAdapter.selectedPosition
            if (selectedPosition != -1) {
                onFolderSelected(folderList[selectedPosition])
                dialog.dismiss()
            }
        }

        return dialog
    }
}
