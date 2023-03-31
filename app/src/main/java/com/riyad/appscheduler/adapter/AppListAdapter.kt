package com.riyad.appscheduler.adapter

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.riyad.appscheduler.R
import com.riyad.appscheduler.activity.ScheduleActivity
import com.riyad.appscheduler.model.AppInfo
import java.util.*

class AppListAdapter(private val appList: List<AppInfo>) : RecyclerView.Adapter<AppListAdapter.AppListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.app_list_item, parent, false)
        return AppListViewHolder(view)
    }

    override fun getItemCount(): Int {
        //Log.d("riyad_app", "onCreate: "+appList.size)
        return appList.size
    }

    override fun onBindViewHolder(holder: AppListViewHolder, position: Int) {
        val appInfo = appList[position]
        holder.appIconImageView.setImageDrawable(appInfo.appIcon)
        holder.appNameTextView.text = appInfo.appName

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, ScheduleActivity::class.java)
            intent.putExtra("packageName", appInfo.packageName)
            context.startActivity(intent)
        }
    }

    inner class AppListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val appIconImageView: ImageView = itemView.findViewById(R.id.appIconImageView)
        val appNameTextView: TextView = itemView.findViewById(R.id.appNameTextView)
    }
}
