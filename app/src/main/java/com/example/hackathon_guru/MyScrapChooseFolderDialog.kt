package com.example.hackathon_guru

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.example.hackathon_guru.databinding.ActivityMyScrapAddFolderDialogBinding

class MyScrapChooseFolderDialog : DialogFragment() {

    private var _binding: ActivityMyScrapAddFolderDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ActivityMyScrapAddFolderDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val closeButton: ImageButton = binding.closeButton
        val addButton: ImageButton = binding.addButton
        val folderNameEditText: EditText = binding.ScrapFolderNameEditText

        closeButton.setOnClickListener {
            dismiss()
        }

        addButton.setOnClickListener {
            val folderName = folderNameEditText.text.toString()
            if (folderName.isNotEmpty()) {
                // Fragment 결과 설정
                setFragmentResult("requestKey", Bundle().apply {
                    putString("folderName", folderName)
                })
                folderNameEditText.text.clear() // 입력 필드 비우기
            }
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
