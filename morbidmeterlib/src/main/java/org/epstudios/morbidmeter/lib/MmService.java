package org.epstudios.morbidmeter.lib;

import android.app.IntentService;
import android.app.PendingIntent;
import android.app.Service;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.JobIntentService;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

public class MmService extends Service {
	public static final String UPDATE = "update";
	private static final String LOG_TAG = "MM";


	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
        // For some reason a null intent is sometimes passed,
        // this check eliminated java exception and message
        // the morbidmeter has stopped.
        // Not sure if START_REDELIVER_INTENT is necessary
		Log.i(LOG_TAG, "onStartCommand called.");
		if (intent == null) {
            Log.d(LOG_TAG, "null intent passed to onStartCommand");
            return START_REDELIVER_INTENT;
        }
		Context context = getApplicationContext();
		int appWidgetId = intent.getExtras().getInt(
				AppWidgetManager.EXTRA_APPWIDGET_ID);
		MorbidMeterClock.resetConfiguration(context, appWidgetId);
		RemoteViews views = new RemoteViews(context.getPackageName(),
				R.layout.main);
		updateButtonAndWidget(context, appWidgetId, views);
		AppWidgetManager appWidgetManager = AppWidgetManager
				.getInstance(context);
		appWidgetManager.updateAppWidget(appWidgetId, views);
		return START_STICKY;
	}

	public static void updateWidget(Context context, RemoteViews views) {
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
		String label = MorbidMeterClock.getLabel();
		if (label != null) {
			views.setTextViewText(R.id.text, label);
			Log.d(LOG_TAG, "Label updated.");
		}
	}

	public static void updateButtonAndWidget(Context context, int appWidgetId,
											 RemoteViews views) {
		MmConfigure.configureSkullButton(context, appWidgetId, views);
		updateWidget(context, views);
	}

	@Override
	public void onCreate() {
		Log.i(LOG_TAG, "MmService onCreate");
		super.onCreate();
	}

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}

