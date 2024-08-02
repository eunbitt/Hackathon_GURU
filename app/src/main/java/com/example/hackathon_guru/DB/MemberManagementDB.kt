package com.example.hackathon_guru.helpers

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "app_database.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_USERS = "users"
        private const val COLUMN_USER_ID = "id"
        const val COLUMN_USER_NAME = "name"
        private const val COLUMN_USER_EMAIL = "email"
        private const val COLUMN_USER_PASSWORD = "password"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = ("CREATE TABLE $TABLE_USERS ("
                + "$COLUMN_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COLUMN_USER_NAME TEXT, "
                + "$COLUMN_USER_EMAIL TEXT, "
                + "$COLUMN_USER_PASSWORD TEXT)")
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    fun addUser(name: String, email: String, password: String) {
        val db = this.writableDatabase
        val insertQuery = "INSERT INTO $TABLE_USERS ($COLUMN_USER_NAME, $COLUMN_USER_EMAIL, $COLUMN_USER_PASSWORD) VALUES ('$name', '$email', '$password')"
        db.execSQL(insertQuery)
        db.close()
    }

    fun getUser(email: String, password: String): Boolean {
        val db = this.readableDatabase
        val selectQuery = "SELECT * FROM $TABLE_USERS WHERE $COLUMN_USER_EMAIL = '$email' AND $COLUMN_USER_PASSWORD = '$password'"
        val cursor = db.rawQuery(selectQuery, null)
        val userExists = cursor.count > 0
        cursor.close()
        db.close()
        return userExists
    }

    fun getUserNameByEmail(email: String): String? {
        val db = this.readableDatabase
        val selectQuery = "SELECT $COLUMN_USER_NAME FROM $TABLE_USERS WHERE $COLUMN_USER_EMAIL = '$email'"
        val cursor: Cursor = db.rawQuery(selectQuery, null)
        var name: String? = null
        if (cursor.moveToFirst()) {
            name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_NAME))
        }
        cursor.close()
        db.close()
        return name
    }

    fun isEmailExists(email: String): Boolean {
        val db = this.readableDatabase
        val selectQuery = "SELECT * FROM $TABLE_USERS WHERE $COLUMN_USER_EMAIL = '$email'"
        val cursor = db.rawQuery(selectQuery, null)
        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }

    fun isNameExists(name: String): Boolean {
        val db = this.readableDatabase
        val selectQuery = "SELECT * FROM $TABLE_USERS WHERE $COLUMN_USER_NAME = '$name'"
        val cursor = db.rawQuery(selectQuery, null)
        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }

    fun updateUserName(email: String, newName: String): Boolean {
        val db = this.writableDatabase
        val updateQuery = "UPDATE $TABLE_USERS SET $COLUMN_USER_NAME = '$newName' WHERE $COLUMN_USER_EMAIL = '$email'"
        db.execSQL(updateQuery)
        db.close()
        return true
    }

    fun getUserByEmail(email: String): Cursor {
        val db = this.readableDatabase
        val selectQuery = "SELECT * FROM $TABLE_USERS WHERE $COLUMN_USER_EMAIL = '$email'"
        return db.rawQuery(selectQuery, null)
    }

    fun deleteUser(email: String) {
        val db = this.writableDatabase
        val deleteQuery = "DELETE FROM $TABLE_USERS WHERE $COLUMN_USER_EMAIL = '$email'"
        db.execSQL(deleteQuery)
        db.close()
    }

}