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

import java.text.SimpleDateFormat;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

public class MorbidMeter extends AppWidgetProvider {
	private SimpleDateFormat formatter = new SimpleDateFormat("MMM d\nKK:mm a");

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
		updateViews.setTextViewText(R.id.text, time);
		//
		appWidgetManager.updateAppWidget(appWidgetId, updateViews);
	}

	public static String getTime(Context context, Configuration configuration) {
		if (configuration.timeScaleName.equals(context
				.getString(R.string.ts_percent))) {
			TimeScale ts = new TimeScale(configuration.timeScaleName, 0, 100);
			return Double.toString(ts.proportionalTime(configuration.user
					.percentAlive()));
		}

		return "test";
	}

}
