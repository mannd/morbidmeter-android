package org.epstudios.morbidmeter.timescale

import android.content.Context
import org.epstudios.morbidmeter.MorbidMeterClock

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
 * TimeScale that represents a duration.
 */
abstract class DurationTimeScale : TimeScale {

    abstract fun getTimeDuration(
        context: Context,
        msecAlive: Long,
        msecTotal: Long,
        direction: TimeScaleDirection
    ): String

    fun getDuration(msecAlive: Long, msecTotal: Long, direction: TimeScaleDirection): Long {
        return if (direction == TimeScaleDirection.FORWARD) {
            msecAlive
        } else {
            msecTotal - msecAlive
        }
    }

    fun numDays(msec: Long): Double {
        return (msec / 1000.0 / 60 / 60 / 24)
    }

    fun numYears(msecs: Long): Double {
        return numDays(msecs) / 365.25
    }

}
