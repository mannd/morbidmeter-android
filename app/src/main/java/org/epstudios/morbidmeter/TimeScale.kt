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
    val nameId: Int
    private val maximum: Long
    private val minimum: Long
    var formatter: Format? = null
    private var formatString: String? = null
    private var timeScaleType: TimeScaleType? = null

    internal constructor() {
        this.nameId = 0
        this.minimum = 0L
        this.maximum = 0L
        formatString = "#"
        formatter = DecimalFormat(formatString)
        timeScaleType = TimeScaleType.NONE
    }

    constructor(nameId: Int, minimum: Long, maximum: Long) {
        this.nameId = nameId
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

}
