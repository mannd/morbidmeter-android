package org.epstudios.morbidmeter

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
class MmExactAlarm(context: Context, intent: Intent, frequency: Long) : MmAlarm {
    private val alarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager

    override fun setAlarm(
        context: Context,
        intent: Intent,
        frequency: Long
    ) {
        TODO("Not yet implemented")
    }

    override fun cancelAlarm(context: Context) {
        TODO("Not yet implemented")
    }
}
