package com.example.hackathon_guru

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.appcompat.app.AppCompatActivity
import com.example.hackathon_guru.databinding.ActivityAddGroupBinding
import java.util.*

class AddGroupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddGroupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddGroupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.selectDateEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(this, { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
                val selectedDate = "$selectedYear-${selectedMonth + 1}-$selectedDay"
                binding.selectDateEditText.setText(selectedDate)
            }, year, month, day)
            datePickerDialog.show()
        }

        binding.saveGroupButton.setOnClickListener {
            val groupName = binding.groupNameEditText.text.toString()
            val date = binding.selectDateEditText.text.toString()

            // 그룹 저장 로직 추가

            finish() // 돌아가기
        }
    }
}
