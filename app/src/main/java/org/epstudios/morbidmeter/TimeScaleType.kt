package org.epstudios.morbidmeter

import android.content.res.Resources
import android.util.Log
import org.epstudios.morbidmeter.TimeScaleType

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
    NONE,
    PERCENT,
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
    RAW;

    companion object {
        val realTimeTypes: Set<TimeScaleType> = setOf(
            TIME,
            TIME_NO_SECONDS,
            TIME_MILITARY,
            TIME_MILITARY_NO_SECONDS
        )
        private const val LOG_TAG = "MorbidMeterWidgetProvider"
    }

    fun isRealTime(): Boolean {
        return realTimeTypes.contains(this)
    }

    fun getMyTimescaleTypes(resources: Resources): List<TimeScaleType> {
        val timescaleNames = resources.getStringArray(R.array.my_timescales)
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
