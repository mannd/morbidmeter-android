package org.epstudios.morbidmeter.timescale

import org.epstudios.morbidmeter.R
import java.util.Calendar
import java.util.GregorianCalendar

/**
Copyright (C) 2025 EP Studios, Inc.
www.epstudiossoftware.com

Created by mannd on 4/5/25.

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
 * TimeScale that maps a time duration onto one month.
 */
class OneMonthTimeScale : CalendarTimeScale() {
    override val type: TimeScaleType = TimeScaleType.ONE_MONTH
    override val nameId: Int = R.string.ts_month
    override val formatString = "MMMM d\nh:mm:ss a"
    override val minTime: Calendar =
        GregorianCalendar(2000, Calendar.JANUARY, 1, 0, 0, 0)
    override val maxTime: Calendar =
        GregorianCalendar(2000, Calendar.FEBRUARY, 1, 0, 0, 0)
}
