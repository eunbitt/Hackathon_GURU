package com.example.hackathon_guru

import Schedule
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hackathon_guru.databinding.ActivityGroupDetailBinding
import com.google.android.material.textfield.TextInputEditText

class GroupDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGroupDetailBinding
    private lateinit var scheduleAdapter: ScheduleAdapter
    private val scheduleList = mutableListOf<Schedule>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 툴바를 액션바로 설정
        setSupportActionBar(binding.toolbar)

        // 인텐트로 전달된 그룹 이름을 받아서 툴바 제목으로 설정
        val groupName = intent.getStringExtra("groupName")
        if (groupName != null) {
            val toolbarTitle = findViewById<TextView>(R.id.toolbar_title)
            toolbarTitle.text = groupName
        }

        // 리사이클러뷰 설정
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        scheduleAdapter = ScheduleAdapter(scheduleList) { schedule ->
            // 일정 추가 버튼 클릭 시 다이얼로그 표시
            if (schedule.title == "일정을 추가하세요") {
                showAddScheduleDialog()
            }
        }
        binding.scheduleRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@GroupDetailActivity)
            adapter = scheduleAdapter
        }

        // 기본 일정 추가 메시지
        scheduleList.add(Schedule("일정을 추가하세요"))
        scheduleAdapter.notifyDataSetChanged()
    }

    private fun showAddScheduleDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_schedule, null)
        val scheduleTitleInput = dialogView.findViewById<TextInputEditText>(R.id.scheduleTitleInput)
        val scheduleLocationInput = dialogView.findViewById<TextInputEditText>(R.id.scheduleLocationInput)
        val scheduleCommentInput = dialogView.findViewById<TextInputEditText>(R.id.scheduleCommentInput)

        AlertDialog.Builder(this)
            .setTitle("일정 추가")
            .setView(dialogView)
            .setPositiveButton("추가") { dialog, _ ->
                val title = scheduleTitleInput.text.toString()
                val location = scheduleLocationInput.text.toString()
                val comment = scheduleCommentInput.text.toString()

                if (title.isNotEmpty()) {
                    val newSchedule = Schedule(title, location, comment)
                    scheduleList.add(newSchedule)
                    scheduleAdapter.notifyDataSetChanged()
                }
                dialog.dismiss()
            }
            .setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }
}
