package org.epstudios.morbidmeter.timescale

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import org.epstudios.morbidmeter.R
import android.Manifest

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
        private const val CHANNEL_ID = "MorbidMeterChannel"
        private const val NOTIFICATION_ID = 1000
    }

    private val notificationManager: NotificationManagerCompat
    = NotificationManagerCompat.from(context)

    init {
        createNotificationChannel()
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

    fun sendNotification(userName: String, percentAlive: Double) {
        if (notificationManager.areNotificationsEnabled()) {
           val milestoneNotificationText = getMilestoneNotificationText(percentAlive)
            if (milestoneNotificationText != null) {
                val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_stat_notification)
                    .setAutoCancel(true)
                    .setTicker("MorbidMeter Milestone")
                    .setWhen(System.currentTimeMillis())
                    .setContentTitle(context.getString(R.string.milestone_notification_title, userName))
                    .setContentText(milestoneNotificationText)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: call ActivityCompat.requestPermissions() to request the permission
                    return
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
