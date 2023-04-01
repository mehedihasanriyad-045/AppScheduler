package com.riyad.appscheduler.activity

import androidx.appcompat.widget.Toolbar
import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
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

    private lateinit var adapter : ScheduleAdapter

    var package_Name: String? = null

    @SuppressLint("NotifyDataSetChanged")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)


        val actionBar = supportActionBar
        actionBar!!.title = "Schedules"

        package_Name = intent.getStringExtra("packageName").toString()


        // Initialize views
        recyclerView = findViewById(R.id.appointments_rv)
        fab = findViewById(R.id.add_appointment_fab)

        // Initialize database helper
        databaseHelper = DatabaseHelper(this)


        val onItemClickListener = object : ScheduleAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val schedule = adapter.getItem(position)


            }

            override fun onDeleteClick(position: Int) {
                // Handle delete button click
                val schedule = adapter.getItem(position)
                // Remove the selected schedule from the database
                databaseHelper.deleteSchedule(schedule.notificationId)
                // Remove the selected schedule from the adapter
                adapter.removeItem(position)

                cancelNotificationFromAlarmManager(schedule.notificationId)



            }

            override fun onEditClick(position: Int) {
                // Handle edit button click
                val schedule = adapter.getItem(position)

                editSchedule(schedule)

            }
        }

        adapter = ScheduleAdapter(databaseHelper.getSchedulesByPackageName(package_Name.toString()) as MutableList<Schedule>, onItemClickListener)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()

        // Setup FAB click listener
        fab.setOnClickListener {
            showAddDialog(adapter)
        }


    }

    @SuppressLint("NotifyDataSetChanged")
    private fun editSchedule(schedule: Schedule) {

        val databaseHelper = DatabaseHelper(this)

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = schedule.time
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

                        // Update the schedule in the database
                        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                        val dateString = dateFormat.format(calendar.time)

                        schedule.time = calendar.timeInMillis
                        schedule.timeStr = dateString

                        databaseHelper.updateSchedule(schedule)

                        cancelNotificationFromAlarmManager(notificationId = schedule.notificationId)

                        val notificationIntent = Intent(this, NotificationReceiver::class.java)
                        notificationIntent.putExtra("notificationId", schedule.notificationId)
                        notificationIntent.putExtra("packageName", intent.getStringExtra("packageName"))


                        val requestCode = schedule.notificationId

                        val pendingIntent = PendingIntent.getBroadcast(this, requestCode, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)

                        // Schedule the notification using the AlarmManager
                        val alarmManager = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)


                        // Refresh the schedule list
                        adapter.updateData(databaseHelper.getSchedulesByPackageName(package_Name.toString()))
                        adapter.notifyDataSetChanged()

                        // Show a success message to the user
                        Toast.makeText(this, "Schedule updated!", Toast.LENGTH_SHORT).show()
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

    private fun cancelNotificationFromAlarmManager(notificationId: Int) {


        // Get a reference to the AlarmManager
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Create an intent that has the same parameters as the one used to schedule the notification
        val intent = Intent(applicationContext, NotificationReceiver::class.java)
        intent.putExtra("notificationId", notificationId) // pass the notification id as an extra

        // Create a new PendingIntent with the same parameters as the original PendingIntent
        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Cancel the alarm using the new PendingIntent
        alarmManager.cancel(pendingIntent)

        // Cancel the notification using the NotificationManager
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(notificationId)




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

                        val notificationIntent = Intent(this, NotificationReceiver::class.java)
                        notificationIntent.putExtra("notificationId", notificationId)
                        notificationIntent.putExtra("packageName", intent.getStringExtra("packageName"))


                        val requestCode = notificationId

                        val pendingIntent = PendingIntent.getBroadcast(this, requestCode, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)

                        // Schedule the notification using the AlarmManager
                        val alarmManager = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)



                        val schedule = Schedule(package_Name.toString(),calendar.timeInMillis, dateString, notificationId, 0)

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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.schedule_menu, menu)
        return true
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_previous -> {
                // Handle Previous action
                val intent = Intent(this, PreviousScheduledActivity::class.java)
                intent.putExtra("packageName", package_Name)
                startActivity(intent)
                return true
            }
            R.id.action_add -> {
                // Handle Delete action
                showAddDialog(adapter)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }



}