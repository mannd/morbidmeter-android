package org.epstudios.morbidmeter.lib;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

public class MmService extends JobIntentService {
	public static final String UPDATE = "update";
	private static final String LOG_TAG = "MM";
	/* Give the Job a Unique Id */
	private static final int JOB_ID = 5000;

	public static void enqueueWork(Context ctx, Intent intent) {
		enqueueWork(ctx, MmService.class, JOB_ID, intent);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// String command = intent.getAction();
        // For some reason a null intent is sometimes passed,
        // this check eliminated java exception and message
        // the morbidmeter has stopped.
        // Not sure if START_REDELIVER_INTENT is necessary
		if (intent == null) {
            Log.d(LOG_TAG, "null intent passed to onStartCommand");
            return START_REDELIVER_INTENT;
        }
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
		Intent configureIntent = new Intent(context, MmConfigure.class);
		configureIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
				appWidgetId);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, appWidgetId,
				configureIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		views.setOnClickPendingIntent(R.id.update_button, pendingIntent);
		// necessary to update label by service,
		// otherwise label will disappear with rotation
		String label = MorbidMeterClock.getLabel();
		if (label != null) {
			views.setTextViewText(R.id.text, label);
			Log.d(LOG_TAG, "Label updated.");
		}

		appWidgetManager.updateAppWidget(appWidgetId, views);
		return START_STICKY;

	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
		Log.i(LOG_TAG, "Handling work!");
    }

}
