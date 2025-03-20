package org.epstudios.morbidmeter

import android.content.res.Resources
import android.util.Log

/**
Copyright (C) 2025 EP Studios, Inc.
www.epstudiossoftware.com

Created by mannd on 3/17/25.

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
enum class TimeScaleType {
    TIME,
    TIME_NO_SECONDS,
    TIME_MILITARY,
    TIME_MILITARY_NO_SECONDS,
    DEBUG,
    YEAR,
    DAY,
    HOUR,
    MONTH,
    UNIVERSE,
    X_UNIVERSE_2,
    X_UNIVERSE,
    PERCENT,
    NONE,
    RAW;

    companion object {
        val realTimeTypes: Set<TimeScaleType> = setOf(
            TIME,
            TIME_NO_SECONDS,
            TIME_MILITARY,
            TIME_MILITARY_NO_SECONDS
        )
        private const val LOG_TAG = "MorbidMeterWidgetProvider"

        @JvmStatic
        fun fromInt(value: Int): TimeScaleType? {
            return TimeScaleType.values().find { it.ordinal == value }
        }

//        fun isRealTime(): Boolean {
//            return realTimeTypes.contains(this)
//        }

        /**
         * Array of integer references to the time scale names
         */
        @JvmStatic
        public val timescaleNameIds = intArrayOf(
            R.string.ts_time,
            R.string.ts_time_no_seconds,
            R.string.ts_time_military,
            R.string.ts_time_military_no_seconds,
            R.string.ts_debug,
            R.string.ts_year,
            R.string.ts_day,
            R.string.ts_hour,
            R.string.ts_month,
            R.string.ts_universe,
            R.string.ts_x_universe_2,
            R.string.ts_x_universe,
            R.string.ts_percent,
            R.string.ts_none,
            R.string.ts_raw
        )
    }

    fun getMyTimescaleTypes(resources: Resources): List<TimeScaleType> {
        val timescaleNames = resources.getStringArray(R.array.timescaletypes)
        val timescaleTypes = mutableListOf<TimeScaleType>()
        for (name in timescaleNames) {
            try {
                val timescaleType = TimeScaleType.valueOf(name)
                timescaleTypes.add(timescaleType)
            } catch (e: IllegalArgumentException) {
                // Handle the case where the string doesn't match an enum value
                // For example, log an error or skip the value
                Log.e(LOG_TAG, "Invalid TimeScaleType name: $name")
            }
        }
        return timescaleTypes
    }
}
