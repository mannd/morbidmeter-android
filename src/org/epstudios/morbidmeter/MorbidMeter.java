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

package org.epstudios.morbidmeter;

import java.text.DecimalFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

public class MorbidMeter extends AppWidgetProvider {
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		final int count = appWidgetIds.length;

		for (int i = 0; i < count; i++) {
			int appWidgetId = appWidgetIds[i];
			Configuration configuration = MmConfigure.loadPrefs(context,
					appWidgetId);
			updateAppWidget(context, appWidgetManager, appWidgetId,
					configuration);
		}
	}

	static void updateAppWidget(Context context,
			AppWidgetManager appWidgetManager, int appWidgetId,
			Configuration configuration) {
		RemoteViews updateViews = new RemoteViews(context.getPackageName(),
				R.layout.main);
		String time = getTime(context, configuration);
		String timeScaleName = "Timescale: ";
		if (configuration.reverseTime)
			timeScaleName += "REVERSE ";
		timeScaleName += configuration.timeScaleName + "\n";
		String userName = configuration.user.getName();
		if (userName.toUpperCase().charAt(userName.length() - 1) == 'S')
			userName += "'";
		else
			userName += "'s";
		String label = userName + " MorbidMeter\n" + timeScaleName;
		if (configuration.user.isDead())
			label += context.getString(R.string.user_dead_message);
		else
			label += time;
		updateViews.setTextViewText(R.id.text, label);
		appWidgetManager.updateAppWidget(appWidgetId, updateViews);
	}

	public static String getTime(Context context, Configuration configuration) {
		String formatString = "";
		String timeString = "";
		Format formatter = new DecimalFormat(formatString);
		TimeScale ts = new TimeScale();
		if (configuration.timeScaleName.equals(context
				.getString(R.string.ts_percent))) {
			ts = new TimeScale(configuration.timeScaleName, 0, 100);
			formatString += "#.000000";
			formatter = new DecimalFormat(formatString);

		}
		if (configuration.timeScaleName.equals(context
				.getString(R.string.ts_year))) {
			ts = new CalendarTimeScale(configuration.timeScaleName,
					new GregorianCalendar(2000, Calendar.JANUARY, 1),
					new GregorianCalendar(2001, Calendar.JANUARY, 1));
			formatString += "MMMM d\nh:mm:ss a";
			if (configuration.useMsec)
				formatString += " S";
			formatter = new SimpleDateFormat(formatString);
		}
		if (configuration.timeScaleName.equals(context
				.getString(R.string.ts_day))) {
			ts = new CalendarTimeScale(configuration.timeScaleName,
					new GregorianCalendar(2000, Calendar.JANUARY, 1),
					new GregorianCalendar(2000, Calendar.JANUARY, 2));
			formatString += "h:mm:ss a";
			if (configuration.useMsec)
				formatString += " S";
			formatter = new SimpleDateFormat(formatString);
		}
		if (configuration.timeScaleName.equals(context
				.getString(R.string.ts_hour))) {
			ts = new CalendarTimeScale(configuration.timeScaleName,
					new GregorianCalendar(2000, Calendar.JANUARY, 1, 11, 0, 0),
					new GregorianCalendar(2000, Calendar.JANUARY, 1, 12, 0, 0));
			formatString += "h:mm:ss";
			if (configuration.useMsec)
				formatString += " S";
			formatter = new SimpleDateFormat(formatString);
		}
		if (configuration.timeScaleName.equals(context
				.getString(R.string.ts_month))) {
			ts = new CalendarTimeScale(configuration.timeScaleName,
					new GregorianCalendar(2000, Calendar.JANUARY, 1),
					new GregorianCalendar(2000, Calendar.FEBRUARY, 1));
			formatString += "MMMM d\nh:mm:ss a";
			if (configuration.useMsec)
				formatString += " S";
			formatter = new SimpleDateFormat(formatString);
		}
		if (configuration.timeScaleName.equals(context
				.getString(R.string.ts_universe))) {
			ts = new TimeScale(configuration.timeScaleName, 0, 15000000000L);
			formatString += "#";
			formatter = new DecimalFormat(formatString);
		}
		// fix age
		if (configuration.timeScaleName.equals(context
				.getString(R.string.ts_age))) {
			ts = new CalendarTimeScale(configuration.timeScaleName,
					configuration.user.birthDay(),
					configuration.user.deathDay());
			formatString += "#.0000";
			formatter = new DecimalFormat(formatString);
		}
		if (configuration.reverseTime)
			timeString = formatter
					.format(ts.reverseProportionalTime(configuration.user
							.percentAlive()));
		else
			timeString = formatter.format(ts
					.proportionalTime(configuration.user.percentAlive()));
		if (configuration.useMsec && ts.okToUseMsec())
			timeString += " msec";
		return timeString;
	}
}
