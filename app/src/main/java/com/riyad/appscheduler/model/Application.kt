package com.riyad.appscheduler.model

data class Application(val appName: String, val packageName: String, val appIcon: ByteArray) {
    companion object {
        const val TABLE_NAME = "Application"
        const val COLUMN_APP_NAME = "app_name"
        const val COLUMN_PACKAGE_NAME = "package_name"
        const val COLUMN_APP_ICON = "app_icon"
    }
}