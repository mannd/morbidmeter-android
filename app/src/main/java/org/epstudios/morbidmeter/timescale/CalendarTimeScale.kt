package org.epstudios.morbidmeter.timescale

import java.text.SimpleDateFormat
import java.util.Calendar

/**
Copyright (C) 2025 EP Studios, Inc.
www.epstudiossoftware.com

Created by mannd on 3/31/25.

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
 * TimeScale that maps a duration on a calendar time period.
 */
abstract class CalendarTimeScale : TimeScale {
    abstract val minTime: Calendar
    abstract val maxTime: Calendar
    abstract val formatString: String

    fun getProportionalTime(percent: Double, direction: TimeScaleDirection): String {
        val formatter = SimpleDateFormat(formatString)
        val duration = timeScaleDuration(maxTime, minTime) * percent
        val time = if (direction == TimeScaleDirection.FORWARD) {
            minTime.getTimeInMillis() + duration
        } else {
            maxTime.getTimeInMillis() - duration
        }
        return formatter.format(time)
    }

    private fun timeScaleDuration(maxTime: Calendar, minTime: Calendar): Long {
        return maxTime.getTimeInMillis() - minTime.getTimeInMillis()
    }
}
