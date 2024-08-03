package com.example.hackathon_guru

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class GroupAdapter(
    private val groupList: List<TravelGroup>,
    private val onGroupAction: (TravelGroup, String) -> Unit
) : RecyclerView.Adapter<GroupAdapter.GroupViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_group, parent, false)
        return GroupViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val currentGroup = groupList[position]
        holder.groupNameTextView.text = currentGroup.name
        holder.groupDatesTextView.text = currentGroup.dates

        // Clear existing member views
        holder.groupMembersLayout.removeAllViews()

        // Dynamically add member views
        for (member in currentGroup.members) {
            val memberView = LayoutInflater.from(holder.itemView.context)
                .inflate(R.layout.item_member, holder.groupMembersLayout, false) as ImageView
            // Set member image or placeholder
            memberView.setImageResource(R.drawable.ic_user_circle) // 기본 이미지 설정
            holder.groupMembersLayout.addView(memberView)
        }

        // Set background color based on whether the trip is in the past
        if (currentGroup.isPast) {
            holder.itemView.setBackgroundResource(R.drawable.past_group) // 과거 여행일 경우 어두운 색
        } else {
            holder.itemView.setBackgroundResource(R.drawable.future_group) // 미래 여행일 경우 밝은 색
        }

        // Set click listener for edit icon
        holder.editIcon.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, GroupDetailActivity::class.java).apply {
                putExtra("groupName", currentGroup.name)
                putExtra("groupDates", currentGroup.dates)
                putExtra("groupMembers", currentGroup.members.toTypedArray())
            }
            context.startActivity(intent)
        }

        // Set long click listener for item view
        holder.itemView.setOnLongClickListener {
            showGroupOptions(holder.itemView, currentGroup)
            true
        }
    }

    override fun getItemCount() = groupList.size

    private fun showGroupOptions(view: View, group: TravelGroup) {
        val popup = android.widget.PopupMenu(view.context, view)
        popup.inflate(R.menu.group_options_menu)
        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.edit_group -> onGroupAction(group, "edit")
                R.id.delete_group -> onGroupAction(group, "delete")
            }
            true
        }
        popup.show()
    }

    inner class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val groupNameTextView: TextView = itemView.findViewById(R.id.groupNameTextView)
        val groupDatesTextView: TextView = itemView.findViewById(R.id.groupDatesTextView)
        val groupMembersLayout: LinearLayout = itemView.findViewById(R.id.groupMembersLayout)
        val editIcon: ImageView = itemView.findViewById(R.id.editIcon)
    }
}
