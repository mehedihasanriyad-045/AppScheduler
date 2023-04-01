/**

This activity is launched when the user taps on a notification from the app. It retrieves the package name and notification ID from the intent,
and uses them to update the status of the schedule associated with the notification in the database.
It then launches the app by getting the launch intent using the package name.
 */

package com.riyad.appscheduler.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.riyad.appscheduler.database.DatabaseHelper

class NotificationTapActivity : AppCompatActivity() {

    /**
     * Initializes the activity by retrieving the package name and notification ID from the intent.
     * It then updates the status of the schedule associated with the notification in the database.
     * Finally, it launches the app by getting the launch intent using the package name.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize database helper
        val databaseHelper = DatabaseHelper(this)

        // Get package name and notification ID from intent
        val packageName = intent?.getStringExtra("packageName").toString()
        val notificationId = intent?.getIntExtra("notificationId", 0)

        // Update the status of the schedule associated with the notification in the database
        if (notificationId != null) {
            databaseHelper.updateScheduleAction(notificationId.toInt(), 1)
        }


        // Get app icon and name using package name
        val packageManager = this.packageManager
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        val appIcon = packageManager.getApplicationIcon(packageInfo.applicationInfo)
        val appInfo = packageManager.getApplicationInfo(packageName, 0)
        val appName = packageManager.getApplicationLabel(appInfo).toString()

        // Launch the app by getting the launch intent using the package name
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        if (intent != null) {
            startActivity(intent)
        }



    }
}