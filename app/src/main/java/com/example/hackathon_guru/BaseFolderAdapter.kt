package com.example.hackathon_guru

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.app.AlertDialog

abstract class BaseFolderAdapter(
    protected val context: Context,
    protected val folderList: MutableList<String>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    fun isFolderNameDuplicate(newName: String): Boolean {
        return folderList.contains(newName)
    }

    protected fun showAlertDialog(title: String, message: String) {
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("확인", null)
            .show()
    }

    // Abstract method to be implemented by subclasses
    abstract override fun getItemViewType(position: Int): Int
}
