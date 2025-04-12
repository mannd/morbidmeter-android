package org.epstudios.morbidmeter.timescale

import android.content.Context
import org.epstudios.morbidmeter.timescale.TimeScaleType.Companion.fromStringId

/**
Copyright (C) 2025 EP Studios, Inc.
www.epstudiossoftware.com

Created by mannd on 3/22/25.

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
 * Interface for a time scale.
 */
interface TimeScale {

    val type: TimeScaleType // Type of time scale
    val nameId: Int // Resource ID for the name of the time scale

    /**
     * Get the name of the time scale.
     * @param context Context to use for getting the string.
     * @return The name of the time scale.
     */
    fun getName(context: Context): String {
        return context.getString(nameId)
    }

    companion object {
        fun getTimeScale(timeScaleNameId: Int): TimeScale? {
            val type = fromStringId(timeScaleNameId)
            if (type == null) {
                return null
            }
            when (type) {
                // Real time scales
                TimeScaleType.LONG_TIME -> return LongTimeScale()
                TimeScaleType.SHORT_TIME -> return ShortTimeScale()
                TimeScaleType.LONG_MILITARY_TIME -> return LongMilitaryTimeScale()
                TimeScaleType.SHORT_MILITARY_TIME -> return ShortMilitaryTimeScale()
                // Percent time scales
                TimeScaleType.PERCENT -> return PercentTimeScale()
                TimeScaleType.UNIVERSE -> return UniverseTimeScale()
                TimeScaleType.X_UNIVERSE_2 -> return XUniverse2TimeScale()
                // Duration time scales
                TimeScaleType.SECONDS -> return SecondsTimeScale()
                TimeScaleType.MINUTES -> return MinutesTimeScale()
                TimeScaleType.HOURS -> return HoursTimeScale()
                TimeScaleType.WEEKS -> return WeeksTimeScale()
                TimeScaleType.MONTHS -> return MonthsTimeScale()
                TimeScaleType.DAYS -> return DaysTimeScale()
                TimeScaleType.YEARS -> return YearsTimeScale()
                TimeScaleType.DAYS_HOURS_MINUTES -> return DaysHoursMinsTimeScale()
                // Calendar time scales
                TimeScaleType.ONE_HOUR -> return OneHourTimeScale()
                TimeScaleType.ONE_DAY -> return OneDayTimeScale()
                TimeScaleType.ONE_WEEK -> return OneWeekTimeScale()
                TimeScaleType.ONE_MONTH -> return OneMonthTimeScale()
                TimeScaleType.ONE_YEAR -> return OneYearTimeScale()
                TimeScaleType.X_UNIVERSE -> return XUniverseTimeScale()
                // Other time scales
                TimeScaleType.NONE -> return NoTimeScale()
            }
        }
    }
}
