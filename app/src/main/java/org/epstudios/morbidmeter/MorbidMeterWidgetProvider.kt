package org.epstudios.morbidmeter

import android.app.AlarmManager
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.util.Log
import android.widget.RemoteViews
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
            "com.epstudiossoftware.morbidmeterwidgetprovider.UPDATE_WIDGET"
        private const val LOG_TAG = "MorbidMeterWidgetProvider"
    }

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
        scheduleNextUpdate(context)
    }

    override fun onDisabled(context: Context) {
        Log.d(LOG_TAG, "onDisabled called")
        cancelUpdates(context)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        Log.d(LOG_TAG, "onReceive called with action: ${intent.action}")
        if (intent.action == UPDATE_ACTION) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val componentName = ComponentName(context, MorbidMeterWidgetProvider::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
            onUpdate(context, appWidgetManager, appWidgetIds)
            scheduleNextUpdate(context)
        }
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        Log.d(LOG_TAG, "updateAppWidget called with appWidgetId: $appWidgetId")
        val views = RemoteViews(context.packageName, R.layout.main)
        // TODO: Fix this code
        val currentTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        views.setTextViewText(R.id.time, currentTime)
        appWidgetManager.updateAppWidget(appWidgetId, views)
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
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime() + 60000, pendingIntent
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
}
