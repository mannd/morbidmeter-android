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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

public class MorbidMeter extends AppWidgetProvider {
	private static final String LOG_TAG = "MM";
	public static final String MM_CLOCK_WIDGET_UPDATE = "org.epstudios.morbidmeter.MORBIDMETER_WIDGET_UPDATE";
	private static boolean notificationOngoing = false;
	// static boolean firstRun = true;

	private Configuration configuration;

	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
		Log.d(LOG_TAG, "MM Widget enabled.");
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		Log.d(LOG_TAG, "onReceive");
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		Log.d(LOG_TAG, "Updating MM Widgets.");

		for (int appWidgetId : appWidgetIds) {
			// due to bug in Android (?documentation) onUpdate actually is
			// called before configuration complete. We must suppress this
			// initial onUpdate or the alarm starts and can't be stopped if
			// widget creation is cancelled.
			MorbidMeterClock.resetConfiguration(context, appWidgetId);
			if (MorbidMeterClock.configurationIsComplete()) {
				setAlarm(context, appWidgetId,
						MorbidMeterClock.getFrequency(context));
				Log.d(LOG_TAG, "Alarm started");
				Intent intent = new Intent(context, MmConfigure.class);
				intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
						appWidgetId);
				PendingIntent pendingIntent = PendingIntent.getActivity(
						context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

				RemoteViews views = new RemoteViews(context.getPackageName(),
						R.layout.main);
				views.setOnClickPendingIntent(R.id.update_button, pendingIntent);
				appWidgetManager.updateAppWidget(appWidgetId, views);

			}
		}
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

	public static void setAlarm(Context context, int appWidgetId, int updateRate) {
		PendingIntent newPending = makeControlPendingIntent(context,
				MmService.UPDATE, appWidgetId);
		AlarmManager alarms = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		if (updateRate >= 0) {
			alarms.setRepeating(AlarmManager.ELAPSED_REALTIME,
					SystemClock.elapsedRealtime(), updateRate, newPending);
		} else {
			// on a negative updateRate stop the refreshing
			alarms.cancel(newPending);
		}
	}

	public static PendingIntent makeControlPendingIntent(Context context,
			String command, int appWidgetId) {
		Intent active = new Intent(context, MmService.class);
		active.setAction(command);
		active.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		// this Uri data is to make the PendingIntent unique, so it wont be
		// updated by FLAG_UPDATE_CURRENT
		// so if there are multiple widget instances they wont override each
		// other
		Uri data = Uri.withAppendedPath(
				Uri.parse("mmwidget://widget/id/#" + command + appWidgetId),
				String.valueOf(appWidgetId));
		active.setData(data);
		return (PendingIntent.getService(context, 0, active,
				PendingIntent.FLAG_UPDATE_CURRENT));
	}

	@Override
	public void onDisabled(Context context) {
		Log.d(LOG_TAG, "MM Widget disabled.");
		context.stopService(new Intent(context, MmService.class));
		super.onDisabled(context);
	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		Log.d(LOG_TAG, "MM Widget deleted.");
		for (int appWidgetId : appWidgetIds) {
			MorbidMeterClock.resetConfiguration(context, appWidgetId);
			if (MorbidMeterClock.configurationIsComplete()) {
				setAlarm(context, appWidgetId, -1);
			}
		}
		super.onDeleted(context, appWidgetIds);
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
