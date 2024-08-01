package com.example.hackathon_guru

import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.hackathon_guru.helpers.DatabaseHelper

class MyPage : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var userEmail: String
    private lateinit var userNameEditText: EditText
    private lateinit var userEmailEditText: EditText
    private lateinit var saveNameButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_page)

        dbHelper = DatabaseHelper(this)

        userEmail = intent.getStringExtra("USER_EMAIL") ?: ""

        val backButton: ImageView = findViewById(R.id.back_button)
        val profileImage: ImageView = findViewById(R.id.profile_image)
        userNameEditText = findViewById(R.id.name)
        userEmailEditText = findViewById(R.id.user_email)
        saveNameButton = findViewById(R.id.save_name_button)
        val friendListText: TextView = findViewById(R.id.friend_list)
        val logoutText: TextView = findViewById(R.id.logout)
        val deleteAccountText: TextView = findViewById(R.id.delete_account)

        backButton.setOnClickListener {
            finish()
        }

        profileImage.setOnClickListener {
            Toast.makeText(this, "Profile Image Clicked", Toast.LENGTH_SHORT).show()
            // Add logic to edit profile image
        }

        saveNameButton.setOnClickListener {
            val newName = userNameEditText.text.toString()
            if (newName.isEmpty()) {
                Toast.makeText(this, "이름을 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else if (dbHelper.isNameExists(newName)) {
                Toast.makeText(this, "이름이 이미 존재합니다.", Toast.LENGTH_SHORT).show()
            } else {
                dbHelper.updateUserName(userEmail, newName)
                Toast.makeText(this, "이름이 업데이트되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        friendListText.setOnClickListener {
            val intent = Intent(this, FriendList::class.java)
            intent.putExtra("USER_EMAIL", userEmail)
            startActivity(intent)
        }

        logoutText.setOnClickListener {
            Toast.makeText(this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
            // Add logic to logout
        }

        deleteAccountText.setOnClickListener {
            Toast.makeText(this, "계정이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
            // Add logic to delete account
        }

        loadUserInfo()
    }

    private fun loadUserInfo() {
        val cursor: Cursor = dbHelper.getUserByEmail(userEmail)
        if (cursor.moveToFirst()) {
            val name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_NAME))
            userNameEditText.setText(name)
            userEmailEditText.setText(userEmail)
            userEmailEditText.isEnabled = false
        }
        cursor.close()
    }
}
