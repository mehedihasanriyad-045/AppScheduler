package com.riyad.appscheduler.model

data class Schedule(val packageName: String, var time: Long, var timeStr: String, val notificationId: Int, val isTapped: Int) {
    companion object {
        const val TABLE_NAME = "Schedule"
        const val COLUMN_PACKAGE_NAME = "package_name"
        const val COLUMN_TIME = "time"
        const val COLUMN_TIME_STR = "time_str"
        const val COLUMN_NOTIFICATION_ID = "notification_id"
        const val COLUMN_IS_TAPPED = "is_tapped"


    }
}


