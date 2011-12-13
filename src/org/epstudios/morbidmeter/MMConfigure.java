package org.epstudios.morbidmeter;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MMConfigure extends Activity {
	private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
	static final String PREFS_NAME = "org.epstudios.morbidmeter.MMConfigure";
	private EditText longevityEditText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setResult(RESULT_CANCELED); // in case user hits back button
		setContentView(R.layout.configure);

		Intent launchIntent = getIntent();
		Bundle extras = launchIntent.getExtras();
		if (extras != null)
			appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
					AppWidgetManager.INVALID_APPWIDGET_ID);
		if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID)
			finish();

		longevityEditText = (EditText) findViewById(R.id.longevity);
		final Context context = MMConfigure.this;

		Button ok = (Button) findViewById(R.id.ok_button);
		ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// stuff
				savePrefs(context, appWidgetId);
				AppWidgetManager appWidgetManager = AppWidgetManager
						.getInstance(context);
				// MorbidMeter.updateAppWidget(context, appWidgetManager,
				// appWidgetId, "");
				Intent resultValue = new Intent();
				resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
						appWidgetId);
				setResult(RESULT_OK, resultValue);
				finish();
			}
		});

		Button cancel = (Button) findViewById(R.id.cancel_button);
		cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent resultValue = new Intent();
				resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
						appWidgetId);
				setResult(RESULT_CANCELED, resultValue);
				finish();
			}
		});
		loadPrefs(context, appWidgetId);
	}

	private void savePrefs(Context context, int appWidgetId) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		prefs.edit().putString("longevity",
				longevityEditText.getText().toString());
	}

	private void loadPrefs(Context context, int appWidgetId) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		String longevity = prefs.getString("longevity", null);
		if (longevity != null)
			longevityEditText.setText(longevity);
		else
			longevityEditText.setText("");
	}

}
