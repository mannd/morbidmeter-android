package org.epstudios.morbidmeter.test

import org.epstudios.morbidmeter.R
import android.content.Context
import android.provider.Settings.Global.getString
import junit.framework.TestCase
import org.epstudios.morbidmeter.timescale.PercentTimeScale
import org.epstudios.morbidmeter.timescale.TimeScaleDirection
import org.junit.Before
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner


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

@RunWith(MockitoJUnitRunner::class)
class NewTimeScaleTests {
    @Test
    fun testPercentTimeScale() {
        val ts = PercentTimeScale()
        // TODO: use raw percent, don't bother mocking context
//        `when`(mockContext.getString(R.string.percent_result))
//            .thenReturn("%1$s")
//        assert("0.000000%" == ts.getPercentTime(mockContext, 0.0, TimeScaleDirection.FORWARD))
//        assertEquals("50.000000%", ts.getPercentTime(mockContext, 0.5, TimeScaleDirection.FORWARD))
//        assertEquals("100.000000%", ts.getPercentTime(mockContext, 1.0, TimeScaleDirection.FORWARD))
    }
}
