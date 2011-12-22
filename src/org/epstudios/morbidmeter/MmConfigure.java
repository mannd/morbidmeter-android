package org.epstudios.morbidmeter;

import java.util.Calendar;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

public class MmConfigure extends Activity {
	private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
	private static final String PREFS_NAME = "org.epstudios.morbidmeter.MmConfigure";
	private static final String LONGEVITY_KEY = "longevity";

	private EditText userNameEditText;
	private DatePicker birthdayDatePicker;
	private EditText longevityEditText;
	private Spinner timeScaleSpinner;
	private OnItemSelectedListener itemListener;
	private CheckBox reverseTimeCheckBox;
	private CheckBox useMsecCheckBox;

	private Configuration configuration;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setResult(RESULT_CANCELED); // in case user hits back button
		setContentView(R.layout.configure);

		userNameEditText = (EditText) findViewById(R.id.user_name);
		birthdayDatePicker = (DatePicker) findViewById(R.id.birthday);
		longevityEditText = (EditText) findViewById(R.id.longevity);
		timeScaleSpinner = (Spinner) findViewById(R.id.timescale);
		reverseTimeCheckBox = (CheckBox) findViewById(R.id.reverse_time);
		useMsecCheckBox = (CheckBox) findViewById(R.id.show_msec);

		Intent launchIntent = getIntent();
		Bundle extras = launchIntent.getExtras();
		if (extras != null)
			appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
					AppWidgetManager.INVALID_APPWIDGET_ID);
		if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID)
			finish();

		final Context context = MmConfigure.this;
		userNameEditText.setText(getString(R.string.default_user_name));
		longevityEditText.setText(loadPrefs(context, appWidgetId));

		setAdapters();

		Button ok = (Button) findViewById(R.id.ok_button);
		ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				savePrefs(context, appWidgetId, longevityEditText.getText()
						.toString());
				AppWidgetManager appWidgetManager = AppWidgetManager
						.getInstance(context);
				configuration.user = getUser();
				if (configuration.user.isSane()) {

					// MorbidMeter.updateAppWidget(context, appWidgetManager,
					// appWidgetId, user, timescale, options);
					Intent resultValue = new Intent();
					resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
							appWidgetId);
					setResult(RESULT_OK, resultValue);
					finish();
				} else {
					// dialog saying user not sane
					finish();
				}
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

	private void setAdapters() {
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.timescales, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		timeScaleSpinner.setAdapter(adapter);
		itemListener = new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View v,
					int position, long id) {
				;
			}

			public void onNothingSelected(AdapterView<?> parent) {
				// do nothing
			}

		};

		timeScaleSpinner.setOnItemSelectedListener(itemListener);

	}

	static void savePrefs(Context context, int appWidgetId, String longevity) {
		// testing with just longevity first
		SharedPreferences.Editor prefs = context.getSharedPreferences(
				PREFS_NAME, 0).edit();
		prefs.putString(LONGEVITY_KEY, longevity);
		prefs.commit();
	}

	static String loadPrefs(Context context, int appWidgetId) {
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
		String longevity = prefs.getString(LONGEVITY_KEY, null);
		if (longevity != null)
			return longevity;
		else
			return "";
	}

	private User getUser() {
		String name = userNameEditText.getText().toString();
		int year = birthdayDatePicker.getYear();
		int month = birthdayDatePicker.getMonth();
		int day = birthdayDatePicker.getDayOfMonth();
		Calendar birthday = Calendar.getInstance();
		birthday.set(year, month, day);
		// try { this might throw number exception
		double longevity = Double.parseDouble(longevityEditText.getText()
				.toString());
		// } catch ... etc.
		return new User(name, birthday, longevity);
	}

}
