package com.riyad.appscheduler.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.riyad.appscheduler.R
import com.riyad.appscheduler.database.DatabaseHelper
import com.riyad.appscheduler.model.Schedule

class ScheduleAdapter(
    private var scheduleList: MutableList<Schedule>,
    private val onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(position: Int)
        fun onDeleteClick(position: Int)
        fun onEditClick(position: Int)
    }

    inner class ScheduleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val dateTv: TextView = itemView.findViewById(R.id.date_tv)
        val timeTextView: TextView = itemView.findViewById(R.id.time_tv)
        val editButton: ImageButton = itemView.findViewById(R.id.edit_button)
        val deleteButton: ImageButton = itemView.findViewById(R.id.delete_button)

        init {
            itemView.setOnClickListener(this)
            editButton.setOnClickListener { onItemClickListener.onEditClick(adapterPosition) }
            deleteButton.setOnClickListener { onItemClickListener.onDeleteClick(adapterPosition) }
        }

        override fun onClick(v: View?) {
            onItemClickListener.onItemClick(adapterPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.schedule_item, parent, false)
        return ScheduleViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        val currentItem = scheduleList[position]
        val parts = currentItem.timeStr.split(" ")
        holder.dateTv.text = "Date : "+parts[0]
        holder.timeTextView.text = "Time : "+parts[1]
    }

    override fun getItemCount(): Int = scheduleList.size

    fun getItem(position: Int): Schedule {
        return scheduleList[position]
    }

    fun removeItem(position: Int) {
        scheduleList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, scheduleList.size)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(scheduleList: List<Schedule>) {
        val newList = ArrayList<Schedule>()
        newList.addAll(scheduleList)
        this.scheduleList = newList
        notifyDataSetChanged()
    }
}
