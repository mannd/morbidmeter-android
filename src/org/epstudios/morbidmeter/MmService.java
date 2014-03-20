package org.epstudios.morbidmeter;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

public class MmService extends Service {
	public static final String UPDATE = "update";
	private static final String LOG_TAG = "MM";

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		String command = intent.getAction();
		Context context = getApplicationContext();
		int appWidgetId = intent.getExtras().getInt(
				AppWidgetManager.EXTRA_APPWIDGET_ID);
		RemoteViews views = new RemoteViews(context.getPackageName(),
				R.layout.main);
		AppWidgetManager appWidgetManager = AppWidgetManager
				.getInstance(context);
		MorbidMeterClock.resetConfiguration(context, appWidgetId);
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
		appWidgetManager.updateAppWidget(appWidgetId, views);
		return START_STICKY;

	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
