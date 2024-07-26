package com.example.hackathon_guru

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MyPage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_page)

        val backButton: ImageView = findViewById(R.id.back_button)
        val profileImage: ImageView = findViewById(R.id.profile_image)
        val editImage: ImageView = findViewById(R.id.edit_image)
        val nameEditText: EditText = findViewById(R.id.name)
        val emailEditText: EditText = findViewById(R.id.user_email)
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

        editImage.setOnClickListener {
            Toast.makeText(this, "Edit Image Clicked", Toast.LENGTH_SHORT).show()
            // Add logic to edit profile image
        }

        friendListText.setOnClickListener {
            // Navigate to FriendList activity
            val intent = Intent(this, FriendList::class.java)
            startActivity(intent)
        }

        logoutText.setOnClickListener {
            Toast.makeText(this, "Logout Clicked", Toast.LENGTH_SHORT).show()
            // Add logic to logout
        }

        deleteAccountText.setOnClickListener {
            Toast.makeText(this, "Delete Account Clicked", Toast.LENGTH_SHORT).show()
            // Add logic to delete account
        }
    }
}
