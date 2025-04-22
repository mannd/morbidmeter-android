package org.epstudios.morbidmeter

import org.epstudios.morbidmeter.timescale.PercentTimeScale
import org.epstudios.morbidmeter.timescale.TimeScaleDirection
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
Copyright (C) 2025 EP Studios, Inc.
www.epstudiossoftware.com

Created by mannd on 4/6/25.

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
class PercentTimeScaleTest {

    @Test
    fun testMaxInterval() {
        val scale = PercentTimeScale()
        assertEquals(100, scale.maxValue)
    }

    @Test
    fun testPercentOfMaxInterval()
    {
        val scale = PercentTimeScale()
        val percent = 0.5
        val direction = TimeScaleDirection.FORWARD
        val maxInterval = 100L
        val result = scale.getPercentOfMaxInterval(percent, direction, maxInterval)
        assertEquals(50.0, result)
        val percent2 = 0.0
        val result2 = scale.getPercentOfMaxInterval(percent2, direction, maxInterval)
        assertEquals(0.0, result2)
        val percent3 = 1.0
        val result3 = scale.getPercentOfMaxInterval(percent3, direction, maxInterval)
        assertEquals(100.0, result3)
        val percent4 = 1.1
        val result4 = scale.getPercentOfMaxInterval(percent4, direction, maxInterval)
        assertEquals(100.0, result4)
        val percent5 = -0.1
        val result5 = scale.getPercentOfMaxInterval(percent5, direction, maxInterval)
        assertEquals(0.0, result5)
    }
}
