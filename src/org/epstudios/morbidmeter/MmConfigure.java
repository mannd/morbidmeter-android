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
	public static final String USER_NAME_KEY = "user_name";
	public static final String BIRTHDAY_YEAR_KEY = "birthday_year";
	public static final String BIRTHDAY_MONTH_KEY = "birthday_month";
	public static final String BIRTHDAY_DAY_KEY = "birthday_day";
	public static final String LONGEVITY_KEY = "longevity";
	public static final String TIMESCALE_KEY = "timescale";
	public static final String REVERSE_TIME_KEY = "reverse_time";
	public static final String USE_MSEC_KEY = "use_msec";

	private EditText userNameEditText;
	private DatePicker birthDayDatePicker;
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
		birthDayDatePicker = (DatePicker) findViewById(R.id.birthday);
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

		setAdapters();

		final Context context = MmConfigure.this;
		configuration = loadPrefs(context, appWidgetId);
		userNameEditText.setText(configuration.user.getName());
		int year = configuration.user.getBirthDay().get(Calendar.YEAR);
		int month = configuration.user.getBirthDay().get(Calendar.MONTH);
		int day = configuration.user.getBirthDay().get(Calendar.DAY_OF_MONTH);
		birthDayDatePicker.updateDate(year, month, day);
		longevityEditText.setText(Double.toString(configuration.user
				.getLongevity()));
		@SuppressWarnings("unchecked")
		// best way to do this is below, so suppress warning
		ArrayAdapter<String> arrayAdapter = (ArrayAdapter<String>) timeScaleSpinner
				.getAdapter();
		int position = arrayAdapter.getPosition(configuration.timeScaleName);
		timeScaleSpinner.setSelection(position);
		reverseTimeCheckBox.setChecked(configuration.reverseTime);
		useMsecCheckBox.setChecked(configuration.useMsec);

		Button ok = (Button) findViewById(R.id.ok_button);
		ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// get all variables from the various entry boxes
				configuration.user.setName(userNameEditText.getText()
						.toString());
				int year = birthDayDatePicker.getYear();
				int month = birthDayDatePicker.getMonth();
				int day = birthDayDatePicker.getDayOfMonth();
				configuration.user.getBirthDay().set(year, month, day);
				configuration.user.setLongevity(Double
						.parseDouble(longevityEditText.getText().toString()));
				configuration.timeScaleName = (String) timeScaleSpinner
						.getSelectedItem();
				configuration.reverseTime = reverseTimeCheckBox.isChecked();
				configuration.useMsec = useMsecCheckBox.isChecked();
				savePrefs(context, appWidgetId, configuration);
				AppWidgetManager appWidgetManager = AppWidgetManager
						.getInstance(context);
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

	static void savePrefs(Context context, int appWidgetId,
			Configuration configuration) {
		// testing with just longevity first
		SharedPreferences.Editor prefs = context.getSharedPreferences(
				PREFS_NAME, 0).edit();
		prefs.putString(USER_NAME_KEY, configuration.user.getName());
		prefs.putInt(BIRTHDAY_YEAR_KEY,
				configuration.user.getBirthDay().get(Calendar.YEAR));
		prefs.putInt(BIRTHDAY_MONTH_KEY,
				configuration.user.getBirthDay().get(Calendar.MONTH));
		prefs.putInt(BIRTHDAY_DAY_KEY,
				configuration.user.getBirthDay().get(Calendar.DAY_OF_MONTH));
		prefs.putFloat(LONGEVITY_KEY, (float) configuration.user.getLongevity());
		prefs.putString(TIMESCALE_KEY, configuration.timeScaleName);
		prefs.putBoolean(REVERSE_TIME_KEY, configuration.reverseTime);
		prefs.putBoolean(USE_MSEC_KEY, configuration.useMsec);
		prefs.commit();
	}

	static Configuration loadPrefs(Context context, int appWidgetId) {
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
		Configuration configuration = new Configuration();
		String name = prefs.getString(USER_NAME_KEY, "");
		int year = prefs.getInt(BIRTHDAY_YEAR_KEY, 0);
		int month = prefs.getInt(BIRTHDAY_MONTH_KEY, 0);
		int day = prefs.getInt(BIRTHDAY_DAY_KEY, 0);
		Calendar birthDay = Calendar.getInstance();
		birthDay.set(year, month, day);
		double longevity = (double) prefs.getFloat(LONGEVITY_KEY, 0);
		configuration.user = new User(name, birthDay, longevity);
		configuration.timeScaleName = prefs.getString(TIMESCALE_KEY, "YEAR");
		configuration.reverseTime = prefs.getBoolean(REVERSE_TIME_KEY, false);
		configuration.useMsec = prefs.getBoolean(USE_MSEC_KEY, false);
		return configuration;
	}
}
