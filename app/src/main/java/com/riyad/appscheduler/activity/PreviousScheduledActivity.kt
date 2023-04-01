
/**

This activity displays the list of schedules previously set for a specific app.
It retrieves the package name of the app from the intent and uses it to fetch the list of schedules from the database.
The list is displayed in a RecyclerView using an adapter. The adapter also handles deleting a schedule when the delete button is clicked.
 **/

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

/**

* This activity displays the list of schedules previously set for a specific app.
* It retrieves the package name of the app from the intent and uses it to fetch the list of schedules from the database.
* The list is displayed in a RecyclerView using an adapter. The adapter also handles deleting a schedule when the delete button is clicked.
* @property databaseHelper an instance of the [DatabaseHelper] class to manage the app's database
* @property recyclerView the [RecyclerView] used to display the list of schedules
* @property packagename the package name of the app whose schedules are being displayed
* @property adapter the adapter used to populate the [RecyclerView] with the schedules and handle item click events
 */
class PreviousScheduledActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var packagename: String

    private lateinit var adapter : PreviousScheduledAdapter


    /**
     * Initializes the activity by setting up the RecyclerView to display the list of schedules previously set for a specific app.
     * It retrieves the package name of the app from the intent and uses it to fetch the list of schedules from the database.
     * The list is displayed in a RecyclerView using an adapter.
     * The adapter also handles deleting a schedule when the delete button is clicked.
     */

    @SuppressLint( "NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_previous_scheduled)

        // Get package name from intent
        packagename = intent.getStringExtra("packageName").toString();

        // Find RecyclerView in layout file
        recyclerView = findViewById(R.id.previous_schedule)

        // Initialize database helper
        databaseHelper = DatabaseHelper(this)

        // Initialize adapter and set item click listener
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

        // Set RecyclerView layout manager and adapter
        adapter = PreviousScheduledAdapter(databaseHelper.getPreviousSchedulesByPackageName(packagename) as MutableList<Schedule>, onItemClickListener)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()



    }
}