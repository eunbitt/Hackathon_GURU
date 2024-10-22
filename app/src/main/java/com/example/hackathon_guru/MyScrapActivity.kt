package com.example.hackathon_guru

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hackathon_guru.databinding.ActivityMyScrapBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MyScrapActivity : AppCompatActivity(), FolderUpdateListener {

    private lateinit var binding: ActivityMyScrapBinding
    private lateinit var recyclerView: RecyclerView
    private val folderAdapter = FolderAdapter(this, mutableListOf(), this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyScrapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 폴더 정보를 SharedPreferences에서 로드
        loadFoldersFromPreferences()

        // RecyclerView 초기화
        recyclerView = binding.recyclerView
        recyclerView.layoutManager = GridLayoutManager(this, 2) // 그리드 레이아웃 매니저 사용, spanCount = 2
        recyclerView.adapter = folderAdapter

        // BottomNavigationView 설정
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.navigationView)
        bottomNavigationView.selectedItemId = R.id.navigation_scrap

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_group -> {
                    startActivity(Intent(this, GroupListMain::class.java))
                    true
                }
                R.id.navigation_map -> {
                    val intent = Intent(this, MapActivity::class.java)
                    intent.putStringArrayListExtra("scrapFolders", ArrayList(folderAdapter.getFolderList()))
                    startActivity(intent)
                    true
                }
                R.id.navigation_scrap -> {
                    true
                }
                else -> false
            }
        }

        // AddIcon 클릭 리스너 설정
        binding.AddIcon.setOnClickListener {
            showAddFolderDialog()
        }

        // GroupScrap 클릭 리스너 설정
        val groupScrap = findViewById<ImageView>(R.id.GroupScrap)
        groupScrap.setOnClickListener {
            val intent = Intent(this, MyPage::class.java)
            startActivity(intent)
        }

        // Fragment 결과 수신 설정
        supportFragmentManager.setFragmentResultListener("requestKey", this) { _, bundle ->
            val folderName = bundle.getString("folderName")
            if (folderName != null) {
                if (!folderAdapter.isFolderNameDuplicate(folderName)) {
                    folderAdapter.addFolder(folderName)
                    saveFoldersToPreferences()
                } else {
                    showAlertDialog("폴더 이름 중복", "이미 존재하는 폴더 이름입니다.")
                }
            }
        }

        // 전달받은 폴더 이름이 있는 경우 해당 폴더를 열기
        val folderName = intent.getStringExtra("folderName")
        if (folderName != null) {
            openFolder(folderName)
        }
    }

    private fun saveFoldersToPreferences() {
        val sharedPreferences = getSharedPreferences("MyScrapPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val folderNames = folderAdapter.getFolderList().joinToString(separator = ",")
        editor.putString("folders", folderNames)
        editor.apply()
    }

    private fun loadFoldersFromPreferences() {
        val sharedPreferences = getSharedPreferences("MyScrapPrefs", MODE_PRIVATE)
        val folderNames = sharedPreferences.getString("folders", "") ?: ""
        val folders = folderNames.split(",").filter { it.isNotEmpty() }
        folderAdapter.clearFolders()
        folderAdapter.addFolders(folders)
    }

    private fun showAddFolderDialog() {
        val dialogView = layoutInflater.inflate(R.layout.activity_my_scrap_add_folder_dialog, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        val folderNameEditText = dialogView.findViewById<EditText>(R.id.ScrapFolderNameEditText)

        dialogView.findViewById<ImageButton>(R.id.closeButton).setOnClickListener {
            dialog.dismiss()
        }

        dialogView.findViewById<ImageButton>(R.id.addButton).setOnClickListener {
            val folderName = folderNameEditText.text.toString().trim()
            if (folderName.isNotBlank()) {
                if (!folderAdapter.isFolderNameDuplicate(folderName)) {
                    folderAdapter.addFolder(folderName)
                    saveFoldersToPreferences()
                    dialog.dismiss()
                } else {
                    showAlertDialog("폴더 이름 중복", "이미 존재하는 폴더 이름입니다.")
                }
            }
        }

        dialog.show()
    }

    private fun showAlertDialog(title: String, message: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("확인", null)
            .show()
    }

    private fun openFolder(folderName: String) {
        val intent = Intent(this, MyScrapDetailActivity::class.java)
        intent.putExtra("folderName", folderName)
        startActivity(intent)
    }

    override fun onFolderListUpdated() {
        saveFoldersToPreferences()
    }
}
