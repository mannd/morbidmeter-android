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

import android.content.res.Resources
import java.text.DecimalFormat
import java.text.Format
import kotlin.text.substring

open class TimeScale {
    val name: String?
    private val maximum: Long
    private val minimum: Long
    var formatter: Format? = null
    private var formatString: String? = null
    private var timeScaleType: TimeScaleType? = null

    internal constructor() {
        this.name = ""
        this.minimum = 0L
        this.maximum = 0L
        formatString = "#"
        formatter = DecimalFormat(formatString)
        timeScaleType = TimeScaleType.NONE
    }

    constructor(name: String?, minimum: Long, maximum: Long) {
        this.name = name
        this.minimum = minimum
        this.maximum = maximum
    }

    open fun okToUseMsec(): Boolean {
        return false
    }

    open fun duration(): Long {
        return maximum - minimum
    }

    open fun proportionalTime(percent: Double): Double {
        return minimum + (percent * duration())
    }

    open fun reverseProportionalTime(percent: Double): Double {
        return maximum - (percent * duration())
    }

    fun setFormatString(formatString: String?) {
        this.formatString = formatString
    }

    @Suppress("unused")
    enum class Duration {
        YEAR, DAY, HOUR, MONTH, PERCENT, UNIVERSE, AGE, RAW
    }

    /**
     * Get the index of a string in the timescales array
     *
     * @param resources the Resources to retrieve the array
     * @param timeScaleName the string to find the index of
     * @return the index of the string in the array, or -1 if the string is not found
     */
    fun getTimeScaleIndex(resources: Resources, timeScaleName: String): Int {
        val timescaleNames = resources.getStringArray(R.array.timescales)
        for (i in timescaleNames.indices) {
            val resourceId = resources.getIdentifier(
                timescaleNames[i].substring(1),
                "string",
                "org.epstudios.morbidmeter"
            )
            val actualString = resources.getString(resourceId)

            if (actualString == timeScaleName) {
                return i
            }
        }
        return -1 // Not found
    }
}
