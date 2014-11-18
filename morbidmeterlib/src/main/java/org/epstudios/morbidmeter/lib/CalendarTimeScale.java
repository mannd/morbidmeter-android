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

package org.epstudios.morbidmeter.lib;

import java.util.Calendar;

public class CalendarTimeScale extends TimeScale {
	public CalendarTimeScale(String name, Calendar minTime, Calendar maxTime) {
		super(name, 0, 0);
		this.minTime = minTime;
		this.maxTime = maxTime;
	}

	@Override
	public boolean okToUseMsec() {
		return true;
	}

	@Override
	public long duration() {
		return maxTime.getTimeInMillis() - minTime.getTimeInMillis();
	}

	@Override
	public double proportionalTime(double percent) {
		return minTime.getTimeInMillis() + percent * duration();
	}

	@Override
	public double reverseProportionalTime(double percent) {
		return maxTime.getTimeInMillis() - percent * duration();
	}

	private final Calendar minTime;
	private final Calendar maxTime;
}
