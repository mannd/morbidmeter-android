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

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

public class MorbidMeter extends AppWidgetProvider {
	public static String ACTION_WIDGET_REFRESH = "ActionReceiverRefresh";
	private static Boolean resetNotification = false;

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

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(ACTION_WIDGET_REFRESH)) {
			Log.d("DEBUG", "ACTION_WIDGET_REFRESH");
			AppWidgetManager appWidgetManager = AppWidgetManager
					.getInstance(context);
			int[] ids = appWidgetManager.getAppWidgetIds(new ComponentName(
					context, MorbidMeter.class));
			this.onUpdate(context, appWidgetManager, ids);
		} else
			super.onReceive(context, intent);
	}

	static void updateAppWidget(Context context,
			AppWidgetManager appWidgetManager, int appWidgetId,
			Configuration configuration) {
		Intent intent = new Intent(context, MorbidMeter.class);
		intent.setAction(ACTION_WIDGET_REFRESH);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
				intent, 0);

		RemoteViews updateViews = new RemoteViews(context.getPackageName(),
				R.layout.main);
		updateViews.setOnClickPendingIntent(R.id.update_button, pendingIntent);
		String time = getTime(context, configuration);
		String timeScaleName = "Timescale: ";
		if (configuration.reverseTime)
			timeScaleName += "REVERSE ";
		timeScaleName += configuration.timeScaleName + "\n";
		String userName = configuration.user.getName();
		if (userName.length() > 0) {
			if (userName.toUpperCase().charAt(userName.length() - 1) == 'S')
				userName += "'";
			else
				userName += "'s";
		}
		String label = userName + " MorbidMeter\n" + timeScaleName;
		if (configuration.user.isDead())
			label += context.getString(R.string.user_dead_message);
		else
			label += time;
		updateViews.setTextViewText(R.id.text, label);
		Boolean isMilestone = isMilestone(context, configuration, time);
		if (resetNotification && !isMilestone) // allow notifications once out
												// of milestone
			resetNotification = false;
		if (configuration.showNotifications && isMilestone
				&& !resetNotification) {
			resetNotification = true;
			NotificationManager notificationManager = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);
			Notification noty = new Notification(R.drawable.notificationskull,
					"Milestone reached", System.currentTimeMillis());
			noty.setLatestEventInfo(context, "Notice", "test", pendingIntent);
			if (configuration.notificationSound == R.id.default_sound)
				noty.defaults |= Notification.DEFAULT_SOUND;
			else if (configuration.notificationSound == R.id.mm_sound)
				noty.sound = Uri
						.parse("android.resource://org.epstudios.morbidmeter/raw/bellsnotification");
			notificationManager.notify(1, noty);
		}
		appWidgetManager.updateAppWidget(appWidgetId, updateViews);
	}

	// is public for testing
	public static Boolean isMilestone(Context context,
			Configuration configuration, String time) {
		if (configuration.timeScaleName.equals(context
				.getString(R.string.ts_year)))
			return isEvenHour(time);
		else if (configuration.timeScaleName.equals(context
				.getString(R.string.ts_month))
				|| configuration.timeScaleName.equals(context
						.getString(R.string.ts_day)))
			return isEvenMinute(time);
		else if (configuration.timeScaleName.equals(context
				.getString(R.string.ts_age))
				|| configuration.timeScaleName.equals(context
						.getString(R.string.ts_percent)))
			return isEvenPercentage(time);
		else if (configuration.timeScaleName.equals(context
				.getString(R.string.ts_universe)))
			return isEvenMillion(time);
		else
			return false;
	}

	public static Boolean isEvenHour(String time) {
		return time.contains(":00:");
	}

	public static Boolean isEvenMinute(String time) {
		return time.contains(":00 ");
	}

	public static Boolean isEvenPercentage(String time) {
		return time.contains(".0");
	}

	public static Boolean isEvenMillion(String time) {
		return time.matches(".+,000,... y");
	}

	public static String getTime(Context context, Configuration configuration) {
		String formatString = "";
		String timeString = "";
		String units = "";
		Format formatter = new DecimalFormat(formatString);
		TimeScale ts = new TimeScale();
		if (configuration.timeScaleName.equals(context
				.getString(R.string.ts_percent))) {
			ts = new TimeScale(configuration.timeScaleName, 0, 100);
			formatString += "#.000000";
			formatter = new DecimalFormat(formatString);
			units = "%";
			if (configuration.reverseTime)
				units += " left";

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
			formatString += "mm:ss";
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
			formatString += "##,###,###,###";
			formatter = new DecimalFormat(formatString);
			if (configuration.reverseTime)
				units = " years left";
			else
				units = " years from Big Bang";
		}
		// age in days does a different calculation
		if (configuration.timeScaleName.equals(context
				.getString(R.string.ts_age))) {
			long lifeInMsec = configuration.user.lifeDurationMsec();
			ts = new TimeScale(configuration.timeScaleName, 0, lifeInMsec);
			formatString += "#.000000";
			formatter = new DecimalFormat(formatString);

			if (configuration.reverseTime) {
				timeString = formatter.format(numDays(ts
						.reverseProportionalTime(configuration.user
								.percentAlive())));
				units = " days left";
			} else {
				timeString = formatter.format(numDays(ts
						.proportionalTime(configuration.user.percentAlive())));
				units = " days old";
			}
		} else {
			if (configuration.reverseTime) {
				timeString = formatter.format(ts
						.reverseProportionalTime(configuration.user
								.percentAlive()));
			} else {
				timeString = formatter.format(ts
						.proportionalTime(configuration.user.percentAlive()));
			}
		}
		if (configuration.useMsec && ts.okToUseMsec())
			timeString += " msec";
		timeString += units;
		return timeString;
	}

	public static double numDays(double timeInMsecs) {
		return (double) timeInMsecs / (24 * 60 * 60 * 1000);
	}

}
