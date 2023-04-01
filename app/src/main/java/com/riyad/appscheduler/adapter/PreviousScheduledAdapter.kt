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


/**

* Adapter class for the recycler view in the "Previous Schedule" activity.

* This adapter is used to display a list of previously scheduled tasks along with their details.

* @param scheduleList the list of schedules to be displayed

* @param onItemClickListener listener for handling click events on the delete button for a schedule
 */

class PreviousScheduledAdapter(
    private var scheduleList: MutableList<Schedule>,
    private val onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<PreviousScheduledAdapter.PreviousScheduleViewHolder>() {

    interface OnItemClickListener {
        fun onDeleteClick(position: Int)
    }

    /**

    View holder class for a single item in the recycler view.

    This class holds the views for the various details of a schedule.
     */
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

    /**

    * Binds the details of a schedule to the corresponding views in the view holder.

    * @param holder the view holder to which the details are to be bound

    * @param position the position of the schedule in the list
     */
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
    /**

    * Gets the schedule at a particular position in the list.
    * @param position the position of the schedule in the list
    * @return the schedule at the specified position
     */
    override fun getItemCount(): Int = scheduleList.size

    fun getItem(position: Int): Schedule {
        return scheduleList[position]
    }

    /**

    * Removes a schedule from the list and updates the view accordingly.
    * @param position the position of the schedule to be removed
     */
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
