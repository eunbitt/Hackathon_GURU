package com.example.hackathon_guru

import android.app.DatePickerDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*
import com.example.hackathon_guru.Schedule


class ScheduleAdapter(
    private var scheduleList: List<Schedule>, // 변경된 부분
    private val onAddScheduleClick: (Schedule) -> Unit
) : RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder>() {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_schedule, parent, false)
        return ScheduleViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        val currentSchedule = scheduleList[position]
        holder.scheduleTitle.text = currentSchedule.title
        holder.scheduleDate.text = currentSchedule.date

        if (currentSchedule.title == "일정을 추가하세요") {
            holder.addScheduleLayout.visibility = View.VISIBLE
            holder.scheduleInfoLayout.visibility = View.GONE
            holder.toggleSchedule.visibility = View.GONE
            holder.editSchedule.visibility = View.GONE
        } else {
            holder.addScheduleLayout.visibility = View.GONE
            holder.scheduleInfoLayout.visibility = View.VISIBLE
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
                showEditDeleteDialog(holder.itemView, position, currentSchedule)
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

    // 새로운 메서드 추가: 데이터 업데이트
    fun updateSchedules(newSchedules: List<Schedule>) {
        scheduleList = newSchedules
        notifyDataSetChanged()
    }

    private fun showEditDeleteDialog(view: View, position: Int, schedule: Schedule) {
        val options = arrayOf("수정", "삭제")
        val builder = AlertDialog.Builder(view.context)
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> editSchedule(view, position, schedule) // 일정 수정
                1 -> deleteSchedule(position) // 일정 삭제
            }
        }
        builder.show()
    }

    private fun editSchedule(view: View, position: Int, schedule: Schedule) {
        val dialogView = LayoutInflater.from(view.context).inflate(R.layout.dialog_add_schedule, null)
        val scheduleDateInput = dialogView.findViewById<TextInputEditText>(R.id.scheduleDateInput)
        val scheduleTitleInput = dialogView.findViewById<TextInputEditText>(R.id.scheduleTitleInput)
        val scheduleLocationInput = dialogView.findViewById<TextInputEditText>(R.id.scheduleLocationInput)
        val scheduleCommentInput = dialogView.findViewById<TextInputEditText>(R.id.scheduleCommentInput)

        // 기존 일정 정보를 다이얼로그에 설정
        scheduleDateInput.setText(schedule.date)
        scheduleTitleInput.setText(schedule.title)
        scheduleLocationInput.setText(schedule.location)
        scheduleCommentInput.setText(schedule.comment)

        // 날짜 선택 클릭 리스너 설정
        scheduleDateInput.setOnClickListener {
            showDatePickerDialog(view.context, scheduleDateInput)
        }

        AlertDialog.Builder(view.context)
            .setTitle("일정 수정")
            .setView(dialogView)
            .setPositiveButton("수정") { dialog, _ ->
                val date = scheduleDateInput.text.toString()
                val title = scheduleTitleInput.text.toString()
                val location = scheduleLocationInput.text.toString()
                val comment = scheduleCommentInput.text.toString()

                if (title.isNotEmpty() && date.isNotEmpty()) {
                    schedule.date = date
                    schedule.title = title
                    schedule.location = location
                    schedule.comment = comment
                    notifyItemChanged(position)
                }
                dialog.dismiss()
            }
            .setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun deleteSchedule(position: Int) {
        scheduleList = scheduleList.toMutableList().apply { removeAt(position) } // 변경된 부분
        notifyItemRemoved(position)
    }

    private fun showDatePickerDialog(context: Context, dateInput: TextInputEditText) {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                dateInput.setText(dateFormat.format(calendar.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    inner class ScheduleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val scheduleTitle: TextView = itemView.findViewById(R.id.scheduleTitle)
        val scheduleDate: TextView = itemView.findViewById(R.id.scheduleDate)
        val toggleSchedule: ImageView = itemView.findViewById(R.id.toggleSchedule)
        val editSchedule: ImageView = itemView.findViewById(R.id.editSchedule)
        val scheduleDetails: LinearLayout = itemView.findViewById(R.id.scheduleDetails)
        val addSchedule: ImageView = itemView.findViewById(R.id.addSchedule)
        val scheduleLocation: TextView = itemView.findViewById(R.id.scheduleLocation)
        val scheduleComment: TextView = itemView.findViewById(R.id.scheduleComment)
        val addScheduleLayout: LinearLayout = itemView.findViewById(R.id.addScheduleLayout)
        val scheduleInfoLayout: LinearLayout = itemView.findViewById(R.id.scheduleInfoLayout)
    }

    // 항목을 이동하는 기능을 위해 ItemTouchHelper.Callback 구현
    val itemTouchHelperCallback = object : ItemTouchHelper.Callback() {
        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
            return makeMovementFlags(dragFlags, 0)
        }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            val fromPosition = viewHolder.adapterPosition
            val toPosition = target.adapterPosition
            Collections.swap(scheduleList.toMutableList(), fromPosition, toPosition)
            notifyItemMoved(fromPosition, toPosition)
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            // 스와이프 동작을 지원하지 않음
        }

        override fun isLongPressDragEnabled(): Boolean {
            return true
        }
    }
}
