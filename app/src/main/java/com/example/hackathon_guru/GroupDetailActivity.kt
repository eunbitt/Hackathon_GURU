package com.example.hackathon_guru

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hackathon_guru.databinding.ActivityGroupDetailBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

class GroupDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGroupDetailBinding
    private lateinit var scheduleAdapter: ScheduleAdapter
    private val scheduleList = mutableListOf<Schedule>()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private lateinit var viewModel: ScheduleViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(ScheduleViewModel::class.java)

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

        // 데이터 로드
        loadSchedulesFromPreferences()

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
        if (scheduleList.isEmpty()) {
            scheduleList.add(Schedule("일정을 추가하세요", "", "", ""))
        }

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

        val itemTouchHelper = ItemTouchHelper(scheduleAdapter.itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.scheduleRecyclerView)
    }

    private fun showAddScheduleDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_schedule, null)
        val scheduleDateInput = dialogView.findViewById<TextInputEditText>(R.id.scheduleDateInput)
        val scheduleTitleInput = dialogView.findViewById<TextInputEditText>(R.id.scheduleTitleInput)
        val scheduleLocationInput = dialogView.findViewById<TextInputEditText>(R.id.scheduleLocationInput)
        val scheduleCommentInput = dialogView.findViewById<TextInputEditText>(R.id.scheduleCommentInput)

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
                    saveSchedulesToPreferences() // 일정 추가 후 저장
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

    private fun saveSchedulesToPreferences() {
        val sharedPreferences = getSharedPreferences("GroupDetailPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(scheduleList)
        editor.putString("schedules", json)
        editor.apply()
    }

    private fun loadSchedulesFromPreferences() {
        val sharedPreferences = getSharedPreferences("GroupDetailPrefs", MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("schedules", null)
        val type = object : TypeToken<MutableList<Schedule>>() {}.type
        scheduleList.clear()
        if (json != null) {
            val savedSchedules: MutableList<Schedule> = gson.fromJson(json, type)
            scheduleList.addAll(savedSchedules)
        } else {
            // 초기 값 추가 (예: 기본 일정)
            scheduleList.add(Schedule("일정을 추가하세요", "", "", ""))
        }
        scheduleAdapter.notifyDataSetChanged()
    }
}
