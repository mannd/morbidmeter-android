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

import android.content.Context;

public class MorbidMeterClock {

	private String userName;
	private String timeScaleName;
	private Calendar birthday;
	private double longevity;
	private static Configuration configuration = null;

	// public MorbidMeterClock(String userName, String timeScaleName,
	// Calendar birthday, double longevity) {
	// this.userName = userName;
	// this.timeScaleName = timeScaleName;
	// this.birthday = birthday;
	// this.longevity = longevity;
	//
	// }
	//
	// public MorbidMeterClock(Configuration configuration) {
	// this.configuration = configuration;
	// }

	public static void loadConfiguration(Context context, int appWidgetId) {
		if (configuration == null) {
			resetConfiguration(context, appWidgetId);
		}
	}

	public static void resetConfiguration(Context context, int appWidgetId) {
		configuration = MmConfigure.loadPrefs(context, appWidgetId);

	}

	public static String getLabel() {
		String timeScaleName = "Timescale: ";
		if (configuration.reverseTime)
			timeScaleName += "REVERSE ";
		timeScaleName += configuration.timeScaleName + "\n";
		String userName = configuration.user.getApostrophedName();
		String label = userName + " MorbidMeter\n" + timeScaleName;
		return label;
	}

	// public static Calendar getBirthday() {
	// return birthday;
	// }

	static public String getFormattedTime() {
		// for testing, clock time for now
		Format formatter = new SimpleDateFormat("hh:mm:ss a");
		String time = formatter.format(new Date());
		return time;
	}

}
