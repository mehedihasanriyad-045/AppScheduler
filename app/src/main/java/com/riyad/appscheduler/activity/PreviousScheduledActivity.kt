package com.riyad.appscheduler.activity

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.riyad.appscheduler.R
import com.riyad.appscheduler.adapter.PreviousScheduledAdapter
import com.riyad.appscheduler.adapter.ScheduleAdapter
import com.riyad.appscheduler.database.DatabaseHelper
import com.riyad.appscheduler.model.Schedule

class PreviousScheduledActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var packagename: String

    private lateinit var adapter : PreviousScheduledAdapter


    @SuppressLint( "NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_previous_scheduled)


        packagename = intent.getStringExtra("packageName").toString();

        recyclerView = findViewById(R.id.previous_schedule)




        databaseHelper = DatabaseHelper(this)

        val onItemClickListener = object : PreviousScheduledAdapter.OnItemClickListener {

            override fun onDeleteClick(position: Int) {
                // Handle delete button click
                val schedule = adapter.getItem(position)
                // Remove the selected schedule from the database
                databaseHelper.deleteSchedule(schedule.notificationId)
                // Remove the selected schedule from the adapter
                adapter.removeItem(position)




            }


        }

        adapter = PreviousScheduledAdapter(databaseHelper.getPreviousSchedulesByPackageName(packagename) as MutableList<Schedule>, onItemClickListener)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()



    }
}