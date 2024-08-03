package com.example.hackathon_guru

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.hackathon_guru.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup Bottom Navigation View
        binding.navigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_group -> {
                    // 그룹 버튼 클릭 시 GroupListMain 액티비티로 이동
                    val intent = Intent(this, GroupListMain::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_map -> {
                    // 다른 네비게이션 아이템 처리
                    true
                }
                R.id.navigation_scrap -> {
                    // 다른 네비게이션 아이템 처리
                    true
                }
                else -> false
            }
        }
    }
}
