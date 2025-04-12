package org.epstudios.morbidmeter

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import org.epstudios.morbidmeter.MorbidMeterClock.showNotification
import org.epstudios.morbidmeter.timescale.CalendarTimeScale
import org.epstudios.morbidmeter.timescale.DurationTimeScale
import org.epstudios.morbidmeter.timescale.NoTimeScale
import org.epstudios.morbidmeter.timescale.PercentTimeScale
import org.epstudios.morbidmeter.timescale.RealTimeScale
import org.epstudios.morbidmeter.timescale.TimeScale
import org.epstudios.morbidmeter.timescale.TimeScaleDirection

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

/**
 * The widget provider for the MorbidMeter widgets.
 */
class MorbidMeterWidgetProvider : AppWidgetProvider() {

    companion object {
        private const val UPDATE_ACTION =
            "com.epstudiossoftware.morbidmeter.UPDATE_WIDGET"
        private const val LOG_TAG = "MorbidMeterWidgetProvider"
    }

    var frequencyId: Int = 0
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
    }

    // Called when last widget is disabled.
    override fun onDisabled(context: Context) {
        Log.d(LOG_TAG, "onDisabled called")
        cancelUpdates(context)
        MmConfigure.deletePrefs(context)
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        Log.d(LOG_TAG, "onDeleted called")
        cancelUpdates(context)
        MmConfigure.deletePrefs(context)
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
            scheduleNextUpdate(context, frequencyId)
        }
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        Log.d(LOG_TAG, "updateAppWidget called with appWidgetId: $appWidgetId")
        val views = RemoteViews(context.packageName, R.layout.widget)
        val configuration = MmConfigure.loadPrefs(context, appWidgetId)
        // TODO: Need to update button with every update?
        MmConfigure.configureSkullButton(context, appWidgetId, views)
        updateWidget(context, views, configuration)
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    private fun updateWidget(context: Context, views: RemoteViews, configuration: MmConfiguration) {
        val timeScale = TimeScale.getTimeScale(configuration.timeScaleNameId)
        frequencyId = configuration.updateFrequencyId
        if (configuration.useExactTime) {
            alarmType = MmAlarmType.EXACT
        } else {
            alarmType = MmAlarmType.INEXACT
        }
        Log.d(LOG_TAG, "timeScale = $timeScale")
        if (timeScale == null) return
        val percentAlive = configuration.user.percentAlive()
        if (timeScale is RealTimeScale) {
            views.setCharSequence(
                R.id.realTime,
                "setFormat12Hour",
                timeScale.getTimeFormat(context)
            );
            views.setCharSequence(
                R.id.realTime,
                "setFormat24Hour", timeScale.getTimeFormat(context)
            );
            views.setViewVisibility(R.id.time, View.GONE)
            views.setViewVisibility(R.id.realTime, View.VISIBLE)
        } else if (timeScale is PercentTimeScale) {
            views.setTextViewText(
                R.id.time,
                timeScale.getPercentTime(
                    context,
                    percentAlive,
                    getTimeScaleDirection(configuration)
                )
            )
            views.setViewVisibility(R.id.time, View.VISIBLE)
            views.setViewVisibility(R.id.realTime, View.GONE)
        } else if (timeScale is DurationTimeScale) {
            views.setTextViewText(
                R.id.time, timeScale.getTimeDuration(
                    context,
                    configuration.user.msecAlive(),
                    configuration.user.lifeDurationMsec(),
                    getTimeScaleDirection(configuration)
                )
            )
            views.setViewVisibility(R.id.time, View.VISIBLE)
            views.setViewVisibility(R.id.realTime, View.GONE)
        } else if (timeScale is CalendarTimeScale) {
            val proportionalTime = timeScale.getProportionalTime(
                percentAlive,
                getTimeScaleDirection(configuration)
            )
            views.setTextViewText(
                R.id.time,
                proportionalTime
            )
            views.setViewVisibility(R.id.time, View.VISIBLE)
            views.setViewVisibility(R.id.realTime, View.GONE)
        } else if (timeScale is NoTimeScale) {
            views.setViewVisibility(R.id.time, View.GONE)
            views.setViewVisibility(R.id.realTime, View.GONE)
        }
        views.setProgressBar(
            R.id.progressBar, 100,
            (configuration.user.percentAlive() * 100).toInt(),
            false
        )
        val label = getLabel(context, configuration, timeScale)
        views.setTextViewText(R.id.text, label)
        Log.d(LOG_TAG, "Label updated.")
    }

    private fun getLabel(
        context: Context,
        configuration: MmConfiguration,
        timeScale: TimeScale
    ): String {
        var timeScaleName: String? = context.getString(R.string.timescale_prefix)
        if (configuration.reverseTime) timeScaleName +=
            context.getString(R.string.reverse_timescale_prefix)
        timeScaleName += timeScale.getName(context)
        //timeScaleName += context.getString(configuration.timeScaleNameId)
        val userName = (if (configuration.doNotModifyName)
            configuration.user.getName()
        else
            (configuration.user.getApostrophedName() + " " +
                    context.getString(R.string.app_name)))
        return userName + "\n" + timeScaleName
    }

    private fun scheduleNextUpdate(context: Context, frequencyId: Int) {
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
        alarm?.setAlarm(Frequency.getFrequency(frequencyId))
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

    // TODO: move this to MmConfiguration?
    private fun getTimeScaleDirection(configuration: MmConfiguration): TimeScaleDirection {
        return if (configuration.reverseTime)
            TimeScaleDirection.REVERSE
        else
            TimeScaleDirection.FORWARD
    }
}
