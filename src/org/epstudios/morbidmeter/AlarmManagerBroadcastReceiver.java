package org.epstudios.morbidmeter;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.widget.RemoteViews;
import android.widget.Toast;

public class AlarmManagerBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		PowerManager pm = (PowerManager) context
				.getSystemService(Context.POWER_SERVICE);
		PowerManager.WakeLock wl = pm.newWakeLock(
				PowerManager.PARTIAL_WAKE_LOCK, "MM_TAG");
		// Acquire the lock
		wl.acquire();

		Bundle extras = intent.getExtras();
		String testString = extras.getString("Test");
		Toast.makeText(context, testString, Toast.LENGTH_SHORT).show();

		// You can do the processing here update the widget/remote views.
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
				R.layout.main);
		String time = MorbidMeterClock.getFormattedTime();

		remoteViews.setTextViewText(R.id.time, time);

		ComponentName thiswidget = new ComponentName(context, MorbidMeter.class);
		AppWidgetManager manager = AppWidgetManager.getInstance(context);
		manager.updateAppWidget(thiswidget, remoteViews);
		// Release the lock
		wl.release();
	}
}