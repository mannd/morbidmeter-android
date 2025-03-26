package org.epstudios.morbidmeter

/**
Copyright (C) 2025 EP Studios, Inc.
www.epstudiossoftware.com

Created by mannd on 3/25/25.

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

class DaysTimeScale : TimeScale {
    override val type: TimeScaleType = TimeScaleType.DAYS
    override val nameId: Int = R.string.ts_days
    override val kind: TimeScaleKind = TimeScaleKind.DURATION
    override val duration: Double = 0.0

//            long lifeInMsec = configuration.user.lifeDurationMsec();
//            ts = new SimpleTimeScale(configuration.timeScaleNameId, 0, lifeInMsec);
//            formatString = SHORT_DECIMAL_FORMAT_STRING;
//            formatter = new DecimalFormat(formatString);
//
//            if (configuration.reverseTime) {
//                timeString = formatter.format(numDays(ts
//                        .reverseProportionalTime(configuration.user
//                                .percentAlive())));
//                units = " days left";
//            } else {
//                timeString = formatter.format(numDays(ts
//                        .proportionalTime(configuration.user.percentAlive())));
//                units = " days old";
}
