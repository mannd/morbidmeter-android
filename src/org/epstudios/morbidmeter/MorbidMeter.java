/*  MorbidMeter - Lifetime in perspective 
    Copyright (C) 2011, 2014 EP Studios, Inc.
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

public class MorbidMeter extends AppWidgetProvider {
	private static final String LOG_TAG = "MM";
	private static final DateFormat df = new SimpleDateFormat("hh:mm:ss");
	public static final String MM_CLOCK_WIDGET_UPDATE = "org.epstudios.morbidmeter.MORBIDMETER_WIDGET_UPDATE";
	private static boolean notificationOngoing = false;
	static boolean firstRun = true;

	private Configuration configuration;

	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
		Log.d(LOG_TAG, "MM Widget enabled.  Starting timer.");

		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.add(Calendar.SECOND, 1);
		// using RTC instead of RTC_WAKEUP prevents waking of system
		am.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(),
				1000 * 60 * 15, createClockTickIntent(context)); // MorbidMeterClock.getLastConfiguration(context);
		int[] allIds = AppWidgetManager.getInstance(context).getAppWidgetIds(
				new ComponentName(context, MorbidMeter.class));
		onUpdate(context, AppWidgetManager.getInstance(context), allIds);

	}

	private PendingIntent createClockTickIntent(Context context) {
		Intent intent = new Intent(MM_CLOCK_WIDGET_UPDATE);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);

		return pendingIntent;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);

		if (MM_CLOCK_WIDGET_UPDATE.equals(intent.getAction())) {
			Log.d(LOG_TAG, "Clock update");
			ComponentName thisAppWidget = new ComponentName(
					context.getPackageName(), getClass().getName());
			AppWidgetManager appWidgetManager = AppWidgetManager
					.getInstance(context);
			int ids[] = appWidgetManager.getAppWidgetIds(thisAppWidget);
			for (int appWidgetID : ids) {
				updateAppWidget(context, appWidgetManager, appWidgetID);
			}
		}
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {

		Log.d(LOG_TAG, "Updating MM Widgets.");

		for (int i = 0; i < appWidgetIds.length; ++i) {
			int appWidgetId = appWidgetIds[i];

			Intent intent = new Intent(context, MmConfigure.class);
			intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
			// flag is needed or extra is null in configuration activity
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
					intent, PendingIntent.FLAG_UPDATE_CURRENT);

			RemoteViews views = new RemoteViews(context.getPackageName(),
					R.layout.main);
			views.setOnClickPendingIntent(R.id.update_button, pendingIntent);
			MorbidMeterClock.resetConfiguration(context, appWidgetId);
			// Label only needs to be changed onUpdate and onEnabled
			// (which calls onUpdate).
			String label = MorbidMeterClock.getLabel();
			if (label != null) {
				views.setTextViewText(R.id.text, label);
			}
			updateViews(context, views);
			appWidgetManager.updateAppWidget(appWidgetId, views);
		}
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

	@Override
	public void onDisabled(Context context) {
		super.onDisabled(context);
		Log.d(LOG_TAG, "MM Widget disabled.  Turning off timer.");
		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(createClockTickIntent(context));
	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		super.onDeleted(context, appWidgetIds);
		Log.d(LOG_TAG, "MM Widget deleted.");
	}

	public static void updateAppWidget(Context context,
			AppWidgetManager appWidgetManager, int appWidgetId) {

		MorbidMeterClock.loadConfiguration(context, appWidgetId);

		RemoteViews views = new RemoteViews(context.getPackageName(),
				R.layout.main);
		updateViews(context, views);
		appWidgetManager.updateAppWidget(appWidgetId, views);
	}

	private static void updateViews(Context context, RemoteViews views) {
		String currentTime = MorbidMeterClock.getFormattedTime(context);
		if (currentTime != null) {
			Log.d(LOG_TAG, "Current time = " + currentTime);
			if (currentTime.equals("0")) {
				views.setViewVisibility(R.id.time, View.GONE);
			} else {
				views.setViewVisibility(R.id.time, View.VISIBLE);
				views.setTextViewText(R.id.time, currentTime);
			}
		}
		views.setProgressBar(R.id.progressBar, 100,
				MorbidMeterClock.percentAlive(), false);
	}

	// static void updateAppWidget(Context context,
	// AppWidgetManager appWidgetManager, int appWidgetId,
	// Configuration configuration) {
	// Intent intent = new Intent(context, MorbidMeter.class);
	// intent.setAction(ACTION_WIDGET_REFRESH);
	// PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
	// intent, 0);
	//
	// RemoteViews updateViews = new RemoteViews(context.getPackageName(),
	// R.layout.main);
	// updateViews.setOnClickPendingIntent(R.id.update_button, pendingIntent);
	// String time = "Test";
	// // String time = getTime(context, configuration);
	// String timeScaleName = "Timescale: ";
	// if (configuration.reverseTime)
	// timeScaleName += "REVERSE ";
	// timeScaleName += configuration.timeScaleName + "\n";
	// String userName = configuration.user.getName();
	// if (userName.length() > 0) {
	// if (userName.toUpperCase(Locale.getDefault()).charAt(
	// userName.length() - 1) == 'S')
	// userName += "'";
	// else
	// userName += "'s";
	// }
	// String label = userName + " MorbidMeter\n" + timeScaleName;
	// if (configuration.user.isDead())
	// label += context.getString(R.string.user_dead_message);
	// else
	// label += time;
	// updateViews.setTextViewText(R.id.text, label);
	// Boolean isMilestone = isMilestone(context, configuration, time);
	// // being dead is a milestone too!
	// isMilestone = isMilestone || configuration.user.isDead();
	// // if below true will ignore milestones and send notification with each
	// // update
	// if (notificationOngoing)
	// if (!isMilestone)
	// notificationOngoing = false;
	// Log.d("DEBUG", "notificationOngoing = " + notificationOngoing);
	// Boolean debugNotifications = false;
	// if (debugNotifications
	// || (configuration.showNotifications && isMilestone &&
	// !notificationOngoing)) {
	// NotificationManager notificationManager = (NotificationManager) context
	// .getSystemService(Context.NOTIFICATION_SERVICE);
	// Notification notification = new Notification(
	// R.drawable.notificationskull, "MorbidMeter Milestone",
	// System.currentTimeMillis());
	// notification.flags |= Notification.FLAG_AUTO_CANCEL;
	// Intent notificationIntent = new Intent(context, MorbidMeter.class);
	// PendingIntent notyPendingIntent = PendingIntent.getActivity(
	// context, 0, notificationIntent, 0);
	// notification.setLatestEventInfo(context, "MorbidMeter", time,
	// notyPendingIntent);
	// if (configuration.notificationSound == R.id.default_sound)
	// notification.defaults |= Notification.DEFAULT_SOUND;
	// else if (configuration.notificationSound == R.id.mm_sound)
	// notification.sound = Uri
	// .parse("android.resource://org.epstudios.morbidmeter/raw/bellsnotification");
	// notificationManager.notify(1, notification);
	// notificationOngoing = true;
	// }
	// appWidgetManager.updateAppWidget(appWidgetId, updateViews);
	// }
	//
	// // is public for testing
	// public static Boolean isMilestone(Context context,
	// Configuration configuration, String time) {
	// if (configuration.timeScaleName.equals(context
	// .getString(R.string.ts_year)))
	// // return isTestTime(time); // for testing
	// // return isEvenMinute(time); // for testing
	// return isEvenHour(time);
	// else if (configuration.timeScaleName.equals(context
	// .getString(R.string.ts_month))
	// || configuration.timeScaleName.equals(context
	// .getString(R.string.ts_day)))
	// return isEvenMinute(time);
	// else if (configuration.timeScaleName.equals(context
	// .getString(R.string.ts_age))
	// || configuration.timeScaleName.equals(context
	// .getString(R.string.ts_percent)))
	// return isEvenPercentage(time);
	// else if (configuration.timeScaleName.equals(context
	// .getString(R.string.ts_universe)))
	// return isEvenMillion(time);
	// else
	// return false;
	// }

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
