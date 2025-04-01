package org.epstudios.morbidmeter.timescale

import android.content.Context
import java.util.Calendar
import java.util.GregorianCalendar
import org.epstudios.morbidmeter.R

/**
Copyright (C) 2025 EP Studios, Inc.
www.epstudiossoftware.com

Created by mannd on 3/30/25.

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

class OneDayTimeScale : CalendarTimeScale() {
    override val type: TimeScaleType = TimeScaleType.ONE_DAY
    override val nameId: Int = R.string.ts_day
    val format: String? = "h:mm:ss a"

    private val minTime: Calendar =
        GregorianCalendar(2000, Calendar.JANUARY, 1, 0, 0, 0)
    private val maxTime: Calendar =
        GregorianCalendar(2000, Calendar.JANUARY, 2, 0, 0, 0)

    override fun getTimeFormat(context: Context): String? {
        return context.getString(R.string.one_day_format)
    }

    override fun getProportionalTime(percent: Double, direction: TimeScaleDirection): Double {
        return if (direction == TimeScaleDirection.FORWARD) {
            minTime.getTimeInMillis() + (percent * duration())
        } else {
            maxTime.getTimeInMillis() - (percent * duration())
        }
    }

    private fun duration(): Long {
        return maxTime.getTimeInMillis() - minTime.getTimeInMillis()
    }



}
