package org.epstudios.morbidmeter;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.widget.RemoteViews;

public class AlarmManagerBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		PowerManager pm = (PowerManager) context
				.getSystemService(Context.POWER_SERVICE);
		PowerManager.WakeLock wl = pm.newWakeLock(
				PowerManager.PARTIAL_WAKE_LOCK, "YOUR TAG");
		// Acquire the lock
		wl.acquire();

		// You can do the processing here update the widget/remote views.
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
				R.layout.main);
		Format formatter = new SimpleDateFormat("hh:mm:ss a");
		String time = formatter.format(new Date());

		remoteViews.setTextViewText(R.id.text, time);
		ComponentName thiswidget = new ComponentName(context, MorbidMeter.class);
		AppWidgetManager manager = AppWidgetManager.getInstance(context);
		manager.updateAppWidget(thiswidget, remoteViews);
		// Release the lock
		wl.release();
	}
}