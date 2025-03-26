package org.epstudios.morbidmeter.timescale

import android.content.Context
import org.epstudios.morbidmeter.R
import org.epstudios.morbidmeter.timescale.TimeScaleDirection
import org.epstudios.morbidmeter.timescale.TimeScaleKind
import org.epstudios.morbidmeter.timescale.TimeScaleType
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

class PercentTimeScale : TimeScale {
    override val type: TimeScaleType = TimeScaleType.PERCENT
    override val nameId: Int = R.string.ts_percent
    override val kind: TimeScaleKind = TimeScaleKind.PERCENT

    val formatString = "#.000000"
    val formatter = DecimalFormat(formatString)

    override fun getPercentTime(context: Context, percent: Double, direction: TimeScaleDirection): String {
        return if (direction == TimeScaleDirection.FORWARD) {
            formatter.format(getProportionalTime(percent, direction)) + "%"
        } else {
            formatter.format(getProportionalTime(percent, direction)) + "% left"
        }
    }

    private fun getProportionalTime(
        percent: Double,
        direction: TimeScaleDirection = TimeScaleDirection.FORWARD): Double {
        if (direction == TimeScaleDirection.FORWARD) {
            return percent * 100.0
        } else {
            return 100.0 - (percent * 100.0)
        }
    }
}
