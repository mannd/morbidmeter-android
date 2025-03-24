package org.epstudios.morbidmeter

import android.content.Context

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
    val kind: TimeScaleKind // Type of time scale
    // TODO: do we need minumum and maximum or will duration be enough?
    val duration: Double // Duration of the time scale in milliseconds

    /**
     * Get the name of the time scale.
     * @param context Context to use for getting the string.
     * @return The name of the time scale.
     */
    fun getName(context: Context): String {
        return context.getString(nameId)
    }

    /**
     * Get the current time in the time scale.
     *
     * Note: The returned string has different functions
     * depending on the type of time scale.  For real time scales,
     * it is simply the format string for the current time.  For
     * the other scales it is the the current time relative to the
     * duration of the time scale.
     * @param context Context to use for getting the string.
     * @return The current time in the time scale.
     */
    fun getRealTimeFormat(context: Context): String? {
        return null
    }

    fun getTime(
        context: Context,
        percent: Double,
        direction: TimeScaleDirection = TimeScaleDirection.FORWARD): String? {
        return null
    }

    fun getProportionalTime(
        percent: Double,
        direction: TimeScaleDirection = TimeScaleDirection.FORWARD): Double {
        if (direction == TimeScaleDirection.FORWARD) {
            return percent * duration
        } else {
            return duration - (percent * duration)
        }
    }
}

