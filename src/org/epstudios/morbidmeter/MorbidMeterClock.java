/*  MorbidMeter - Lifetime in perspective 
    Copyright (C) 2014 EP Studios, Inc.
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

package org.epstudios.morbidmeter;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MorbidMeterClock {
	public MorbidMeterClock(String userName, String timeScaleName,
			Calendar birthday, double longevity) {
		this.userName = userName;
		this.timeScaleName = timeScaleName;
		this.birthday = birthday;
		this.longevity = longevity;

	}

	public MorbidMeterClock(Configuration configuration) {
		this.configuration = configuration;
	}

	public String getLabel() {
		String timeScaleName = "Timescale: ";
		if (configuration.reverseTime)
			timeScaleName += "REVERSE ";
		timeScaleName += configuration.timeScaleName + "\n";
		String userName = configuration.user.getName();
		if (userName.length() > 0) {
			if (userName.toUpperCase(Locale.getDefault()).charAt(
					userName.length() - 1) == 'S')
				userName += "'";
			else
				userName += "'s";
		}
		String label = userName + " MorbidMeter\n" + timeScaleName;
		return label;
	}

	public Calendar getBirthday() {
		return birthday;
	}

	static public String getFormattedTime() {
		// for testing, clock time for now
		Format formatter = new SimpleDateFormat("hh:mm:ss a");
		String time = formatter.format(new Date());
		return time;
	}

	private String userName;
	private String timeScaleName;
	private Calendar birthday;
	private double longevity;
	private Configuration configuration;
}
