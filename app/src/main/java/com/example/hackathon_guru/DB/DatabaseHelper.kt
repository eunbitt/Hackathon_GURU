package com.example.hackathon_guru.DB

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        // User 테이블 생성
        db.execSQL("CREATE TABLE $TABLE_USER (" +
                "$COLUMN_USER_EMAIL TEXT PRIMARY KEY, " +
                "$COLUMN_USER_NAME TEXT, " +
                "$COLUMN_USER_PASSWORD TEXT)")

        // Friend 테이블 생성
        db.execSQL("CREATE TABLE $TABLE_FRIEND (" +
                "$COLUMN_USER_EMAIL TEXT, " +
                "$COLUMN_FRIEND_EMAIL TEXT, " +
                "$COLUMN_FRIEND_NAME TEXT, " +
                "PRIMARY KEY ($COLUMN_USER_EMAIL, $COLUMN_FRIEND_EMAIL))")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USER")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_FRIEND")
        onCreate(db)
    }

    // 사용자 추가
    fun addUser(name: String, email: String, password: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USER_NAME, name)
            put(COLUMN_USER_EMAIL, email)
            put(COLUMN_USER_PASSWORD, password)
        }
        db.insert(TABLE_USER, null, values)
    }

    // 로그인 시 사용자 확인
    fun getUser(email: String, password: String): Boolean {
        val db = readableDatabase
        val cursor = db.query(TABLE_USER, arrayOf(COLUMN_USER_EMAIL),
            "$COLUMN_USER_EMAIL = ? AND $COLUMN_USER_PASSWORD = ?",
            arrayOf(email, password), null, null, null)
        val exists = cursor.moveToFirst()
        cursor.close()
        return exists
    }

    // 사용자 이름 가져오기
    fun getUserNameByEmail(email: String): String? {
        val db = readableDatabase
        val cursor = db.query(TABLE_USER, arrayOf(COLUMN_USER_NAME), "$COLUMN_USER_EMAIL = ?", arrayOf(email), null, null, null)
        return if (cursor.moveToFirst()) {
            val userName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_NAME))
            cursor.close()
            userName
        } else {
            cursor.close()
            null
        }
    }

    // 사용자 정보 가져오기
    fun getUserByEmail(email: String): Cursor {
        val db = readableDatabase
        return db.query(TABLE_USER, null, "$COLUMN_USER_EMAIL = ?", arrayOf(email), null, null, null)
    }

    // 사용자 정보 가져오기 (이름을 통해)
    fun getUserByName(name: String): Cursor {
        val db = readableDatabase
        return db.query(TABLE_USER, null, "$COLUMN_USER_NAME = ?", arrayOf(name), null, null, null)
    }

    // 사용자 삭제 (이름을 통해)
    fun deleteUserByName(name: String) {
        val db = writableDatabase
        db.delete(TABLE_USER, "$COLUMN_USER_NAME = ?", arrayOf(name))
        db.delete(TABLE_FRIEND, "$COLUMN_USER_EMAIL IN (SELECT $COLUMN_USER_EMAIL FROM $TABLE_USER WHERE $COLUMN_USER_NAME = ?)", arrayOf(name)) // 친구 목록에서도 삭제
    }


    // 사용자 이름 업데이트
    fun updateUserName(email: String, newName: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USER_NAME, newName)
        }
        db.update(TABLE_USER, values, "$COLUMN_USER_EMAIL = ?", arrayOf(email))
    }

    // 사용자 삭제
    fun deleteUser(email: String) {
        val db = writableDatabase
        db.delete(TABLE_USER, "$COLUMN_USER_EMAIL = ?", arrayOf(email))
        db.delete(TABLE_FRIEND, "$COLUMN_USER_EMAIL = ?", arrayOf(email)) // 친구 목록에서도 삭제
    }

    // 이름 존재 여부 확인
    fun isNameExists(name: String): Boolean {
        val db = readableDatabase
        val cursor = db.query(TABLE_USER, arrayOf(COLUMN_USER_NAME), "$COLUMN_USER_NAME = ?", arrayOf(name), null, null, null)
        val exists = cursor.moveToFirst()
        cursor.close()
        return exists
    }

    // 이메일 존재 여부 확인
    fun isEmailExists(email: String): Boolean {
        val db = readableDatabase
        val cursor = db.query(TABLE_USER, arrayOf(COLUMN_USER_EMAIL), "$COLUMN_USER_EMAIL = ?", arrayOf(email), null, null, null)
        val exists = cursor.moveToFirst()
        cursor.close()
        return exists
    }

    // 친구 목록 가져오기
    fun getFriends(userEmail: String): Cursor {
        val db = readableDatabase
        return db.query(TABLE_FRIEND, arrayOf(COLUMN_FRIEND_NAME), "$COLUMN_USER_EMAIL = ?", arrayOf(userEmail), null, null, null)
    }

    // 친구 추가
    fun addFriend(userEmail: String, friendEmail: String, friendName: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USER_EMAIL, userEmail)
            put(COLUMN_FRIEND_EMAIL, friendEmail)
            put(COLUMN_FRIEND_NAME, friendName)
        }
        db.insert(TABLE_FRIEND, null, values)
    }

    // 친구 삭제
    fun deleteFriend(userEmail: String, friendName: String) {
        val db = writableDatabase
        db.delete(TABLE_FRIEND, "$COLUMN_USER_EMAIL = ? AND $COLUMN_FRIEND_NAME = ?", arrayOf(userEmail, friendName))
    }

    companion object {
        private const val DATABASE_NAME = "hackathon_guru.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_USER = "User"
        const val COLUMN_USER_EMAIL = "user_email"
        const val COLUMN_USER_NAME = "user_name"
        const val COLUMN_USER_PASSWORD = "user_password"

        const val TABLE_FRIEND = "Friend"
        const val COLUMN_FRIEND_EMAIL = "friend_email"
        const val COLUMN_FRIEND_NAME = "friend_name"
    }
}
