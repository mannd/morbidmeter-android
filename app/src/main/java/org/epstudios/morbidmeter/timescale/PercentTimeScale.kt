package org.epstudios.morbidmeter.timescale

import android.content.Context
import org.epstudios.morbidmeter.R
import java.text.DecimalFormat

/**
Copyright (C) 2025 EP Studios, Inc.
www.epstudiossoftware.com

Created by mannd on 3/23/25.

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
 * TimeScale that represents a percent value.
 */
open class PercentTimeScale : TimeScale {
    override val type: TimeScaleType = TimeScaleType.PERCENT
    override val nameId: Int = R.string.ts_percent

    open val formatString = "#.00"
    open val maxValue = 100L

    open fun getPercentTime(
        context: Context,
        percent: Double,
        direction: TimeScaleDirection
    ): String {
        val formatter = DecimalFormat(formatString)
        val percentString = formatter.format(getPercentOfMaxInterval(
            percent,
            direction,
            maxValue))
        return if (direction == TimeScaleDirection.FORWARD) {
            context.getString(R.string.percent_result, percentString)
        } else {
            context.getString(R.string.reverse_percent_result, percentString)
        }
    }

    /**
     * Gets the percent of the max interval.
     *
     * Coerces value between 0.0 and maxInterval.
     */
    internal fun getPercentOfMaxInterval(
        percent: Double,
        direction: TimeScaleDirection,
        maxValue: Long): Double {
        return if (direction == TimeScaleDirection.FORWARD) {
            (percent * maxValue).coerceIn(0.0, maxValue.toDouble())
        } else {
            (maxValue - (percent * maxValue)).coerceIn(0.0, maxValue.toDouble())
        }
    }
}
