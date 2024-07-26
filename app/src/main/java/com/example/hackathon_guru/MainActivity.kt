package com.example.hackathon_guru

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hackathon_guru.databinding.ActivityGroupListMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGroupListMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupListMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup Toolbar
        setSupportActionBar(binding.toolbar)

        // Setup RecyclerView
        val groupList = listOf(
                TravelGroup(name = "부산", dates = "7월 12일 ~ 7월 14일"),
                TravelGroup(name = "서울", dates = "8월 1일 ~ 8월 9일"),
                TravelGroup(name = "인천", dates = "6월 1일 ~ 6월 9일")
        )

        binding.groupRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.groupRecyclerView.adapter = GroupAdapter(groupList)

        // Setup Add Group Button
        binding.addGroupButton.setOnClickListener {
            val addGroupDialog = AddGroupDialogFragment()
            addGroupDialog.show(supportFragmentManager, "AddGroupDialogFragment")
        }

        // Setup Bottom Navigation View
        binding.navigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_group -> {
                    // Handle group navigation
                    true
                }
                R.id.navigation_map -> {
                    // Handle map navigation
                    true
                }
                R.id.navigation_scrap -> {
                    // Handle scrap navigation
                    true
                }
                else -> false
            }
        }
    }
}
