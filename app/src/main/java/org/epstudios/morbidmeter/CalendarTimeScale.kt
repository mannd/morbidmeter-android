/*  MorbidMeter - Lifetime in perspective
    Copyright (C) 2011 EP Studios, Inc.
    www.epstudiossoftware.com

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.epstudios.morbidmeter

import java.util.Calendar

@Suppress("unused")
class CalendarTimeScale internal constructor(
    name: String?,
    private val minTime: Calendar,
    private val maxTime: Calendar
) : TimeScale(name, 0, 0) {
    public override fun okToUseMsec(): Boolean {
        return true
    }

    public override fun duration(): Long {
        return maxTime.getTimeInMillis() - minTime.getTimeInMillis()
    }

    public override fun proportionalTime(percent: Double): Double {
        return minTime.getTimeInMillis() + (percent * duration())
    }

    public override fun reverseProportionalTime(percent: Double): Double {
        return maxTime.getTimeInMillis() - (percent * duration())
    }
}
