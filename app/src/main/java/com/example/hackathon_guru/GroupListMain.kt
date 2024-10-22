package com.example.hackathon_guru

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hackathon_guru.databinding.ActivityGroupListMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class GroupListMain : AppCompatActivity() {

    private lateinit var binding: ActivityGroupListMainBinding
    private lateinit var groupAdapter: GroupAdapter
    private val groupList = mutableListOf<TravelGroup>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupListMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup Toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false) // 툴바 타이틀 비활성화

        // Load groups from SharedPreferences
        loadGroups()

        // BottomNavigationView 설정
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.navigationView)
        bottomNavigationView.selectedItemId = R.id.navigation_group // map 선택

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_group -> {
                    // 이미 현재 화면이므로 아무 작업도 하지 않음
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

        // GroupScrap 아이콘 클릭 시 MyPage로 이동하도록 설정
        val groupScrapIcon = findViewById<ImageView>(R.id.GroupScrap)
        groupScrapIcon.setOnClickListener {
            val intent = Intent(this, MyPage::class.java)
            startActivity(intent)
        }

        // Setup RecyclerView
        binding.groupRecyclerView.layoutManager = LinearLayoutManager(this)
        groupAdapter = GroupAdapter(groupList) { group, action ->
            when (action) {
                "delete" -> showDeleteGroupDialog(group)
            }
        }
        binding.groupRecyclerView.adapter = groupAdapter

        // Setup Add Group Button
        binding.AddIcon.setOnClickListener {
            val addGroupDialog = AddGroupDialogFragment()
            addGroupDialog.show(supportFragmentManager, "AddGroupDialogFragment")
        }

        // Fragment Result Listener 설정
        supportFragmentManager.setFragmentResultListener("addGroupRequestKey", this) { _, bundle ->
            val newGroup = bundle.getParcelable<TravelGroup>("newGroup")
            Log.d("GroupListMain", "New group received: $newGroup")
            newGroup?.let { addNewGroup(it) }
        }
    }

    private fun addNewGroup(group: TravelGroup) {
        Log.d("GroupListMain", "Adding new group: $group")
        groupList.add(0, group) // 새로운 그룹을 맨 위에 추가
        groupAdapter.notifyItemInserted(0)
        binding.groupRecyclerView.scrollToPosition(0) // 추가된 항목으로 스크롤
        saveGroups() // 그룹 목록을 저장
    }

    private fun showDeleteGroupDialog(group: TravelGroup) {
        AlertDialog.Builder(this)
            .setTitle("그룹 삭제")
            .setMessage("그룹을 삭제하시겠습니까?")
            .setPositiveButton("삭제") { dialog, _ ->
                groupList.remove(group)
                groupAdapter.notifyDataSetChanged()
                saveGroups() // 그룹 목록을 저장
                dialog.dismiss()
            }
            .setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun saveGroups() {
        val sharedPreferences = getSharedPreferences("GroupListPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(groupList)
        editor.putString("groupList", json)
        editor.apply()
    }

    private fun loadGroups() {
        val sharedPreferences = getSharedPreferences("GroupListPrefs", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("groupList", null)
        if (json != null) {
            val type = object : TypeToken<MutableList<TravelGroup>>() {}.type
            val savedGroupList: MutableList<TravelGroup> = gson.fromJson(json, type)
            groupList.clear()
            groupList.addAll(savedGroupList)
        }
    }
}
