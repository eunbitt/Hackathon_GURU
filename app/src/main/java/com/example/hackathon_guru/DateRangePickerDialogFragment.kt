package com.example.hackathon_guru

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.hackathon_guru.databinding.FragmentAddGroupDialogBinding
import com.google.android.material.datepicker.MaterialDatePicker
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

        showDateRangePicker()
    }

    private fun showDateRangePicker() {
        val dateRangePicker =
            MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText("Select Date Range")
                .build()

        dateRangePicker.addOnPositiveButtonClickListener { datePicked ->
            val startDate = datePicked.first
            val endDate = datePicked.second
            val dateRangeString = "Start Date: ${dateFormat.format(Date(startDate!!))}\nEnd Date: ${dateFormat.format(Date(endDate!!))}"

            parentFragmentManager.setFragmentResult(
                "dateRangePickerKey",
                Bundle().apply {
                    putString("selectedDateRange", dateRangeString)
                }
            )
            dismiss()
        }

        dateRangePicker.show(childFragmentManager, "date_range_picker")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
