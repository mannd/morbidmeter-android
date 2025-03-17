package org.epstudios.morbidmeter

import android.app.AlarmManager
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.RemoteViews

/**
Copyright (C) 2025 EP Studios, Inc.
www.epstudiossoftware.com

Created by mannd on 3/12/25.

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
class MorbidMeterWidgetProvider: AppWidgetProvider() {

    companion object {
        private const val UPDATE_ACTION =
            "com.epstudiossoftware.morbidmeter.UPDATE_WIDGET"
        private const val LOG_TAG = "MorbidMeterWidgetProvider"
    }

    var alarm: MmAlarm? = null
    var alarmType: MmAlarmType = MmAlarmType.INEXACT

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        Log.d(LOG_TAG, "onUpdate called")
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        Log.d(LOG_TAG, "onEnabled called")
        // TODO: too early to get a frequency here? so not needed?
//        scheduleNextUpdate(context)
    }

    override fun onDisabled(context: Context) {
        Log.d(LOG_TAG, "onDisabled called")
        cancelUpdates2(context)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        Log.d(LOG_TAG, "onReceive called with action: ${intent.action}")
        if (intent.action == AppWidgetManager.ACTION_APPWIDGET_UPDATE
            || intent.action == UPDATE_ACTION) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val componentName = ComponentName(context, MorbidMeterWidgetProvider::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
            onUpdate(context, appWidgetManager, appWidgetIds)
            scheduleNextUpdate2(context)
        }
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        Log.d(LOG_TAG, "updateAppWidget called with appWidgetId: $appWidgetId")
        val views = RemoteViews(context.packageName, R.layout.widget)
        // TODO: Fix this code
        // Skull button needs to be configured each time?  Maybe just with
        // initial configuration.
        MorbidMeterClock.resetConfiguration(context, appWidgetId)
        val frequency = MorbidMeterClock.getFrequency(context)
        Log.d(LOG_TAG, "Frequency = $frequency")
        MmConfigure.configureSkullButton(context, appWidgetId, views)
        updateWidget(context, views)
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    private fun scheduleNextUpdate2(context: Context) {
        Log.d(LOG_TAG, "scheduleNextUpdate2 called")
        if (alarm == null) {
            alarm = MmAlarm.create(context, Intent(context, MorbidMeterWidgetProvider::class.java).apply {
                action = UPDATE_ACTION
            }, alarmType)
        }
        alarm?.setAlarm(MorbidMeterClock.getFrequency(context))
    }

    private fun cancelUpdates2(context: Context) {
        Log.d(LOG_TAG, "cancelUpdates2 called")
        if (alarm == null) {
            alarm = MmAlarm.create(context, Intent(context, MorbidMeterWidgetProvider::class.java).apply {
                action = UPDATE_ACTION
            }, alarmType)
        }
        alarm?.cancelAlarm()
    }

    private fun scheduleNextUpdate(context: Context) {
        Log.d(LOG_TAG, "scheduleNextUpdate called")
        val alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
        val intent = Intent(context, MorbidMeterWidgetProvider::class.java).apply {
            action = UPDATE_ACTION
        }
        val pendingIntent = android.app.PendingIntent.getBroadcast(
            context, 0,
            intent,
            android.app.PendingIntent.FLAG_IMMUTABLE or android.app.PendingIntent.FLAG_UPDATE_CURRENT
        )
        val frequency = MorbidMeterClock.getFrequency(context)
        Log.d(LOG_TAG, "Frequency = $frequency")
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime() + frequency, pendingIntent
        )
    }

    private fun cancelUpdates(context: Context) {
        Log.d(LOG_TAG, "cancelUpdates called")
        val alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
        val intent = Intent(context, MorbidMeterWidgetProvider::class.java).apply {
            action = UPDATE_ACTION
        }
        val pendingIntent = android.app.PendingIntent.getBroadcast(
            context, 0,
            intent,
            android.app.PendingIntent.FLAG_IMMUTABLE or android.app.PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager.cancel(pendingIntent)
    }

    // TODO: use a setting to determine whether to show the time or the RealTime clock.
    fun updateWidget(context: Context, views: RemoteViews) {
        val currentTime = MorbidMeterClock.getFormattedTime(context)
        if (currentTime != null) {
            Log.d(LOG_TAG, "Current time = $currentTime")
            if (currentTime == "0") {
                views.setViewVisibility(R.id.time, View.GONE)
            } else {
                views.setViewVisibility(R.id.time, View.VISIBLE)
                views.setTextViewText(R.id.time, currentTime)
            }
        }
        views.setProgressBar(
            R.id.progressBar, 100,
            MorbidMeterClock.percentAlive(), false
        )
        val label = MorbidMeterClock.getLabel()
        views.setTextViewText(R.id.text, label)
        Log.d(LOG_TAG, "Label updated.")
    }
}
