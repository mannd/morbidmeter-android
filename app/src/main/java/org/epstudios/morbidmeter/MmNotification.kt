package org.epstudios.morbidmeter

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

/**
Copyright (C) 2025 EP Studios, Inc.
www.epstudiossoftware.com

Created by mannd on 4/12/25.

This file is part of morbidmeter-android.

morbidmeter-android is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

morbidmeter-android is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with morbidmeter-android.  If not, see <http://www.gnu.org/licenses/>.
 */

class MmNotification(private val context: Context) {

    companion object {
        private const val CHANNEL_ID = "MorbidMeterMilestoneChannel"
        private const val NOTIFICATION_ID = 1000
        private const val LOG_TAG = "MmNotification"
        private const val PREFS_NAME = "org.epstudios.morbidmeter.MmNotifications"
        private const val KEY_LAST_MILESTONE = "last_milestone"
        private const val MAX_MILESTONE = 100
    }

    private val notificationManager: NotificationManagerCompat
        = NotificationManagerCompat.from(context)
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>


    init {
        createNotificationChannel()
    }

    fun registerForPermission(activity: AppCompatActivity) {
        Log.d(LOG_TAG, "registerForPermission")
        requestPermissionLauncher = activity.registerForActivityResult(
            ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
            if (isGranted) {
                // permission granted
                Log.d(LOG_TAG, "Permission granted")
            } else {
                // not granted
                Log.d(LOG_TAG, "Permission not granted")
            }
        }
    }

    private fun createNotificationChannel() {
        Log.d(LOG_TAG, "createNotificationChannel")
        // NB: Earlier versions of Android don't support notification channels.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {  // O = Oreo, Android 8
            val name = context.getString(R.string.channel_name)
            val descriptionText = context.getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = context.getSystemService(
                Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    internal fun sendNotification(userName: String, percentAlive: Double, widgetId: Int) {
        if (notificationManager.areNotificationsEnabled()) {
            Log.d(LOG_TAG, "Sending notification")
            if (notificationManager.areNotificationsEnabled()) {
                Log.d(LOG_TAG, "Notifications enabled")
                val milestone = getMileStone(percentAlive)
                val lastMilestone = getLastMileStone(widgetId)
                if (compareMilestones(milestone, lastMilestone)) {
                    Log.d(LOG_TAG, "New Milestone")
                    saveLastMilestone(milestone, widgetId)
                    val milestoneNotificationText = getMilestoneNotificationText(percentAlive)
                    if (milestoneNotificationText != null) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            if (ActivityCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.POST_NOTIFICATIONS
                                ) != PackageManager.PERMISSION_GRANTED
                            ) {
                                // Permission not granted.  Show permission request.
                                requestPermissionLauncher.launch(
                                    Manifest.permission.POST_NOTIFICATIONS)
                                return
                            }
                        }
                        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                            .setSmallIcon(R.drawable.ic_stat_notification)
                            .setAutoCancel(true)
                            .setTicker("MorbidMeter Milestone")
                            .setWhen(System.currentTimeMillis())
                            .setContentTitle(context.getString(R.string.milestone_notification_title, userName))
                            .setContentText(milestoneNotificationText)
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        Log.d(LOG_TAG, "Notifying")
                        notificationManager.notify(NOTIFICATION_ID, builder.build())
                    }
                }
            }
        }
    }

    private fun getMilestoneNotificationText(percentAlive: Double): String? {
        val milestone = getMileStone(percentAlive)
        if (milestone >= MAX_MILESTONE) {
            return context.getString(R.string.finish_notification_text)
        } else {
            return context.getString(R.string.milestone_notification_text, milestone)
        }
        // NOTE that there is no notification for preBirth.
        // It would be complicated to implement given how we
        // avoid repeating notifications by assessing increments
        // in percentAlive.
    }

    private fun getMileStone(percentAlive: Double): Int {
        return (percentAlive * 100.0).toInt()
    }

    private fun getLastMileStone(widgetId: Int): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME + widgetId, Context.MODE_PRIVATE)
        return prefs.getInt(KEY_LAST_MILESTONE, 0)
    }

    private fun saveLastMilestone(milestone: Int, widgetId: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME + widgetId, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putInt(KEY_LAST_MILESTONE, milestone)
        editor.apply()
    }

    private fun compareMilestones(newMilestone: Int, lastMilestone: Int): Boolean {
        return newMilestone % 10 > lastMilestone % 10

    }

    internal fun clearLastMilestone(widgetId: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME + widgetId, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.remove(KEY_LAST_MILESTONE)
        editor.apply()
    }
}
