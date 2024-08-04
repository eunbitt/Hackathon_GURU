package com.example.hackathon_guru

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hackathon_guru.databinding.ActivityMyScrapDetailBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MyScrapDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyScrapDetailBinding
    private lateinit var placeAdapter: PlaceAdapter
    private val placeList = mutableListOf<PlaceData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyScrapDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val folderName = intent.getStringExtra("FOLDER_NAME") ?: return
        binding.folderNameTextView.text = folderName

        // SharedPreferences에서 폴더의 장소 정보 로드
        val sharedPreferences = getSharedPreferences("MyScrapFolderPrefs", MODE_PRIVATE)
        val savedPlaces = sharedPreferences.getStringSet(folderName, null)

        // 장소 정보를 로드하여 placeList에 추가
        savedPlaces?.forEach { place ->
            val parts = place.split("|")
            if (parts.size == 3) {
                placeList.add(PlaceData(parts[0], parts[1], parts[2]))
            }
        }

        // RecyclerView 초기화
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        placeAdapter = PlaceAdapter(placeList, showScrapButton = false) { /* 클릭 리스너 */ }
        binding.recyclerView.adapter = placeAdapter

        // BottomNavigationView 설정
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.navigationView)
        bottomNavigationView.selectedItemId = R.id.navigation_scrap // scrap 선택

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

        binding.backButton.setOnClickListener {
            onBackPressed() // 이전 페이지로 이동
        }

        binding.editButton.setOnClickListener {
            // 폴더 편집 기능 구현
        }
    }
}
