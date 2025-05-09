package org.epstudios.morbidmeter.timescale

import android.content.Context
import org.epstudios.morbidmeter.R
import java.text.DecimalFormat

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

class UniverseTimeScale : PercentTimeScale() {
    override val type: TimeScaleType = TimeScaleType.UNIVERSE
    override val nameId: Int = R.string.ts_universe

    override val formatString = "##,###,###,###"
    override val maxValue = 15000000000L

    override fun getPercentTime(
        context: Context,
        percent: Double,
        direction: TimeScaleDirection
    ): String {
        val formatter = DecimalFormat(formatString)
        val percentString = formatter.format(getPercentOfMaxInterval(
            percent,
            direction,
            maxValue))
        return if (direction == TimeScaleDirection.FORWARD) {
            context.getString(R.string.universe_result, percentString)
        } else {
            context.getString(R.string.reverse_universe_result, percentString)
        }
    }
}
