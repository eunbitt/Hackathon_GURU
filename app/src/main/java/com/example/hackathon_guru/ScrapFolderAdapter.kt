package com.example.hackathon_guru

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

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
            // 삭제 버튼 애니메이션
            val shakeAnimation = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.shake_animation)
            holder.removeButton.startAnimation(shakeAnimation)
            holder.removeButton.visibility = View.VISIBLE
        }

        holder.removeButton.setOnClickListener {
            // 폴더 삭제 처리
            // 여기서 실제 삭제 작업을 구현할 수 있습니다.
        }
    }

    override fun getItemCount(): Int = folderItems.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.text_scrap_folder_item_name)
        val removeButton: ImageView = itemView.findViewById(R.id.removeFolderButton)
    }
}
