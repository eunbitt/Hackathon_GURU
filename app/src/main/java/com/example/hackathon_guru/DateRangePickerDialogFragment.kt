package com.example.hackathon_guru

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.hackathon_guru.databinding.FragmentAddGroupDialogBinding
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener
import java.text.SimpleDateFormat
import java.util.*

class DateRangePickerDialogFragment : DialogFragment() {

    private var _binding: FragmentAddGroupDialogBinding? = null
    private val binding get() = _binding!!

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

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
            showDateRangePicker()
        }

        binding.addButton.setOnClickListener {
            val groupName = binding.groupNameEditText.text.toString()
            val groupDate = binding.selectDateEditText.text.toString()

            if (groupName.isEmpty() || groupDate.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else {
                // Add logic to save the new group
                dismiss()
            }
        }

        binding.closeButton.setOnClickListener {
            dismiss()
        }
    }

    private fun showDateRangePicker() {
        val dateRangePicker =
            MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText("Select Date Range")
                .build()

        dateRangePicker.addOnPositiveButtonClickListener { datePicked ->
            val startDate = datePicked.first
            val endDate = datePicked.second
            binding.selectDateEditText.setText(
                "Start Date: ${dateFormat.format(Date(startDate!!))}\nEnd Date: ${dateFormat.format(Date(endDate!!))}"
            )
        }

        dateRangePicker.show(parentFragmentManager, "date_range_picker")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

