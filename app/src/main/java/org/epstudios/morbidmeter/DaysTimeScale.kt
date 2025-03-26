package org.epstudios.morbidmeter

import android.content.Context
import java.text.DecimalFormat

/**
Copyright (C) 2025 EP Studios, Inc.
www.epstudiossoftware.com

Created by mannd on 3/25/25.

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

class DaysTimeScale : TimeScale {
    override val type: TimeScaleType = TimeScaleType.DAYS
    override val nameId: Int = R.string.ts_days
    override val kind: TimeScaleKind = TimeScaleKind.DURATION

    val formatString = "#,###.0000"
    val formatter = DecimalFormat(formatString)

    override fun getTimeDuration(
        context: Context,
        msecAlive: Long,
        msecTotal: Long,
        direction: TimeScaleDirection ): String? {
        if (direction == TimeScaleDirection.FORWARD) {
            return formatter.format(numDays(msecAlive)) + " days alive"
        } else {
            return formatter.format(numDays(msecTotal - msecAlive)) + " days remaining"
        }
    }

    private fun numDays(msec: Long): Double {
        return (msec / 1000.0 / 60 / 60 / 24)
    }
}
