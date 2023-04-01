package com.riyad.appscheduler.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.riyad.appscheduler.R
import com.riyad.appscheduler.activity.NotificationTapActivity
import com.riyad.appscheduler.utils.NotificationHelper

class NotificationReceiver : BroadcastReceiver() {


    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {

            val notificationId = intent?.getIntExtra("notificationId", 0) ?: 0

            val packageName = intent?.getStringExtra("packageName").toString()
            val packageManager = context.packageManager
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            val appIcon = packageManager.getApplicationIcon(packageInfo.applicationInfo)

            val appInfo = packageManager.getApplicationInfo(packageName, 0)
            val appName = packageManager.getApplicationLabel(appInfo).toString()

            val intents = Intent(context, NotificationTapActivity::class.java)
            intents.putExtra("packageName", packageName)
            intents.putExtra("notificationId", notificationId)

            val intent = packageManager.getLaunchIntentForPackage(packageName)
            intent?.flags = Intent.FLAG_ACTIVITY_NEW_TASK

            val requestCode = System.currentTimeMillis().toInt()

            // Create a PendingIntent with the Intent
            val pendingIntent = PendingIntent.getActivity(context, requestCode, intents, PendingIntent.FLAG_UPDATE_CURRENT)







            val notificationContent = "Click to open.."

            // Create the notification
            val notification = NotificationCompat.Builder(it, NotificationHelper.CHANNEL_ID)
                .setContentTitle(appName)
                .setContentText(notificationContent)
                .setSmallIcon(R.drawable.ic_notifications)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()

            // Show the notification
            val notificationManager = NotificationManagerCompat.from(it)
            notificationManager.notify(notificationId.toString(), notificationId, notification)
        }
    }




}
