package com.riyad.appscheduler.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Log
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

class PreviousScheduledAdapter(
    private var scheduleList: MutableList<Schedule>,
    private val onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<PreviousScheduledAdapter.PreviousScheduleViewHolder>() {

    interface OnItemClickListener {
        fun onDeleteClick(position: Int)
    }

    inner class PreviousScheduleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val dateTv: TextView = itemView.findViewById(R.id.date_tv)
        val timeTextView: TextView = itemView.findViewById(R.id.time_tv)
        val actionTv: TextView = itemView.findViewById(R.id.performed_action_tv)
        val deleteButton: ImageButton = itemView.findViewById(R.id.delete_button)

        init {
            itemView.setOnClickListener(this)
            deleteButton.setOnClickListener { onItemClickListener.onDeleteClick(adapterPosition) }
        }

        override fun onClick(p0: View?) {

        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PreviousScheduleViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.previous_scheduled_item, parent, false)
        return PreviousScheduleViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: PreviousScheduleViewHolder, position: Int) {
        val currentItem = scheduleList[position]
        val parts = currentItem.timeStr.split(" ")
        holder.dateTv.text = "Date : "+parts[0]
        holder.timeTextView.text = "Time : "+parts[1]
        if(currentItem.isTapped == 0){
            holder.actionTv.text = "No Action"
            holder.actionTv.setTextColor(Color.RED)

        }else{
            holder.actionTv.text = "Yes"
            holder.actionTv.setTextColor(Color.BLUE)

        }
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
