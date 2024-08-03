package com.example.hackathon_guru

import Schedule
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hackathon_guru.databinding.ActivityGroupDetailBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*

class GroupDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGroupDetailBinding
    private lateinit var scheduleAdapter: ScheduleAdapter
    private val scheduleList = mutableListOf<Schedule>()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // BottomNavigationView 설정
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.navigationView)
        bottomNavigationView.selectedItemId = R.id.navigation_group // group 선택

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_group -> {
                    startActivity(Intent(this, GroupListMain::class.java))
                    true
                }
                R.id.navigation_map -> {
                    startActivity(Intent(this, MapActivity::class.java))
                    true
                }
                R.id.navigation_scrap -> {
                    startActivity(Intent(this, MyScrapActivity::class.java))
                    true
                }
                else -> false
            }
        }

        // 인텐트로 전달된 그룹 이름을 받아서 툴바 제목으로 설정
        val groupName = intent.getStringExtra("groupName")
        if (groupName != null) {
            val toolbarTitle = findViewById<TextView>(R.id.toolbar_title)
            toolbarTitle.text = groupName
        }

        // 리사이클러뷰 설정
        setupRecyclerView()

        binding.backButton.setOnClickListener {
            onBackPressed() // 이전 페이지로 이동
        }

        binding.GroupScrap.setOnClickListener {
            val intent = Intent(this, MyScrapDetailActivity::class.java)
            intent.putExtra("FOLDER_NAME", groupName)
            startActivity(intent)
        }
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

        // 드래그 앤 드롭을 위한 ItemTouchHelper 설정
        val itemTouchHelper = ItemTouchHelper(scheduleAdapter.itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.scheduleRecyclerView)

        // 기본 일정 추가 메시지
        if (scheduleList.isEmpty()) {
            scheduleList.add(Schedule("일정을 추가하세요", date = ""))
        }
        scheduleAdapter.notifyDataSetChanged()
    }

    private fun showAddScheduleDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_schedule, null)
        val scheduleDateInput = dialogView.findViewById<TextInputEditText>(R.id.scheduleDateInput)
        val scheduleTitleInput = dialogView.findViewById<TextInputEditText>(R.id.scheduleTitleInput)
        val scheduleLocationInput = dialogView.findViewById<TextInputEditText>(R.id.scheduleLocationInput)
        val scheduleCommentInput = dialogView.findViewById<TextInputEditText>(R.id.scheduleCommentInput)

        // 날짜 선택 클릭 리스너 설정
        scheduleDateInput.setOnClickListener {
            showDatePickerDialog(scheduleDateInput)
        }

        AlertDialog.Builder(this)
            .setTitle("일정 추가")
            .setView(dialogView)
            .setPositiveButton("추가") { dialog, _ ->
                val date = scheduleDateInput.text.toString()
                val title = scheduleTitleInput.text.toString()
                val location = scheduleLocationInput.text.toString()
                val comment = scheduleCommentInput.text.toString()

                if (title.isNotEmpty() && date.isNotEmpty()) {
                    val newSchedule = Schedule(title, date, location, comment)
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

    private fun showDatePickerDialog(dateInput: TextInputEditText) {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                dateInput.setText(dateFormat.format(calendar.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }
}
