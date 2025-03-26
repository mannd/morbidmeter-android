package org.epstudios.morbidmeter.test

import android.content.Context
import junit.framework.TestCase
import org.epstudios.morbidmeter.timescale.PercentTimeScale
import org.epstudios.morbidmeter.timescale.TimeScaleDirection
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

/**
Copyright (C) 2025 EP Studios, Inc.
www.epstudiossoftware.com

Created by mannd on 3/23/25.

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

class NewTimeScaleTests : TestCase() {
    fun testPercentTimeScale() {
        val mockContext = mock(Context::class.java)
        val ts = PercentTimeScale()
        assertEquals(100.0, ts.duration)
        assertEquals(0.0, ts.getProportionalTime(0.0, TimeScaleDirection.FORWARD))
        assertEquals(100.0, ts.getProportionalTime(1.0, TimeScaleDirection.FORWARD))
        assertEquals(100.0, ts.getProportionalTime(0.0, TimeScaleDirection.REVERSE))
        assertEquals(50.0, ts.getProportionalTime(0.5, TimeScaleDirection.FORWARD))
        assertEquals("50.000000%", ts.getTime(mockContext, 0.5, TimeScaleDirection.FORWARD))
    }
}
