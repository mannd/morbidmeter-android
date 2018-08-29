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

package org.epstudios.morbidmeter.lib;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

public class MorbidMeter extends AppWidgetProvider {
	private static final String LOG_TAG = "MM";

	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
		Log.d(LOG_TAG, "MM Widget enabled.");
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		Log.d(LOG_TAG, "MorbidMeter onReceive");
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		Log.d(LOG_TAG, "Updating MM Widgets.");
		for (int appWidgetId : appWidgetIds) {
			MorbidMeterClock.resetConfiguration(context, appWidgetId);
			if (MorbidMeterClock.configurationIsComplete()) {
				setAlarm(context, appWidgetId,
						MorbidMeterClock.getFrequency(context));
				Log.d(LOG_TAG, "Alarm started");
				RemoteViews views = new RemoteViews(context.getPackageName(),
						R.layout.main);
				// we'll update everything here to avoid a delay if clock
				// frequency is long
				MmService.updateButtonAndWidget(context, appWidgetId, views);
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
//			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//				alarms.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, updateRate, newPending);
//				alarms.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), updateRate, newPending);
//			} else {
				alarms.setRepeating(AlarmManager.ELAPSED_REALTIME,
						SystemClock.elapsedRealtime(), updateRate, newPending);
//			}
		} else {
			// on a negative updateRate stop the refreshing
			alarms.cancel(newPending);
			Log.d(LOG_TAG, "Alarm stopped.");
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
			setAlarm(context, appWidgetId, -1);
		}
		super.onDeleted(context, appWidgetIds);
	}

}
