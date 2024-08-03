package com.example.hackathon_guru

import Schedule
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ScheduleAdapter(
    private val scheduleList: MutableList<Schedule>,
    private val onAddScheduleClick: (Schedule) -> Unit
) : RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_schedule, parent, false)
        return ScheduleViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        val currentSchedule = scheduleList[position]
        holder.scheduleTitle.text = currentSchedule.title

        if (currentSchedule.title == "일정을 추가하세요") {
            holder.addSchedule.visibility = View.VISIBLE
            holder.toggleSchedule.visibility = View.GONE
            holder.editSchedule.visibility = View.GONE
        } else {
            holder.addSchedule.visibility = View.GONE
            holder.toggleSchedule.visibility = View.VISIBLE
            holder.editSchedule.visibility = View.VISIBLE

            holder.scheduleDetails.visibility = if (currentSchedule.isExpanded) View.VISIBLE else View.GONE
            holder.scheduleLocation.text = currentSchedule.location
            holder.scheduleComment.text = currentSchedule.comment
            holder.toggleSchedule.setImageResource(
                if (currentSchedule.isExpanded) R.drawable.ic_expand_less else R.drawable.ic_expand_more
            )

            holder.toggleSchedule.setOnClickListener {
                currentSchedule.isExpanded = !currentSchedule.isExpanded
                notifyItemChanged(position)
            }

            holder.editSchedule.setOnClickListener {
                // Edit schedule logic here
            }
        }

        // Add schedule click handling
        holder.addSchedule.setOnClickListener {
            if (currentSchedule.title == "일정을 추가하세요") {
                onAddScheduleClick(currentSchedule)
            }
        }
    }

    override fun getItemCount() = scheduleList.size

    inner class ScheduleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val scheduleTitle: TextView = itemView.findViewById(R.id.scheduleTitle)
        val toggleSchedule: ImageView = itemView.findViewById(R.id.toggleSchedule)
        val editSchedule: ImageView = itemView.findViewById(R.id.editSchedule)
        val scheduleDetails: LinearLayout = itemView.findViewById(R.id.scheduleDetails)
        val addSchedule: ImageView = itemView.findViewById(R.id.addSchedule)
        val scheduleLocation: TextView = itemView.findViewById(R.id.scheduleLocation)
        val scheduleComment: TextView = itemView.findViewById(R.id.scheduleComment)
    }
}
