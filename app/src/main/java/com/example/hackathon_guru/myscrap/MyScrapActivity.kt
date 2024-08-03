package com.example.hackathon_guru.myscrap

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hackathon_guru.R
import com.example.hackathon_guru.databinding.ActivityMyScrapBinding
import com.example.hackathon_guru.map.MapActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MyScrapActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyScrapBinding
    private lateinit var recyclerView: RecyclerView
    private val folderAdapter = FolderAdapter(mutableListOf())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyScrapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // RecyclerView 초기화
        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = folderAdapter

        // BottomNavigationView 설정
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.navigationView)
        bottomNavigationView.selectedItemId = R.id.navigation_scrap // scrap 선택

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_group -> {
                    // Handle group navigation
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

        // AddIcon 클릭 리스너 설정
        binding.AddIcon.setOnClickListener {
            showAddFolderDialog()
        }
    }

    private fun showAddFolderDialog() {
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.activity_my_scrap_add_folder_dialog, null)

        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)

        val dialog = builder.create()
        dialog.show()

        val closeButton: ImageButton = dialogView.findViewById(R.id.closeButton)
        val addButton: ImageButton = dialogView.findViewById(R.id.addButton)
        val folderNameEditText: EditText = dialogView.findViewById(R.id.ScrapFolderNameEditText)

        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        addButton.setOnClickListener {
            val folderName = folderNameEditText.text.toString()
            if (folderName.isNotEmpty()) {
                folderAdapter.addFolder(folderName)
                folderNameEditText.text.clear() // 입력 필드 비우기
            }
            dialog.dismiss()
        }
    }
}
