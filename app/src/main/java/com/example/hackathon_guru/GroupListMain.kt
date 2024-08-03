package com.example.hackathon_guru

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hackathon_guru.databinding.ActivityGroupListMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText

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

        // Setup RecyclerView
        binding.groupRecyclerView.layoutManager = LinearLayoutManager(this)
        groupAdapter = GroupAdapter(groupList) { group, action ->
            when (action) {
                "edit" -> showEditGroupDialog(group)
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
    }

    private fun showEditGroupDialog(group: TravelGroup) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_group, null)
        val groupNameInput = dialogView.findViewById<TextInputEditText>(R.id.groupNameInput)
        val groupDateInput = dialogView.findViewById<TextInputEditText>(R.id.groupDateInput)
        val groupMembersInput = dialogView.findViewById<TextInputEditText>(R.id.groupMembersInput)

        groupNameInput.setText(group.name)
        groupDateInput.setText(group.dates)
        groupMembersInput.setText(group.members.joinToString(","))

        AlertDialog.Builder(this)
            .setTitle("         ")
            .setView(dialogView)
            .setPositiveButton("수정") { dialog, _ ->
                group.name = groupNameInput.text.toString()
                group.dates = groupDateInput.text.toString()
                group.members = groupMembersInput.text.toString().split(",")

                groupAdapter.notifyDataSetChanged()
                dialog.dismiss()
            }
            .setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun showDeleteGroupDialog(group: TravelGroup) {
        AlertDialog.Builder(this)
            .setTitle("그룹 삭제")
            .setMessage("정말 이 그룹을 삭제하시겠습니까?")
            .setPositiveButton("삭제") { dialog, _ ->
                groupList.remove(group)
                groupAdapter.notifyDataSetChanged()
                dialog.dismiss()
            }
            .setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }
}
