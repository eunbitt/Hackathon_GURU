package com.example.hackathon_guru

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.hackathon_guru.databinding.ItemGroupBinding

class GroupAdapter(private val groupList: List<TravelGroup>) : RecyclerView.Adapter<GroupAdapter.GroupViewHolder>() {

    inner class GroupViewHolder(val binding: ItemGroupBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemGroupBinding.inflate(layoutInflater, parent, false)
        return GroupViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val group = groupList[position]
        holder.binding.groupNameTextView.text = group.name
        holder.binding.groupDateTextView.text = group.dates

        // Optional: Set click listeners for options button
        holder.binding.optionsButton.setOnClickListener {
            // Handle options button click
        }
    }

    override fun getItemCount(): Int {
        return groupList.size
    }
}
