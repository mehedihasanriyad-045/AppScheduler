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

    @RequiresApi(Build.VERSION_CODES.M)
    val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            Log.d("riyad_" +
                    "" +
                    "app", "Request permission: " + result.resultCode)
        }

    @SuppressLint("UnspecifiedImmutableFlag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(!AutoStartPermissionHelper.getInstance().isAutoStartPermissionAvailable(this)){
            AutoStartPermissionHelper.getInstance().getAutoStartPermission(this)
        }

        //requestAlertPermission()

        createNotificationChannel();




        val appListRecyclerView = findViewById<RecyclerView>(R.id.appListRecyclerView)
        appListRecyclerView.layoutManager = LinearLayoutManager(this)
        val packageManager = packageManager
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        val resolveInfoList = packageManager.queryIntentActivities(intent, 0)

        val appList = mutableListOf<AppInfo>()

        for (resolveInfo in resolveInfoList) {
            val appName = resolveInfo.loadLabel(packageManager).toString()
            val packageName = resolveInfo.activityInfo.packageName
            val appIcon = resolveInfo.loadIcon(packageManager)
            appList.add(AppInfo(appName, packageName, appIcon))
        }

        val appListAdapter = AppListAdapter(appList)
        appListRecyclerView.adapter = appListAdapter

    }

    private fun createNotificationChannel() {
        // Create a notification channel
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "app_scheduler"
            val channelName = "App Scheduler"
            val channelDescription = "My Channel Description"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = channelDescription
            }

            // Register the notification channel with the system
            val notificationManager = this.getSystemService(NotificationManager::class.java) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }*/

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

    fun requestAlertPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // check if we already  have permission to draw over other apps
            if (!Settings.canDrawOverlays(this)) {
                val alert = AlertDialog.Builder(this)
                alert.setTitle("Play In background")
                alert.setMessage("Please allow the app to run in the background so the app functions work properly.")
                alert.setPositiveButton("Ok") { _: DialogInterface?, _: Int ->
                    requestDrawOverlay()
                }
                alert.setNegativeButton("Cancel") { dialog: DialogInterface, _: Int -> finish() }
                alert.show()
            }
            requestBatteryOptimizationPermission()
        }

    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestDrawOverlay() {
        // if not construct intent to request permission
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:" + applicationContext.packageName)
        )
        resultLauncher.launch(intent)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun requestBatteryOptimizationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
            intent.data = Uri.parse("package:" + this.applicationContext.packageName)
            checkIntentAndStart(this, intent)
        } else {
            val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
            if (checkIntentAndStart(this, intent))
                Toast.makeText(
                    this,
                    "Please enable battery optimizations switch",
                    Toast.LENGTH_LONG
                ).show()
        }
    }

    private fun checkIntentAndStart(context: Context, intent: Intent): Boolean {
        intent.resolveActivity(context.packageManager)?.let {
            context.startActivity(intent)
            return true
        }

        return false
    }





}