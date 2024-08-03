package com.example.hackathon_guru.myscrap

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.hackathon_guru.R
import com.example.hackathon_guru.databinding.ActivityMyScrapDetailBinding
import com.example.hackathon_guru.map.MapActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MyScrapDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyScrapDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyScrapDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        // Intent로부터 폴더 이름을 가져옴
        val folderName = intent.getStringExtra("FOLDER_NAME")
        binding.folderNameTextView.text = folderName

        // Back 버튼 클릭 리스너 설정
        binding.backButton.setOnClickListener {
            onBackPressed() // 이전 페이지로 이동
        }

        // Edit 버튼 클릭 리스너 설정
        binding.editButton.setOnClickListener {
            // 현재는 삭제 관련 기능 제거
            // 필요시 폴더 세부 정보 수정 관련 코드를 여기에 추가
        }
    }
}
