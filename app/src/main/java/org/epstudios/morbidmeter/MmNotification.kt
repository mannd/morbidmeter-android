package org.epstudios.morbidmeter

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
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

    // NOTE: probably need to set custom sounds in the channel creation fun.
    // See https://help.batch.com/en/articles/5671551-how-can-i-use-a-custom-notification-sound-on-android
    internal fun sendNotification(userName: String, percentAlive: Double, configuration: MmConfiguration) {
        if (notificationManager.areNotificationsEnabled()) {
            Log.d(LOG_TAG, "Sending notification")
            Log.d(LOG_TAG, "Sound: $configuration.notificationSound")
           val milestoneNotificationText = getMilestoneNotificationText(percentAlive)
            // FIXME: Need this here?
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
                if (configuration.notificationSound == R.id.default_sound) {
                    Log.d(LOG_TAG, "Using default sound")
                    builder.setSound(
                        RingtoneManager.getDefaultUri(
                            RingtoneManager.TYPE_NOTIFICATION
                        )
                    )
                } else if (configuration.notificationSound == R.id.mm_sound) {
                    Log.d(LOG_TAG, "Using MM sound")
                    builder.setSound(Uri
                        .parse("android.resource://org.epstudios.morbidmeter/raw/bellsnotification"));
                } else { // no sound
                    Log.d(LOG_TAG, "No sound")
                    builder.setSound(null)
                }
                notificationManager.notify(NOTIFICATION_ID, builder.build())
            }
        }
    }

    private fun getMilestoneNotificationText(percentAlive: Double): String? {
        val milestone = (percentAlive * 10.0).toInt()
        if (milestone % 10 == 0) {
            return context.getString(R.string.milestone_notification_text, milestone)
        } else {
            return null
        }
    }
}
