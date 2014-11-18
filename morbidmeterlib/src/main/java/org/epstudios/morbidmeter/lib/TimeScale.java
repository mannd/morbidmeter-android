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

import java.text.DecimalFormat;
import java.text.Format;

public class TimeScale {
	public enum Duration {
		YEAR, DAY, HOUR, MONTH, PERCENT, UNIVERSE, AGE, RAW
	};

	public TimeScale() {
		this.name = "";
		this.minimum = 0L;
		this.maximum = 0L;
		formatString = "#";
		formatter = new DecimalFormat(formatString);
	}

	public TimeScale(String name, long minimum, long maximum) {
		this.name = name;
		this.minimum = minimum;
		this.maximum = maximum;
	}

	public boolean okToUseMsec() {
		return false;
	}

	public long duration() {
		return maximum - minimum;
	}

	public double proportionalTime(double percent) {
		return minimum + percent * duration();
	}

	public double reverseProportionalTime(double percent) {
		return maximum - percent * duration();
	}

	public String getName() {
		return name;
	}

	public Format getFormatter() {
		return formatter;
	}

	public void setFormatter(Format formatter) {
		this.formatter = formatter;
	}

	public void setFormatString(String formatString) {
		this.formatString = formatString;
	}

	private final String name;
	private final long maximum;
	private final long minimum;
	private Format formatter;
	private String formatString;

}
