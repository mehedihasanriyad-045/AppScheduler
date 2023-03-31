package com.riyad.appscheduler.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.riyad.appscheduler.database.DatabaseHelper

class NotificationTapActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val databaseHelper = DatabaseHelper(this)

        val packageName = intent?.getStringExtra("packageName").toString()
        val notificationId = intent?.getStringExtra("notificationId").toString()

        //databaseHelper.updateScheduleAction(notificationId.toInt(), 1)

        val packageManager = this.packageManager
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        val appIcon = packageManager.getApplicationIcon(packageInfo.applicationInfo)

        val appInfo = packageManager.getApplicationInfo(packageName, 0)
        val appName = packageManager.getApplicationLabel(appInfo).toString()
        val intent = packageManager.getLaunchIntentForPackage(packageName)

        if (intent != null) {
            startActivity(intent)
        }



    }
}