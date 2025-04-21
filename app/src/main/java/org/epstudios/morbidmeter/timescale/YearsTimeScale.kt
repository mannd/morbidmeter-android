package org.epstudios.morbidmeter.timescale

import android.content.Context
import org.epstudios.morbidmeter.R
import java.text.DecimalFormat

/**
Copyright (C) 2025 EP Studios, Inc.
www.epstudiossoftware.com

Created by mannd on 4/3/25.

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

class YearsTimeScale : DurationTimeScale() {
    override val type: TimeScaleType = TimeScaleType.YEARS
    override val nameId: Int = R.string.ts_years

    private val formatString = "#.000000"
    private val formatter = DecimalFormat(formatString)
    private val resultMap = mapOf(TimeScaleDirection.FORWARD to R.string.years_alive_result,
        TimeScaleDirection.REVERSE to R.string.reverse_years_alive_result)

    override fun getTimeDuration(
        context: Context,
        msecAlive: Long,
        msecTotal: Long,
        direction: TimeScaleDirection
    ): String {
        val duration = getDuration(msecAlive, msecTotal, direction)
        val years = numYears(duration)
        val resultId = resultMap[direction]
        return getTimeDuration(context, formatter, years, resultId)
    }
}
