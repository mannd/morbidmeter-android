package org.epstudios.morbidmeter;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

public class MorbidMeter extends AppWidgetProvider {
	private SimpleDateFormat formatter = new SimpleDateFormat(
			"EEEEEEEEE\nd MMM KK:mm a");
	
	@Override
	public void onUpdate(Context context,
			AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		String now = formatter.format(new Date());
		
		RemoteViews updateViews = new RemoteViews( 
				context.getPackageName(), R.layout.main);
		updateViews.setTextViewText(R.id.text, now);
		appWidgetManager.updateAppWidget(appWidgetIds, updateViews);
		
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}
	
}
