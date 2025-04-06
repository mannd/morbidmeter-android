package org.epstudios.morbidmeter.timescale

import android.content.Context
import org.epstudios.morbidmeter.R
import java.text.DecimalFormat

/**
Copyright (C) 2025 EP Studios, Inc.
www.epstudiossoftware.com

Created by mannd on 4/5/25.

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

class DaysHoursMinsTimeScale : DurationTimeScale() {
    override val type: TimeScaleType = TimeScaleType.DAYS_HOURS_MINUTES
    override val nameId: Int = R.string.ts_d_h_m

    private val formatString = "#,###"
    private val formatter = DecimalFormat(formatString)
    private val resultMap = mapOf(TimeScaleDirection.FORWARD to R.string.d_h_m_result,
        TimeScaleDirection.REVERSE to R.string.reverse_d_h_m_result)

    override fun getTimeDuration(
        context: Context,
        msecAlive: Long,
        msecTotal: Long,
        direction: TimeScaleDirection
    ): String {
        val duration = getDuration(msecAlive, msecTotal, direction)
        val resultId = resultMap[direction]
        return getTimeDuration(context, formatter, duration, resultId)
    }

    fun getTimeDuration(
        context: Context,
        formatter: DecimalFormat,
        duration: Long,
        resultId: Int?
    ): String {
        if (resultId == null) return context.getString(R.string.error_message)
        val secs = duration / 1000.0
        var mins = secs / 60.0
        var hours = mins / 60.0
        val days = hours / 24.0
        mins = mins % 60
        hours = hours % 24
        val minsFormat  = formatter.format(mins)
        val hoursFormat = formatter.format(hours)
        val daysFormat = formatter.format(days)
        return String.format(context.getString(resultId), daysFormat,
            hoursFormat, minsFormat)
    }
}
