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

/**

* Adapter class for the RecyclerView in the ScheduleFragment.
* @param scheduleList The list of Schedule objects to be displayed in the RecyclerView.
* @param onItemClickListener The click listener for the items in the RecyclerView.
 */
class ScheduleAdapter(
    private var scheduleList: MutableList<Schedule>,
    private val onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder>() {

    /**
     * Interface for item click events in the RecyclerView.
     */

    interface OnItemClickListener {
        fun onItemClick(position: Int)
        fun onDeleteClick(position: Int)
        fun onEditClick(position: Int)
    }

    /**
     * ViewHolder for the items in the RecyclerView.
     */
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

    /**
     * Binds the data to the ViewHolder.
     */
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        val currentItem = scheduleList[position]
        val parts = currentItem.timeStr.split(" ")
        holder.dateTv.text = "Date : "+parts[0]
        holder.timeTextView.text = "Time : "+parts[1]
    }

    /**
     * Returns the Schedule object at the specified position.
     *
     * @param position The position of the Schedule object to retrieve.
     * @return The Schedule object at the specified position.
     */
    override fun getItemCount(): Int = scheduleList.size

    fun getItem(position: Int): Schedule {
        return scheduleList[position]
    }

    /**
     * Removes the item at the specified position from the list and updates the RecyclerView.
     *
     * @param position The position of the item to remove.
     */
    fun removeItem(position: Int) {
        scheduleList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, scheduleList.size)
    }
    /**
     * Updates the list of Schedule objects and updates the RecyclerView.
     *
     * @param scheduleList The new list of Schedule objects to display in the RecyclerView.
     */
    @SuppressLint("NotifyDataSetChanged")
    fun updateData(scheduleList: List<Schedule>) {
        val newList = ArrayList<Schedule>()
        newList.addAll(scheduleList)
        this.scheduleList = newList
        notifyDataSetChanged()
    }
}
