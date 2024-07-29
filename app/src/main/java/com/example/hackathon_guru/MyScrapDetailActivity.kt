package com.example.hackathon_guru

import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hackathon_guru.databinding.ActivityMyScrapDetailBinding

class MyScrapDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyScrapDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyScrapDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // BottomNavigationView 설정
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

        // Intent로부터 폴더 이름을 가져옴
        val folderName = intent.getStringExtra("FOLDER_NAME")
        binding.folderNameTextView.text = folderName

        // Back 버튼 클릭 리스너 설정
        binding.backButton.setOnClickListener {
            onBackPressed() // 이전 페이지로 이동
        }

        // Edit 버튼 클릭 리스너 설정
        binding.editButton.setOnClickListener {
            // Animate remove buttons
            animateRemoveButtons()

            // Show delete confirmation dialog
            showDeleteConfirmationDialog(folderName)
        }
    }

    private fun showDeleteConfirmationDialog(folderName: String?) {
        val dialogView = layoutInflater.inflate(R.layout.activity_my_scrap_delete_folder_dialog, null)
        val alertDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        val yesButton = dialogView.findViewById<Button>(R.id.yesButton)
        val closeButton = dialogView.findViewById<ImageButton>(R.id.closeButton)

        closeButton.setOnClickListener {
            alertDialog.dismiss() // 다이얼로그 닫기
        }

        yesButton.setOnClickListener {
            deleteFolder(folderName)
            alertDialog.dismiss() // 다이얼로그 닫기
        }

        alertDialog.show()
    }

    private fun animateRemoveButtons() {
        // Get RecyclerView and its items
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val adapter = recyclerView.adapter as? ScrapFolderAdapter

        adapter?.let {
            for (i in 0 until it.itemCount) {
                val viewHolder = recyclerView.findViewHolderForAdapterPosition(i)
                viewHolder?.let {
                    val removeButton = viewHolder.itemView.findViewById<ImageView>(R.id.removeFolderButton)
                    val shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.shake_animation)
                    removeButton.apply {
                        visibility = View.VISIBLE
                        startAnimation(shakeAnimation)
                    }
                }
            }
        }
    }

    private fun deleteFolder(folderName: String?) {
        // 폴더 삭제 처리
        // 실제로 폴더를 삭제하고 데이터 변경을 반영하는 로직을 추가해야 합니다.
        Toast.makeText(this, "Folder '$folderName' deleted", Toast.LENGTH_SHORT).show()

        // 액티비티 종료 또는 다른 처리
        finish()
    }
}
