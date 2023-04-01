package com.riyad.appscheduler.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.icu.text.MessageFormat.format
import android.util.Log
import com.riyad.appscheduler.model.Application
import com.riyad.appscheduler.model.Schedule
import java.lang.String.format
import java.text.DateFormat
import java.text.MessageFormat.format


class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "ScheduleDB"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        // Create the Schedule table
        val createScheduleTable = "CREATE TABLE ${Schedule.TABLE_NAME} (" +
                "${Schedule.COLUMN_PACKAGE_NAME} TEXT , " +
                "${Schedule.COLUMN_TIME} INTEGER NOT NULL, " +
                "${Schedule.COLUMN_NOTIFICATION_ID} INTEGER NOT NULL PRIMARY KEY, " +
                "${Schedule.COLUMN_TIME_STR} TEXT NOT NULL);"
        db?.execSQL(createScheduleTable)

        // Create the Application table
        val createApplicationTable = "CREATE TABLE ${Application.TABLE_NAME} (" +
                "${Application.COLUMN_APP_NAME} TEXT NOT NULL, " +
                "${Application.COLUMN_PACKAGE_NAME} TEXT PRIMARY KEY, " +
                "${Application.COLUMN_APP_ICON} BLOB NOT NULL);"
        db?.execSQL(createApplicationTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Drop the tables if they exist and recreate them
        db?.execSQL("DROP TABLE IF EXISTS ${Schedule.TABLE_NAME}")
        db?.execSQL("DROP TABLE IF EXISTS ${Application.TABLE_NAME}")
        onCreate(db)
    }

    fun insertSchedule(schedule: Schedule): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(Schedule.COLUMN_PACKAGE_NAME, schedule.packageName)
            put(Schedule.COLUMN_TIME, schedule.time)
            put(Schedule.COLUMN_TIME_STR, schedule.timeStr)
        }
        val id = db.insert(Schedule.TABLE_NAME, null, values)
        db.close()
        Log.d("riyad_app", "insertSchedule: "+schedule.timeStr+ " "+schedule.time+" "+schedule.packageName)

        return id
    }

    @SuppressLint("Range")
    fun getAllSchedules(): List<Schedule> {
        val scheduleList = mutableListOf<Schedule>()
        val selectQuery = "SELECT * FROM ${Schedule.TABLE_NAME}"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        if (cursor.moveToFirst()) {
            do {
                val packageName = cursor.getString(cursor.getColumnIndex(Schedule.COLUMN_PACKAGE_NAME))
                val time = cursor.getLong(cursor.getColumnIndex(Schedule.COLUMN_TIME))
                val timeStr = cursor.getString(cursor.getColumnIndex(Schedule.COLUMN_TIME_STR))
                val notification_id = cursor.getInt(cursor.getColumnIndexOrThrow(Schedule.COLUMN_NOTIFICATION_ID))
                scheduleList.add(Schedule(packageName, time, timeStr, notification_id))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return scheduleList
    }

    fun getSchedulesByPackageName(packageName: String): List<Schedule> {
        val db = readableDatabase
        val schedules = mutableListOf<Schedule>()

        val currentTimeMillis = System.currentTimeMillis()
        //val currentTimeStr = DateFormat.format("hh:mm a", currentTimeMillis).toString()


        val query = "SELECT * FROM ${Schedule.TABLE_NAME} WHERE ${Schedule.COLUMN_PACKAGE_NAME} = ? AND ${Schedule.COLUMN_TIME} >=  ? ORDER BY ${Schedule.COLUMN_TIME} DESC"
        val selectionArgs = arrayOf(packageName, currentTimeMillis.toString())

        val cursor = db.rawQuery(query, selectionArgs)

        while (cursor.moveToNext()) {
            val time = cursor.getLong(cursor.getColumnIndexOrThrow(Schedule.COLUMN_TIME))
            val timeStr = cursor.getString(cursor.getColumnIndexOrThrow(Schedule.COLUMN_TIME_STR))
            val notification_id = cursor.getInt(cursor.getColumnIndexOrThrow(Schedule.COLUMN_NOTIFICATION_ID))
            schedules.add(Schedule(packageName, time, timeStr, notification_id))
        }

        cursor.close()
        return schedules
    }

    /*fun updateScheduleAction(db: SQLiteDatabase?, notificationId: Int, isTapped: Int): Int {
        val values = ContentValues().apply {
            put(Schedule.COLUMN_IS_TAPPED, isTapped)
        }
        val selection = "${Schedule.COLUMN_NOTIFICATION_ID} = ?"
        val selectionArgs = arrayOf(notificationId.toString())
        return db?.update(Schedule.TABLE_NAME, values, selection, selectionArgs) ?: 0
    }*/


    fun updateSchedule(schedule: Schedule): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(Schedule.COLUMN_TIME, schedule.time)
            put(Schedule.COLUMN_TIME_STR, schedule.timeStr)
            put(Schedule.COLUMN_NOTIFICATION_ID, schedule.notificationId)
        }
        val rows = db.update(Schedule.TABLE_NAME, values, "${Schedule.COLUMN_PACKAGE_NAME}=?", arrayOf(schedule.packageName))
        db.close()
        return rows
    }

    fun deleteSchedule(packageName: String): Int {
        val db = this.writableDatabase
        val rows = db.delete(Schedule.TABLE_NAME, "${Schedule.COLUMN_PACKAGE_NAME}=?", arrayOf(packageName))
        db.close()
        return rows
    }

    // Update a scheduled notification by notification ID
    fun updateScheduled(notificationId: Int, packageName: String, time: Long, timeStr: String) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(Schedule.COLUMN_PACKAGE_NAME, packageName)
            put(Schedule.COLUMN_TIME, time)
            put(Schedule.COLUMN_TIME_STR, timeStr)
        }
        db.update(Schedule.TABLE_NAME, values, "${Schedule.COLUMN_NOTIFICATION_ID} = ?", arrayOf(notificationId.toString()))
        db.close()
    }

    // Delete a scheduled notification by notification ID
    fun deleteScheduled(notificationId: Int) {
        val db = this.writableDatabase
        db.delete(Schedule.TABLE_NAME, "${Schedule.COLUMN_NOTIFICATION_ID} = ?", arrayOf(notificationId.toString()))
        db.close()
    }

    // Add a new application to the Application table
    fun addApplication(application: Application): Long {
        val db = this.writableDatabase

        val values = ContentValues()
        values.put(Application.COLUMN_APP_NAME, application.appName)
        values.put(Application.COLUMN_PACKAGE_NAME, application.packageName)
        values.put(Application.COLUMN_APP_ICON, application.appIcon)

        val id = db.insert(Application.TABLE_NAME, null, values)
        db.close()
        return id
    }

    // Get a single application from the Application table by package name
    @SuppressLint("Range")
    fun getApplication(packageName: String): Application? {
        val db = this.readableDatabase

        val selection = "${Application.COLUMN_PACKAGE_NAME} = ?"
        val selectionArgs = arrayOf(packageName)

        val cursor = db.query(
            Application.TABLE_NAME,
            null,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        var application: Application? = null

        if (cursor.moveToFirst()) {
            val appName = cursor.getString(cursor.getColumnIndex(Application.COLUMN_APP_NAME))
            val appIcon = cursor.getBlob(cursor.getColumnIndex(Application.COLUMN_APP_ICON))

            application = Application(appName, packageName, appIcon)
        }

        cursor.close()
        db.close()
        return application
    }

    // Get all applications from the Application table
    @SuppressLint("Range")
    fun getAllApplications(): List<Application> {
        val applications = ArrayList<Application>()

        val db = this.readableDatabase

        val cursor = db.query(
            Application.TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            null
        )

        if (cursor.moveToFirst()) {
            do {
                val appName = cursor.getString(cursor.getColumnIndex(Application.COLUMN_APP_NAME))
                val packageName = cursor.getString(cursor.getColumnIndex(Application.COLUMN_PACKAGE_NAME))
                val appIcon = cursor.getBlob(cursor.getColumnIndex(Application.COLUMN_APP_ICON))

                val application = Application(appName, packageName, appIcon)
                applications.add(application)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return applications
    }

    // Update an existing application in the Application table
    fun updateApplication(application: Application): Int {
        val db = this.writableDatabase

        val values = ContentValues()
        values.put(Application.COLUMN_APP_NAME, application.appName)
        values.put(Application.COLUMN_APP_ICON, application.appIcon)

        val selection = "${Application.COLUMN_PACKAGE_NAME} = ?"
        val selectionArgs = arrayOf(application.packageName)

        val count = db.update(
            Application.TABLE_NAME,
            values,
            selection,
            selectionArgs
        )

        db.close()
        return count
    }

    // Delete an application from the Application table
    fun deleteApplication(packageName: String): Int {
        val db = this.writableDatabase

        val selection = "${Application.COLUMN_PACKAGE_NAME} = ?"
        val selectionArgs = arrayOf(packageName)

        val count = db.delete(
            Application.TABLE_NAME,
            selection,
            selectionArgs
        )

        db.close()
        return count
    }



}
