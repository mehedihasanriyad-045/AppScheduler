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


/**

* This class represents a broadcast receiver that receives notifications from the system and
* handles them by creating and showing a notification in the notification bar.
* The [onReceive] method is called when a notification is received. The method extracts
* the notification ID, package name, app icon, and app name from the received intent and creates
* a PendingIntent to launch the app when the notification is clicked. Then, it creates a
* notification using the NotificationCompat API and shows it using the NotificationManagerCompat API.
 */
class NotificationReceiver : BroadcastReceiver() {

    /**
     * This method is called when a notification is received. It extracts the notification ID,
     * package name, app icon, and app name from the received intent and creates a PendingIntent
     * to launch the app when the notification is clicked. Then, it creates a notification using
     * the NotificationCompat API and shows it using the NotificationManagerCompat API.
     *
     * @param context the context in which the receiver is running.
     * @param intent the received intent containing the notification details.
     */

    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {

            val notificationId = intent?.getIntExtra("notificationId", 0) ?: 0

            // Get app name from package name
            val packageName = intent?.getStringExtra("packageName").toString()
            val packageManager = context.packageManager
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            val appIcon = packageManager.getApplicationIcon(packageInfo.applicationInfo)

            val appInfo = packageManager.getApplicationInfo(packageName, 0)
            val appName = packageManager.getApplicationLabel(appInfo).toString()

            // Create intent to launch app when notification is clicked
            val intents = Intent(context, NotificationTapActivity::class.java)
            intents.putExtra("packageName", packageName)
            intents.putExtra("notificationId", notificationId)

            // Get launch intent for package
            val intent = packageManager.getLaunchIntentForPackage(packageName)
            intent?.flags = Intent.FLAG_ACTIVITY_NEW_TASK

            val requestCode = System.currentTimeMillis().toInt()

            // Create a PendingIntent with the Intent
            val pendingIntent = PendingIntent.getActivity(context, requestCode, intents, PendingIntent.FLAG_UPDATE_CURRENT)


            // Create the notification
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
