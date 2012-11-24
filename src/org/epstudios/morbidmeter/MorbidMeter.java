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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.AlarmManager;
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
import android.widget.Toast;

public class MorbidMeter extends AppWidgetProvider {
	static final String ACTION_WIDGET_REFRESH = "ActionReceiverRefresh";
	static boolean notificationOngoing = false;

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		Toast.makeText(context, "onUpdate()", Toast.LENGTH_SHORT).show();

		final int count = appWidgetIds.length;

		for (int i = 0; i < count; i++) {
			int appWidgetId = appWidgetIds[i];
			Configuration configuration = MmConfigure.loadPrefs(context,
					appWidgetId);
			updateAppWidget(context, appWidgetManager, appWidgetId,
					configuration);
		}
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

	@Override
	public void onDisabled(Context context) {
		Toast.makeText(context, "onDisabled():last widget instance removed",
				Toast.LENGTH_SHORT).show();
		Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
		PendingIntent sender = PendingIntent
				.getBroadcast(context, 0, intent, 0);
		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(sender);
		super.onDisabled(context);
	}

	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
		// After after 3 seconds
		am.setRepeating(AlarmManager.RTC_WAKEUP,
				System.currentTimeMillis() + 1000 * 3, 1000, pi);
		Toast.makeText(context, "onEnabled()", Toast.LENGTH_SHORT).show();

	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(ACTION_WIDGET_REFRESH)) {
			Toast.makeText(context, "onReceive()", Toast.LENGTH_SHORT).show();
			Log.d("DEBUG", "ACTION_WIDGET_REFRESH");
			AppWidgetManager appWidgetManager = AppWidgetManager
					.getInstance(context);
			int[] ids = appWidgetManager.getAppWidgetIds(new ComponentName(
					context, MorbidMeter.class));
			Log.d("DEBUG", "ids.length = " + ids.length);
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
		// being dead is a milestone too!
		isMilestone = isMilestone || configuration.user.isDead();
		// if below true will ignore milestones and send notification with each
		// update
		if (notificationOngoing)
			if (!isMilestone)
				notificationOngoing = false;
		Log.d("DEBUG", "notificationOngoing = " + notificationOngoing);
		Boolean debugNotifications = false;
		if (debugNotifications
				|| (configuration.showNotifications && isMilestone && !notificationOngoing)) {
			NotificationManager notificationManager = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);
			Notification notification = new Notification(
					R.drawable.notificationskull, "MorbidMeter Milestone",
					System.currentTimeMillis());
			notification.flags |= Notification.FLAG_AUTO_CANCEL;
			Intent notificationIntent = new Intent(context, MorbidMeter.class);
			PendingIntent notyPendingIntent = PendingIntent.getActivity(
					context, 0, notificationIntent, 0);
			notification.setLatestEventInfo(context, "MorbidMeter", time,
					notyPendingIntent);
			if (configuration.notificationSound == R.id.default_sound)
				notification.defaults |= Notification.DEFAULT_SOUND;
			else if (configuration.notificationSound == R.id.mm_sound)
				notification.sound = Uri
						.parse("android.resource://org.epstudios.morbidmeter/raw/bellsnotification");
			notificationManager.notify(1, notification);
			notificationOngoing = true;
		}
		appWidgetManager.updateAppWidget(appWidgetId, updateViews);
	}

	// is public for testing
	public static Boolean isMilestone(Context context,
			Configuration configuration, String time) {
		if (configuration.timeScaleName.equals(context
				.getString(R.string.ts_year)))
			// return isTestTime(time); // for testing
			// return isEvenMinute(time); // for testing
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
		return time.contains(".000");
	}

	public static Boolean isEvenMillion(String time) {
		Pattern p = Pattern.compile(".*,000,... y.*", Pattern.DOTALL);
		Matcher m = p.matcher(time);
		return m.find();
	}

	// for testing, allows quicker notifications than usual
	public static Boolean isTestTime(String time) {
		Pattern p = Pattern.compile(".*[1369] [AP]M.*", Pattern.DOTALL);
		Matcher m = p.matcher(time);
		return m.find();
	}

	public static String getTime(Context context, Configuration configuration) {
		final String DECIMAL_FORMAT_STRING = "#.000000";
		String formatString = "";
		String timeString = "";
		String units = "";
		Format formatter = new DecimalFormat(formatString);
		TimeScale ts = new TimeScale();
		if (configuration.timeScaleName.equals(context
				.getString(R.string.ts_percent))) {
			ts = new TimeScale(configuration.timeScaleName, 0, 100);
			formatString += DECIMAL_FORMAT_STRING;
			formatter = new DecimalFormat(formatString);
			units = "%";
			if (configuration.reverseTime)
				units += " left";

		}
		if (configuration.timeScaleName.equals(context
				.getString(R.string.ts_debug))) {
			ts = new TimeScale(configuration.timeScaleName, 0, 0);
			long currentCalendarTime = Calendar.getInstance().getTimeInMillis();
			long currentSystemTime = System.currentTimeMillis();
			timeString = "SystemMsec = " + currentSystemTime + " msec";
			timeString += "\nCalendarTime = " + currentCalendarTime + " msec";
			timeString += "\nBDMsec = " + configuration.user.birthDayMsec()
					+ " msec";
			timeString += "\nDDMsec = " + configuration.user.deathDayMsec()
					+ " msec";
			timeString += "\n%Alive = " + configuration.user.percentAlive()
					+ "%";
			return timeString;
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
		// deal with raw time scales, i.e. real time
		if (configuration.timeScaleName.equals(context
				.getString(R.string.ts_raw))) {
			if (configuration.reverseTime)
				timeString = configuration.user.reverseMsecAlive()
						+ " msec remaining";
			else
				timeString = configuration.user.msecAlive() + " msec alive";
		} else if (configuration.timeScaleName.equals(context
				.getString(R.string.ts_seconds))) {
			if (configuration.reverseTime)
				timeString = configuration.user.reverseSecAlive()
						+ " sec remaining";
			else
				timeString = configuration.user.secAlive() + " sec alive";
		}
		// age in days or years does a different calculation
		else if (configuration.timeScaleName.equals(context
				.getString(R.string.ts_days))) {
			long lifeInMsec = configuration.user.lifeDurationMsec();
			ts = new TimeScale(configuration.timeScaleName, 0, lifeInMsec);
			formatString += DECIMAL_FORMAT_STRING;
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
		} else if (configuration.timeScaleName.equals(context
				.getString(R.string.ts_years))) {
			long lifeInMsec = configuration.user.lifeDurationMsec();
			ts = new TimeScale(configuration.timeScaleName, 0, lifeInMsec);
			formatString += DECIMAL_FORMAT_STRING;
			formatter = new DecimalFormat(formatString);

			if (configuration.reverseTime) {
				timeString = formatter.format(numYears(ts
						.reverseProportionalTime(configuration.user
								.percentAlive())));
				units = " years left";
			} else {
				timeString = formatter.format(numYears(ts
						.proportionalTime(configuration.user.percentAlive())));
				units = " years old";
			}
		} else if (configuration.timeScaleName.equals(context
				.getString(R.string.ts_hours))) {
			long lifeInMsec = configuration.user.lifeDurationMsec();
			ts = new TimeScale(configuration.timeScaleName, 0, lifeInMsec);
			formatString += DECIMAL_FORMAT_STRING;
			formatter = new DecimalFormat(formatString);

			if (configuration.reverseTime) {
				timeString = formatter.format(numHours(ts
						.reverseProportionalTime(configuration.user
								.percentAlive())));
				units = " hours left";
			} else {
				timeString = formatter.format(numHours(ts
						.proportionalTime(configuration.user.percentAlive())));
				units = " hours old";
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
		return timeInMsecs / (24 * 60 * 60 * 1000.0);
	}

	public static double numYears(double timeInMsecs) {
		return numDays(timeInMsecs) / 365.25;
	}

	public static double numHours(double timeInMsecs) {
		return timeInMsecs / (60 * 60 * 1000);
	}

}
