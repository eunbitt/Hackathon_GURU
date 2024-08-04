package com.example.hackathon_guru

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hackathon_guru.Schedule


class ScrapFolderAdapter(
    private val folderItems: List<String>, // 폴더 이름 리스트
    private val onItemClick: (String) -> Unit // 아이템 클릭 리스너
) : RecyclerView.Adapter<ScrapFolderAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_scrap_folder, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val folderName = folderItems[position]
        holder.textView.text = folderName

        holder.itemView.setOnClickListener {
            onItemClick(folderName)
            // 클릭 시 처리를 추가하고 싶다면 여기에 작성
        }
    }

    override fun getItemCount(): Int = folderItems.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.text_scrap_folder_item_name)
    }
}
