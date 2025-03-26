package org.epstudios.morbidmeter

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import org.epstudios.morbidmeter.timescale.TimeScaleKind

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
class MorbidMeterWidgetProvider : AppWidgetProvider() {

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
        cancelUpdates(context)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        Log.d(LOG_TAG, "onReceive called with action: ${intent.action}")
        if (intent.action == AppWidgetManager.ACTION_APPWIDGET_UPDATE
            || intent.action == UPDATE_ACTION
        ) {
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
        val views = RemoteViews(context.packageName, R.layout.widget)
        // TODO: Fix this code
        // Skull button needs to be configured each time?  Maybe just with
        // initial configuration.
        MorbidMeterClock.resetConfiguration(context, appWidgetId)
        MmConfigure.configureSkullButton(context, appWidgetId, views)
        updateWidget(context, views)
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    private fun scheduleNextUpdate(context: Context) {
        Log.d(LOG_TAG, "scheduleNextUpdate called")
        if (alarm == null) {
            alarm = MmAlarm.create(
                context,
                Intent(context, MorbidMeterWidgetProvider::class.java).apply {
                    action = UPDATE_ACTION
                },
                alarmType
            )
        }
        alarm?.setAlarm(MorbidMeterClock.getFrequency(context))
    }

    private fun cancelUpdates(context: Context) {
        Log.d(LOG_TAG, "cancelUpdates called")
        if (alarm == null) {
            alarm = MmAlarm.create(
                context,
                Intent(context, MorbidMeterWidgetProvider::class.java).apply {
                    action = UPDATE_ACTION
                },
                alarmType
            )
        }
        alarm?.cancelAlarm()
    }

    fun updateWidget(context: Context, views: RemoteViews) {
//        val currentTime = MorbidMeterClock.getFormattedTime(context)
//        if (currentTime != null) {
        val timeScale = MorbidMeterClock.getTimeScale()
        Log.d(LOG_TAG, "timeScale = $timeScale")
        if (timeScale == null) {
            return
        }
        // TODO: null check time functions
        if (timeScale.kind == TimeScaleKind.REAL_TIME) {
            Log.d(LOG_TAG, "setting real time")
            views.setCharSequence(
                R.id.realTime,
                "setFormat12Hour", timeScale.getTimeFormat(context));
            views.setCharSequence(
                R.id.realTime,
                "setFormat24Hour", timeScale.getTimeFormat(context));
            views.setViewVisibility(R.id.time, View.GONE)
            views.setViewVisibility(R.id.realTime, View.VISIBLE)
        } else if (timeScale.kind == TimeScaleKind.PERCENT) {
            val percentage: Double = MorbidMeterClock.rawPercentAlive()
            Log.d(LOG_TAG, "Percentage = $percentage")
            views.setTextViewText(R.id.time, timeScale.getPercentTime(context, percentage,
                MorbidMeterClock.getTimeScaleDirection()))
            views.setViewVisibility(R.id.time, View.VISIBLE)
            views.setViewVisibility(R.id.realTime, View.GONE)
        } else if (timeScale.kind == TimeScaleKind.DURATION) {
            views.setTextViewText(R.id.time, timeScale.getTimeDuration(context,
                MorbidMeterClock.getMsecAlive(),
                MorbidMeterClock.getMsecTotal(),
                MorbidMeterClock.getTimeScaleDirection()))
            views.setViewVisibility(R.id.time, View.VISIBLE)
            views.setViewVisibility(R.id.realTime, View.GONE)
        }


//            if (TimeScaleType.isRealTime(MorbidMeterClock.getTimeScaleNameId())) {
//                Log.d(LOG_TAG, "setting real time")
//                views.setCharSequence(
//                    R.id.realTime,
//                    "setFormat12Hour", "EEEE, MMMM d yyyy\nhh:mm:ss a z")
//                // Probably will override users clock preference
//                // so set 12 hour and 24 hour clock to same format.
//                views.setCharSequence(
//                    R.id.realTime,
//                    "setFormat24Hour", "EEEE, MMMM d yyyy\nhh:mm:ss a z")
//                views.setViewVisibility(R.id.time, View.GONE)
//                views.setViewVisibility(R.id.realTime, View.VISIBLE)
//            }
        else if (timeScale.kind == TimeScaleKind.NONE) {
            views.setViewVisibility(R.id.time, View.GONE)
            views.setViewVisibility(R.id.realTime, View.GONE)
        }
//            } else {
//                views.setViewVisibility(R.id.time, View.VISIBLE)
//                views.setTextViewText(R.id.time, currentTime)
//            }
//        }
        views.setProgressBar(
            R.id.progressBar, 100,
            MorbidMeterClock.percentAlive(), false
        )
        val label = MorbidMeterClock.getLabel(context)
        views.setTextViewText(R.id.text, label)
        Log.d(LOG_TAG, "Label updated.")
    }
}
