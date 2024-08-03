package com.example.hackathon_guru

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResultListener
import com.example.hackathon_guru.databinding.FragmentAddGroupDialogBinding

class AddGroupDialogFragment : DialogFragment() {

    private var _binding: FragmentAddGroupDialogBinding? = null
    private val binding get() = _binding!!

    private val members = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddGroupDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.selectDateEditText.setOnClickListener {
            val dateRangePicker = DateRangePickerDialogFragment()
            dateRangePicker.show(parentFragmentManager, "DateRangePickerDialogFragment")
        }

        binding.addButton.setOnClickListener {
            val groupName = binding.groupNameEditText.text.toString()
            val dateRange = binding.selectDateEditText.text.toString()

            if (groupName.isEmpty() || dateRange.isEmpty() || members.isEmpty()) {
                Toast.makeText(requireContext(), "그룹 정보를 작성해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                val newGroup = TravelGroup(groupName, dateRange, members, false)
                val result = Bundle().apply {
                    putParcelable("newGroup", newGroup)
                }
                parentFragmentManager.setFragmentResult("addGroupRequestKey", result)
                dismiss()
            }
        }

        binding.closeButton.setOnClickListener {
            dismiss()
        }

        // 상단 텍스트 색상 변경
        binding.dialogTitle.setTextColor(Color.BLACK)
        binding.closeButton.setColorFilter(Color.BLACK)
        binding.addButton.setColorFilter(Color.BLACK)

        binding.addMemberButton.setOnClickListener {
            val emailMemberDialog = EmailMemberDialogFragment()
            emailMemberDialog.show(parentFragmentManager, "EmailMemberDialogFragment")
        }

        parentFragmentManager.setFragmentResultListener("addMemberRequestKey", this) { _, bundle ->
            val email = bundle.getString("memberEmail")
            email?.let {
                members.add(it)
                updateMemberList()
            }
        }

        parentFragmentManager.setFragmentResultListener("dateRangePickerKey", this) { _, bundle ->
            val selectedDateRange = bundle.getString("selectedDateRange")
            selectedDateRange?.let {
                binding.selectDateEditText.setText(it)
            }
        }
    }

    private fun updateMemberList() {
        val memberContainer = binding.memberContainer
        memberContainer.removeAllViews()

        for (member in members) {
            val memberView = LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

            val memberImageView = ImageView(context).apply {
                layoutParams = ViewGroup.LayoutParams(100, 100)
                setImageResource(R.drawable.ic_user_circle) // 기본 이미지 설정
                setPadding(8, 8, 8, 8)
            }

            val memberNameTextView = TextView(context).apply {
                text = member
                textSize = 14f
                setPadding(8, 8, 8, 8)
            }

            memberView.addView(memberImageView)
            memberView.addView(memberNameTextView)

            memberContainer.addView(memberView)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
