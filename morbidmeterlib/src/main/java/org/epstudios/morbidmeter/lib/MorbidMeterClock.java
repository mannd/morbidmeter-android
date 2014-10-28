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

package org.epstudios.morbidmeter.lib;

import java.text.DecimalFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

public class MorbidMeterClock {

	private static final String DEPRECATION = "deprecation";
	private static final String LOG_TAG = "MM";
	private static Configuration configuration = null;
	private static int appWidgetId = 0;
	private static final String PREFS_NAME = "org.epstudios.morbidmeter.MmConfigure";
	private static final String IN_MILESTONE = "in_milestone";

	public static void resetConfiguration(Context context, int appWidgetId) {
		configuration = MmConfigure.loadPrefs(context, appWidgetId);
		MorbidMeterClock.appWidgetId = appWidgetId;
		Log.d(LOG_TAG, "resetConfiguration, appWidgetId = " + appWidgetId);

	}

	public static int getFrequency(Context context) {
		String frequencyString = configuration.updateFrequency;
		int frequency = -1; // shut off clock for error
		if (frequencyString.equals(context.getString(R.string.one_sec)))
			frequency = 1000;
		else if (frequencyString.equals(context.getString(R.string.five_sec)))
			frequency = 1000 * 5;
		else if (frequencyString
				.equals(context.getString(R.string.fifteen_sec)))
			frequency = 1000 * 15;
		else if (frequencyString.equals(context.getString(R.string.thirty_sec)))
			frequency = 1000 * 30;
		else if (frequencyString.equals(context.getString(R.string.one_min)))
			frequency = 1000 * 60;
		else if (frequencyString
				.equals(context.getString(R.string.fifteen_min)))
			frequency = 1000 * 60 * 15;
		else if (frequencyString.equals(context.getString(R.string.thirty_min)))
			frequency = 1000 * 60 * 30;
		else if (frequencyString.equals(context.getString(R.string.one_hour)))
			frequency = 1000 * 60 * 60;
		return frequency;
	}

	public static void getLastConfiguration(Context context) {
		configuration = MmConfigure.loadPrefs(context,
				MmConfigure.loadLastAppWidgetId(context));
	}

	public static boolean configurationIsComplete() {
		return configuration.configurationComplete;
	}

	public static String getLabel() {
		String timeScaleName = "Timescale:\n";
		if (configuration.reverseTime)
			timeScaleName += "REVERSE ";
		timeScaleName += configuration.timeScaleName;
		String userName = configuration.user.getApostrophedName();
		String label = userName + " MorbidMeter\n" + timeScaleName;
		return label;
	}

	static public String getFormattedTime(Context context) {

		final String DECIMAL_FORMAT_STRING = "#.000000";
		final String SHORT_DECIMAL_FORMAT_STRING = "#,###.0000";
		String formatString = "";
		String timeString = "";
		String units = "";
		// Log.d(LOG_TAG, "percent alive = " +
		// configuration.user.percentAlive());
		// Log.d(LOG_TAG, "birthday msec = " +
		// configuration.user.birthDayMsec());
		Format formatter = new DecimalFormat(formatString);
		TimeScale ts = new TimeScale();
		if (configuration.user.percentAlive() >= 1.0) {
			if (configuration.showNotifications) {
				showNotification(context,
						context.getString(R.string.user_dead_message));
				return context.getString(R.string.user_dead_message);
			}
		}
		if (configuration.timeScaleName.equals(context
				.getString(R.string.ts_none))) {
			return "0";
		}
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
				.getString(R.string.ts_time))) {
			formatter = new SimpleDateFormat("EEEE, MMMM d yyyy\nhh:mm:ss a z",
					Locale.getDefault());
			timeString = formatter.format(new Date());
			return timeString; // early exit
		}
		if (configuration.timeScaleName.equals(context
				.getString(R.string.ts_debug))) {
			ts = new TimeScale(configuration.timeScaleName, 0, 0);
			long currentSystemTime = System.currentTimeMillis();
			timeString = "System Time " + currentSystemTime + " ms";
			timeString += "\nBirth " + configuration.user.birthDayMsec()
					+ " ms";
			timeString += "\nDeath " + configuration.user.deathDayMsec()
					+ " ms";
			timeString += "\n%Alive " + configuration.user.percentAlive() + "%";
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
			formatter = new SimpleDateFormat(formatString, Locale.getDefault());
		}
		if (configuration.timeScaleName.equals(context
				.getString(R.string.ts_day))) {
			ts = new CalendarTimeScale(configuration.timeScaleName,
					new GregorianCalendar(2000, Calendar.JANUARY, 1),
					new GregorianCalendar(2000, Calendar.JANUARY, 2));
			formatString += "h:mm:ss a";
			if (configuration.useMsec)
				formatString += " S";
			formatter = new SimpleDateFormat(formatString, Locale.getDefault());
		}
		if (configuration.timeScaleName.equals(context
				.getString(R.string.ts_hour))) {
			ts = new CalendarTimeScale(configuration.timeScaleName,
					new GregorianCalendar(2000, Calendar.JANUARY, 1, 11, 0, 0),
					new GregorianCalendar(2000, Calendar.JANUARY, 1, 12, 0, 0));
			formatString += "hh:mm:ss";
			if (configuration.useMsec)
				formatString += " S";
			formatter = new SimpleDateFormat(formatString, Locale.getDefault());
		}
		if (configuration.timeScaleName.equals(context
				.getString(R.string.ts_month))) {
			ts = new CalendarTimeScale(configuration.timeScaleName,
					new GregorianCalendar(2000, Calendar.JANUARY, 1),
					new GregorianCalendar(2000, Calendar.FEBRUARY, 1));
			formatString += "MMMM d\nh:mm:ss a";
			if (configuration.useMsec)
				formatString += " S";
			formatter = new SimpleDateFormat(formatString, Locale.getDefault());
		}
		if (configuration.timeScaleName.equals(context
				.getString(R.string.ts_universe))) {
			ts = new TimeScale(configuration.timeScaleName, 0, 15000000000L);
			formatString += "##,###,###,###";
			formatter = new DecimalFormat(formatString);
			if (configuration.reverseTime)
				units = " yrs to Present";
			else
				units = " yrs from Big Bang";
		}
		if (configuration.timeScaleName.equals(context
				.getString(R.string.ts_x_universe_2))) {
			ts = new TimeScale(configuration.timeScaleName, 0, 6000L);
			formatString += "##,###,###,###.0000";
			formatter = new DecimalFormat(formatString);
			if (configuration.reverseTime)
				units = " yrs to Armageddon";
			else
				units = " yrs from Creation";
		}
		if (configuration.timeScaleName.equals(context
				.getString(R.string.ts_x_universe))) {
			ts = new CalendarTimeScale(configuration.timeScaleName,
					new GregorianCalendar(-4000, Calendar.JANUARY, 1),
					new GregorianCalendar(2001, Calendar.JANUARY, 1));
			formatString += "y G MMMM d\nh:mm:ss a";
			formatter = new SimpleDateFormat(formatString, Locale.getDefault());

		}

		// deal with raw time scales, i.e. real time
		if (configuration.timeScaleName.equals(context
				.getString(R.string.ts_raw))) {
			formatString += "#,###";
			formatter = new DecimalFormat(formatString);

			if (configuration.reverseTime)
				timeString = formatter.format(configuration.user
						.reverseMsecAlive()) + " msec remaining";
			else
				timeString = formatter.format(configuration.user.msecAlive())
						+ " msec alive";
		} else if (configuration.timeScaleName.equals(context
				.getString(R.string.ts_seconds))) {
			formatString += "#,###";
			formatter = new DecimalFormat(formatString);

			if (configuration.reverseTime)
				timeString = formatter.format(configuration.user
						.reverseSecAlive()) + " sec remaining";
			else
				timeString = formatter.format(configuration.user.secAlive())
						+ " sec alive";
		}
		// age in days or years does a different calculation
		else if (configuration.timeScaleName.equals(context
				.getString(R.string.ts_days))) {
			long lifeInMsec = configuration.user.lifeDurationMsec();
			ts = new TimeScale(configuration.timeScaleName, 0, lifeInMsec);
			formatString += SHORT_DECIMAL_FORMAT_STRING;
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
			formatString += SHORT_DECIMAL_FORMAT_STRING;
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
		} else if (configuration.timeScaleName.equals(context
				.getString(R.string.ts_minutes))) {
			long lifeInMsec = configuration.user.lifeDurationMsec();
			ts = new TimeScale(configuration.timeScaleName, 0, lifeInMsec);
			formatString += SHORT_DECIMAL_FORMAT_STRING;
			formatter = new DecimalFormat(formatString);

			if (configuration.reverseTime) {
				timeString = formatter.format(numMinutes(ts
						.reverseProportionalTime(configuration.user
								.percentAlive())));
				units = " mins left";
			} else {
				timeString = formatter.format(numMinutes(ts
						.proportionalTime(configuration.user.percentAlive())));
				units = " mins old";
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

		if (configuration.showNotifications) {
			showNotification(context, timeString);
		}

		return timeString;
	}

	public static int percentAlive() {
		return (int) (configuration.user.percentAlive() * 100);
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

	public static double numMinutes(double timeInMsecs) {
		return timeInMsecs / (60 * 1000);
	}

	@SuppressWarnings(DEPRECATION)
	public static void showNotification(Context context, String time) {

		Boolean userDead = time.equals(context
				.getString(R.string.user_dead_message));
		Boolean atMilestone = isMilestone(context, time);
		Boolean inMilestone;
		if ((atMilestone || userDead)) {
			SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME,
					0);
			inMilestone = prefs.getBoolean(IN_MILESTONE + appWidgetId, false);
			if (!inMilestone) {
				NotificationManager notificationManager = (NotificationManager) context
						.getSystemService(Context.NOTIFICATION_SERVICE);
				Notification notification = new Notification(
						R.drawable.notificationskull, "MorbidMeter Milestone",
						System.currentTimeMillis());
				notification.flags |= Notification.FLAG_AUTO_CANCEL;
				Intent notificationIntent = new Intent(context,
						MorbidMeter.class);
				PendingIntent notificationPendingIntent = PendingIntent
						.getActivity(context, 0, notificationIntent, 0);
				notification.setLatestEventInfo(context, "MorbidMeter", time,
						notificationPendingIntent);
				if (configuration.notificationSound == R.id.default_sound)
					notification.defaults |= Notification.DEFAULT_SOUND;
				else if (configuration.notificationSound == R.id.mm_sound)
					notification.sound = Uri
							.parse("android.resource://org.epstudios.morbidmeter/raw/bellsnotification");
				notificationManager.notify(1, notification);
				inMilestone = true;
			}
		} else {
			inMilestone = false;
		}
		SharedPreferences.Editor prefsEditor = context.getSharedPreferences(
				PREFS_NAME, 0).edit();
		prefsEditor.putBoolean(IN_MILESTONE + appWidgetId, inMilestone);
		prefsEditor.commit();

	}

	public static Boolean isMilestone(Context context, String time) {
		if (configuration.timeScaleName.equals(context
				.getString(R.string.ts_year))) {
			return isEvenHour(time);
		} else if (configuration.timeScaleName.equals(context
				.getString(R.string.ts_month))
				|| configuration.timeScaleName.equals(context
						.getString(R.string.ts_day))) {
			return isEvenMinute(time);
		} else if (configuration.timeScaleName.equals(context
				.getString(R.string.ts_percent))) {
			return isEvenPercentage(time);
		} else if (configuration.timeScaleName.equals(context
				.getString(R.string.ts_universe))) {
			return isEvenMillion(time);
		} else
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

}
