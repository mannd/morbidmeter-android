package org.epstudios.morbidmeter

import android.app.PendingIntent
import android.content.Context
import android.content.Intent

/**
Copyright (C) 2025 EP Studios, Inc.
www.epstudiossoftware.com

Created by mannd on 3/15/25.

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
abstract class MmAlarm(context: Context, intent: Intent) {
    protected val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
    protected val pendingIntent: PendingIntent = android.app.PendingIntent.getBroadcast(
        context,
        0,
        intent,
        android.app.PendingIntent.FLAG_IMMUTABLE or android.app.PendingIntent.FLAG_UPDATE_CURRENT )

    abstract fun setAlarm(frequency: Int)

    fun cancelAlarm() {
        alarmManager.cancel(pendingIntent)
    }
}
