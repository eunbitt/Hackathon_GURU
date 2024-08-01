package com.example.hackathon_guru

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.hackathon_guru.helpers.DatabaseHelper

class FriendList : AppCompatActivity() {

    private lateinit var friendListLayout: LinearLayout
    private lateinit var dbHelper: DatabaseHelper
    private val friendList = ArrayList<String>()
    private var deleteMode = false
    private lateinit var userEmail: String  // 로그인한 사용자의 이메일

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_list)

        friendListLayout = findViewById(R.id.friend_list_layout)
        dbHelper = DatabaseHelper(this)

        // 로그인한 사용자의 이메일을 Intent로 전달받음
        userEmail = intent.getStringExtra("USER_EMAIL") ?: ""

        val backButton: ImageView = findViewById(R.id.back_button)
        backButton.setOnClickListener {
            val intent = Intent(this, MyPage::class.java)
            intent.putExtra("USER_EMAIL", userEmail)
            startActivity(intent)
            finish()
        }

        val addDeleteButton: ImageView = findViewById(R.id.add_delete_button)
        addDeleteButton.setOnClickListener {
            showManageFriendsDialog()
        }
    }

    private fun addFriendView(friendName: String) {
        val friendView = LayoutInflater.from(this).inflate(R.layout.activity_friend_info, friendListLayout, false)

        val friendNameView: TextView = friendView.findViewById(R.id.friend_name)
        friendNameView.text = friendName

        val deleteButton: Button = friendView.findViewById(R.id.delete_button)
        deleteButton.visibility = if (deleteMode) View.VISIBLE else View.GONE
        deleteButton.setOnClickListener {
            showDeleteConfirmationDialog(friendView, friendName)
        }

        friendListLayout.addView(friendView)
    }

    private fun showManageFriendsDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("친구 관리")

        builder.setPositiveButton("친구 추가") { _, _ ->
            showAddFriendDialog()
        }
        builder.setNegativeButton("친구 삭제") { _, _ ->
            toggleDeleteMode()
        }

        builder.setNeutralButton("닫기") { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }

    private fun showAddFriendDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("친구 추가")

        val input = EditText(this)
        input.hint = "친구의 이메일을 입력하세요"
        builder.setView(input)

        builder.setPositiveButton("추가") { _, _ ->
            val friendEmail = input.text.toString()
            if (friendEmail.isNotEmpty()) {
                if (friendEmail == userEmail) {
                    Toast.makeText(this, "본인은 친구로 추가할 수 없습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    val friendName = dbHelper.getUserNameByEmail(friendEmail)
                    if (friendName != null) {
                        friendList.add(friendName)
                        addFriendView(friendName)
                    } else {
                        Toast.makeText(this, "친구를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("취소") { dialog, _ ->
            dialog.cancel()
        }

        builder.create().show()
    }

    private fun toggleDeleteMode() {
        deleteMode = !deleteMode
        for (i in 0 until friendListLayout.childCount) {
            val friendView = friendListLayout.getChildAt(i)
            val deleteButton: Button = friendView.findViewById(R.id.delete_button)
            deleteButton.visibility = if (deleteMode) View.VISIBLE else View.GONE
        }
    }

    private fun showDeleteConfirmationDialog(friendView: View, friendName: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("삭제 확인")
        builder.setMessage("정말로 삭제하시겠습니까?")
        builder.setPositiveButton("삭제") { _, _ ->
            friendListLayout.removeView(friendView)
            friendList.remove(friendName)
        }

        builder.setNegativeButton("취소") { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }
}
