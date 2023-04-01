package com.riyad.appscheduler.activity

import androidx.appcompat.widget.Toolbar
import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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


/**

The ScheduleActivity class is responsible for displaying the list of schedules for a specific package.

@property databaseHelper an instance of [DatabaseHelper] used to interact with the SQLite database.

@property recyclerView the [RecyclerView] used to display the schedules.

@property fab the [FloatingActionButton] used to add new schedules.

@property adapter the [ScheduleAdapter] used to populate the [recyclerView] with data.

@property package_Name the package name of the application whose schedules are being displayed.
 */

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
        actionBar.setBackgroundDrawable(ColorDrawable(Color.BLACK))


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

            /**
             * This method is called when the delete button of an item in the [recyclerView] is clicked.
             * It removes the selected [Schedule] from the database and the [adapter].
             *
             * @param position the position of the clicked item.
             */


            override fun onDeleteClick(position: Int) {
                // Handle delete button click
                val schedule = adapter.getItem(position)
                // Remove the selected schedule from the database
                databaseHelper.deleteSchedule(schedule.notificationId)
                // Remove the selected schedule from the adapter
                adapter.removeItem(position)

                cancelNotificationFromAlarmManager(schedule.notificationId)



            }


            /**
             * This method is called when the edit button of an item in the [recyclerView] is clicked.
             * It opens the edit dialog for the selected [Schedule].
             *
             * @param position the position of the clicked item.
             */

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


    /**
     * Edit the given schedule in the database.
     *
     * @param schedule the schedule to edit
     */
    @SuppressLint("NotifyDataSetChanged")
    private fun editSchedule(schedule: Schedule) {

        // Get a reference to the database helper
        val databaseHelper = DatabaseHelper(this)

        // Create a new calendar instance with the time set to the schedule's time

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = schedule.time

        // Create a new DatePickerDialog to select the date
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

                        // Format the date as a string
                        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                        val dateString = dateFormat.format(calendar.time)

                        // Update the schedule's time and time string in the database
                        schedule.time = calendar.timeInMillis
                        schedule.timeStr = dateString
                        databaseHelper.updateSchedule(schedule)

                        // Cancel any existing notifications for this schedule
                        cancelNotificationFromAlarmManager(notificationId = schedule.notificationId)

                        // Create a new notification intent with the schedule's notification ID and package name
                        val notificationIntent = Intent(this, NotificationReceiver::class.java)
                        notificationIntent.putExtra("notificationId", schedule.notificationId)
                        notificationIntent.putExtra("packageName", intent.getStringExtra("packageName"))

                        // Create a new PendingIntent with the notification intent
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

    /**
     * Cancel the notification with the given ID using the AlarmManager and NotificationManager.
     *
     * @param notificationId the ID of the notification to cancel
     */
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


    /**

    * Shows a dialog for adding a new schedule to the database. The schedule is added to the database and the schedule list is refreshed.

    * @param adapter the adapter for the schedule list

    * @SuppressLint("NotifyDataSetChanged") suppresses lint warnings related to calling notifyDataSetChanged on the adapter

    * @RequiresApi(Build.VERSION_CODES.M) requires API level M or higher
     */
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

                        // Create a unique notification ID based on the current time
                        val notificationId = System.currentTimeMillis().toInt().absoluteValue

                        // Create an intent for the notification receiver and add extras for the notification ID and package name
                        val notificationIntent = Intent(this, NotificationReceiver::class.java)
                        notificationIntent.putExtra("notificationId", notificationId)
                        notificationIntent.putExtra("packageName", intent.getStringExtra("packageName"))

                        // Create a unique request code for the pending intent based on the notification ID
                        val requestCode = notificationId
                        val pendingIntent = PendingIntent.getBroadcast(this, requestCode, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)

                        // Schedule the notification using the AlarmManager
                        val alarmManager = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)


                        // Create a new schedule object and insert it into the database
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


    /**

    * Inflates the menu resource and adds items to the app bar if present.
    * @param menu The options menu in which items are placed
    * @return true for the menu to be displayed, false for the menu to not be displayed
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.schedule_menu, menu)
        return true
    }

    /**

    * Handles the click events on the menu items of the app bar.
    * @param item The clicked item in the menu
    * @return true if the item click event is handled, false otherwise
     */
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