/**

The MainActivity class displays a list of all installed applications on the user's device that can be selected and scheduled to open at a later time via a notification.

 **/

package com.riyad.appscheduler

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import android.widget.Toolbar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.judemanutd.autostarter.AutoStartPermissionHelper
import com.riyad.appscheduler.adapter.AppListAdapter
import com.riyad.appscheduler.model.AppInfo
import com.riyad.appscheduler.utils.NotificationHelper
import java.util.*

class MainActivity : AppCompatActivity() {

    // Method to initialize activity
    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("UnspecifiedImmutableFlag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Create notification channel
        createNotificationChannel();


        // Set up RecyclerView for displaying list of apps
        val appListRecyclerView = findViewById<RecyclerView>(R.id.appListRecyclerView)
        appListRecyclerView.layoutManager = LinearLayoutManager(this)

        // Get list of all installed apps
        val packageManager = packageManager
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        val resolveInfoList = packageManager.queryIntentActivities(intent, 0)

        val appList = mutableListOf<AppInfo>()

        // Add app information to appList
        for (resolveInfo in resolveInfoList) {
            val appName = resolveInfo.loadLabel(packageManager).toString()
            val packageName = resolveInfo.activityInfo.packageName
            val appIcon = resolveInfo.loadIcon(packageManager)
            appList.add(AppInfo(appName, packageName, appIcon))
        }

        // Set up RecyclerView Adapter
        val appListAdapter = AppListAdapter(appList)
        appListRecyclerView.adapter = appListAdapter

    }

    // Method to create notification channel
    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "App Scheduler"
            val descriptionText = "Scheduling Application Notification To Open That Application"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(NotificationHelper.CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }



}