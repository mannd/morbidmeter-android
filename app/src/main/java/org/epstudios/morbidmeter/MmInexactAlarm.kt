package org.epstudios.morbidmeter

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.util.Log

/**
Copyright (C) 2025 EP Studios, Inc.
www.epstudiossoftware.com

Created by mannd on 3/16/25.

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

class MmInexactAlarm(context: Context, intent: Intent) : MmAlarm(context, intent) {
    companion object {
        const val LOG_TAG = "MmInexactAlarm"
    }
    override fun setAlarm(frequency: Int) {
        Log.d(LOG_TAG, "setInexactAlarm")
        alarmManager.setAndAllowWhileIdle(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime() + frequency, pendingIntent
        )
    }
}
