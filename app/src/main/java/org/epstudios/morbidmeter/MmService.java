package org.epstudios.morbidmeter;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class MmService extends Service {
    public static final String UPDATE = "update";
    private static final String LOG_TAG = "MM";

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
        String label = MorbidMeterClock.getLabel(context);
        views.setTextViewText(R.id.text, label);
        Log.d(LOG_TAG, "Label updated.");
    }

    public static void updateButtonAndWidget(Context context, int appWidgetId,
                                             RemoteViews views) {
        MmConfigure.configureSkullButton(context, appWidgetId, views);
        updateWidget(context, views);
    }

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
        int appWidgetId = Objects.requireNonNull(intent.getExtras()).getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID);
        MorbidMeterClock.resetConfiguration(context, appWidgetId);
        RemoteViews views = new RemoteViews(context.getPackageName(),
                R.layout.widget);
        updateButtonAndWidget(context, appWidgetId, views);
        AppWidgetManager appWidgetManager = AppWidgetManager
                .getInstance(context);
        appWidgetManager.updateAppWidget(appWidgetId, views);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        Log.i(LOG_TAG, "MmService onCreate");
        super.onCreate();
        if (Build.VERSION_CODES.O <= Build.VERSION.SDK_INT) {
            startInForeground();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // With Oreo, background services are limited.
    // Service must run in Foreground, meaning there has to be a notification.
    // See https://stackoverflow.com/questions/47611123/oreo-starting-a-service-in-the-foreground
    private void startInForeground() {
        Log.i(LOG_TAG, "startInForeground");
        String CHANNEL_ID = "MMChannel";
        String CHANNEL_NAME = "MorbidMeter";
        String CHANNEL_DESCRIPTION = "Notifications for MorbidMeter";
        int TASK_ID = 1333;
        Intent notificationIntent = new Intent(this, MmService.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("MorbidMeter")
                .setContentText("MorbidMeter")
                .setTicker("TICKER")
                .setContentIntent(pendingIntent);
        Notification notification = builder.build();
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_DESCRIPTION);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
        startForeground(TASK_ID, notification);
    }
}

