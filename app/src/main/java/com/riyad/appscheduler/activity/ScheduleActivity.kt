package com.riyad.appscheduler.activity

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.riyad.appscheduler.R
import com.riyad.appscheduler.adapter.ScheduleAdapter
import com.riyad.appscheduler.database.DatabaseHelper
import com.riyad.appscheduler.model.Schedule
import com.riyad.appscheduler.receiver.NotificationReceiver
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.absoluteValue


class ScheduleActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var fab: FloatingActionButton

    var package_Name: String? = null

    @SuppressLint("NotifyDataSetChanged")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)
        package_Name = intent.getStringExtra("packageName").toString()

        Log.d("riyad_app", "onCreate: "+package_Name)

        // Initialize views
        recyclerView = findViewById(R.id.appointments_rv)
        fab = findViewById(R.id.add_appointment_fab)

        // Initialize database helper
        databaseHelper = DatabaseHelper(this)


        val onItemClickListener = object : ScheduleAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                // Handle item click
            }

            override fun onDeleteClick(position: Int) {
                // Handle delete button click
            }

            override fun onEditClick(position: Int) {
                // Handle edit button click
            }
        }

        val adapter = ScheduleAdapter(databaseHelper.getSchedulesByPackageName(package_Name.toString()), onItemClickListener)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()

        // Setup FAB click listener
        fab.setOnClickListener {
            showAddDialog(adapter)
        }


    }

    @SuppressLint("NotifyDataSetChanged")
    @RequiresApi(Build.VERSION_CODES.M)
    private fun showAddDialog(adapter: ScheduleAdapter) {
        val databaseHelper = DatabaseHelper(this)

        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, monthOfYear, dayOfMonth ->
                calendar.set(year, monthOfYear, dayOfMonth)

                // Launch the time picker dialog
                val timePickerDialog = TimePickerDialog(
                    this,
                    { _, hourOfDay, minute ->
                        // Update the calendar with the selected time
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        calendar.set(Calendar.MINUTE, minute)

                        // Add the schedule to the database
                        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                        val dateString = dateFormat.format(calendar.time)


                        val notificationId = System.currentTimeMillis().toInt().absoluteValue
                        Log.d("riyad_app", "notification : notificationId "+ notificationId.toString())

                        val notificationIntent = Intent(this, NotificationReceiver::class.java)
                        notificationIntent.putExtra("notificationId", notificationId)
                        notificationIntent.putExtra("packageName", intent.getStringExtra("packageName"))


                        val requestCode = System.currentTimeMillis().toInt()

                        val pendingIntent = PendingIntent.getBroadcast(this, requestCode, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)

                        // Schedule the notification using the AlarmManager
                        val alarmManager = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)



                        val schedule = Schedule(package_Name.toString(),calendar.timeInMillis, dateString, notificationId)

                        databaseHelper.insertSchedule(schedule)

                        // Refresh the schedule list
                        adapter.updateData(databaseHelper.getSchedulesByPackageName(package_Name.toString()))
                        adapter.notifyDataSetChanged()

                        // Show a success message to the user
                        Toast.makeText(this, "Schedule added!", Toast.LENGTH_SHORT).show()
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    false
                )
                timePickerDialog.show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()

    }


}