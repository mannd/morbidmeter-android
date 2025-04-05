package org.epstudios.morbidmeter.timescale

import android.content.Context
import android.content.res.Resources
import android.util.Log
import org.epstudios.morbidmeter.R

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

/**
 * Enum representing the different types of time scales.
 */
enum class TimeScaleType {
    LONG_TIME,
    SHORT_TIME,
    LONG_MILITARY_TIME,
    SHORT_MILITARY_TIME,
    MILLISECONDS,
    SECONDS,
    MINUTES,
    HOURS,
    DAYS,
    WEEKS,
    MONTHS,
    YEARS,
    DAYS_HOURS_MINUTES_SECONDS,
    DAYS_HOURS_MINUTES,
    ONE_DAY,
    ONE_HOUR,
    ONE_WEEK,
    ONE_MONTH,
    ONE_YEAR,
    UNIVERSE,
    X_UNIVERSE_2,
    X_UNIVERSE,
    PERCENT,
    NONE,
    RAW;

    companion object {
        val realTimeTypes: Set<TimeScaleType> = setOf(
            LONG_TIME,
            SHORT_TIME,
            LONG_MILITARY_TIME,
            SHORT_MILITARY_TIME
        )
        private const val LOG_TAG = "TimeScaleType"

        @JvmStatic
        fun fromInt(value: Int): TimeScaleType? {
            return TimeScaleType.entries.find { it.ordinal == value }
        }

        @JvmStatic
        fun indexFromString(stringId: Int): Int {
            return timescaleNameIds.indexOf(stringId)
        }

        @JvmStatic
        fun fromStringId(stringId: Int): TimeScaleType? {
            val index = indexFromString(stringId)
            if (index != -1) {
                return fromInt(index)
            } else {
                return null
            }
        }

        @JvmStatic
        fun isRealTime(stringId: Int): Boolean {
            return realTimeTypes.contains(fromStringId(stringId))
        }

        /**
         * Array of integer references to the time scale names.
         */
        @JvmStatic
        public val timescaleNameIds = intArrayOf(
            R.string.ts_long_time,
            R.string.ts_short_time,
            R.string.ts_long_military_time,
            R.string.ts_short_military_time,
            R.string.ts_raw,
            R.string.ts_seconds,
            R.string.ts_minutes,
            R.string.ts_hours,
            R.string.ts_days,
            R.string.ts_weeks,
            R.string.ts_months,
            R.string.ts_years,
            R.string.ts_d_h_m_s,
            R.string.ts_d_h_m,
            R.string.ts_day,
            R.string.ts_hour,
            R.string.ts_week,
            R.string.ts_month,
            R.string.ts_year,
            R.string.ts_universe,
            R.string.ts_x_universe_2,
            R.string.ts_x_universe,
            R.string.ts_percent,
            R.string.ts_none,
            R.string.ts_debug
        )

        @JvmStatic
        fun getStringArray(context: Context): Array<String> {
            return timescaleNameIds.map {
                context.getString(it) }.toTypedArray()
        }
    }

    fun getMyTimescaleTypes(resources: Resources): List<TimeScaleType> {
        val timescaleNames = resources.getStringArray(R.array.timescaletypes)
        val timescaleTypes = mutableListOf<TimeScaleType>()
        for (name in timescaleNames) {
            try {
                val timescaleType = valueOf(name)
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

/**
 * Time can only go forwards or backwards...
 */
enum class TimeScaleDirection {
    FORWARD,
    REVERSE
}
