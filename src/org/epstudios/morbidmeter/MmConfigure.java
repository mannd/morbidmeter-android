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

import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;

public class MmConfigure extends Activity {
	private static final String LOG_TAG = "MM";

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
	public static final String LAST_APP_WIDGET_ID = "last_app_widget_id";
	public static final String SHOW_NOTIFICATIONS_KEY = "show_notifications";
	public static final String NOTIFICATION_SOUND_KEY = "notification_sound";

	private EditText userNameEditText;
	private DatePicker birthDayDatePicker;
	private EditText longevityEditText;
	private Spinner timeScaleSpinner;
	private OnItemSelectedListener itemListener;
	private CheckBox reverseTimeCheckBox;
	private CheckBox useMsecCheckBox;
	private CheckBox showNotificationsCheckBox;
	private RadioGroup notificationSoundRadioGroup;

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
		showNotificationsCheckBox = (CheckBox) findViewById(R.id.show_notifications);
		notificationSoundRadioGroup = (RadioGroup) findViewById(R.id.notification_sound_radio_group);

		// setting the focus is kinda annoying
		// userNameEditText.requestFocus();
		this.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		Intent launchIntent = this.getIntent();
		Bundle extras = launchIntent.getExtras();
		if (extras != null)
			appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
					AppWidgetManager.INVALID_APPWIDGET_ID);
		if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID)
			finish();

		setAdapters();

		final Context context = MmConfigure.this;

		configuration = loadPrefs(context, loadLastAppWidgetId(context));

		userNameEditText.setText(configuration.user.getName());
		int year = configuration.user.getBirthDay().get(Calendar.YEAR);
		int month = configuration.user.getBirthDay().get(Calendar.MONTH);
		int day = configuration.user.getBirthDay().get(Calendar.DAY_OF_MONTH);
		birthDayDatePicker.updateDate(year, month, day);
		longevityEditText.setText(Double.toString(configuration.user
				.getLongevity()));
		// best way to do this is below, so suppress warning
		@SuppressWarnings("unchecked")
		ArrayAdapter<String> arrayAdapter = (ArrayAdapter<String>) timeScaleSpinner
				.getAdapter();
		int position = arrayAdapter.getPosition(configuration.timeScaleName);
		timeScaleSpinner.setSelection(position);
		// timeScaleSpinner.set
		reverseTimeCheckBox.setChecked(configuration.reverseTime);
		useMsecCheckBox.setChecked(configuration.useMsec);
		showNotificationsCheckBox.setChecked(configuration.showNotifications);
		notificationSoundRadioGroup.check(configuration.notificationSound);

		setEnabledOptions(configuration.timeScaleName);

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
				configuration.showNotifications = showNotificationsCheckBox
						.isChecked();
				configuration.notificationSound = notificationSoundRadioGroup
						.getCheckedRadioButtonId();

				if (configuration.user.isSane()) {
					savePrefs(context, appWidgetId, configuration);
					AppWidgetManager appWidgetManager = AppWidgetManager
							.getInstance(context);
					ComponentName thisAppWidget = new ComponentName(context
							.getPackageName(), MorbidMeter.class.getName());
					Intent updateMmIntent = new Intent(context,
							MorbidMeter.class);
					int[] appWidgetIds = appWidgetManager
							.getAppWidgetIds(thisAppWidget);
					updateMmIntent
							.setAction("android.appwidget.action.APPWIDGET_UPDATE");
					updateMmIntent.putExtra(
							AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
					context.sendBroadcast(updateMmIntent);
					Log.d(LOG_TAG, "onUpdate broadcast sent");

					Intent resultValue = new Intent();
					resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
							appWidgetId);
					setResult(RESULT_OK, resultValue);
					finish();
				} else {
					// dialog saying user not sane
					AlertDialog alert = new AlertDialog.Builder(context)
							.create();
					String message = getString(R.string.sanity_message);
					alert.setMessage(message);
					alert.setTitle(getString(R.string.sanity_title));
					alert.show();
				}
			}
		});

		Button help = (Button) findViewById(R.id.help_button);
		help.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				displayHelpMessage();
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
	}

	private void displayHelpMessage() {
		AlertDialog dialog = new AlertDialog.Builder(this).create();
		String message = getString(R.string.help_message);
		dialog.setMessage(message);
		dialog.setTitle(getString(R.string.help_title));
		dialog.show();
	}

	private void setAdapters() {
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.timescales, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		timeScaleSpinner.setAdapter(adapter);
		itemListener = new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View v,
					int position, long id) {
				setEnabledOptions((String) timeScaleSpinner.getSelectedItem());

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// do nothing
			}

		};

		timeScaleSpinner.setOnItemSelectedListener(itemListener);

	}

	private void setEnabledOptions(String timeScaleName) {
		// change to hasMsec and implement
		// Also need same for notifications, reverse time sets
		// / TODO
		final Set<String> okMsecSet = new HashSet<String>(Arrays.asList(
				this.getString(R.string.ts_day),
				this.getString(R.string.ts_hour),
				this.getString(R.string.ts_month),
				this.getString(R.string.ts_year)));
		boolean okMsec = okMsecSet.contains(timeScaleName);
		useMsecCheckBox.setEnabled(okMsec);
		if (!okMsec) {
			useMsecCheckBox.setChecked(false);
		}
	}

	static void savePrefs(Context context, int appWidgetId,
			Configuration configuration) {
		// testing with just longevity first
		SharedPreferences.Editor prefs = context.getSharedPreferences(
				PREFS_NAME, 0).edit();
		prefs.putString(USER_NAME_KEY + appWidgetId,
				configuration.user.getName());
		prefs.putInt(BIRTHDAY_YEAR_KEY + appWidgetId, configuration.user
				.getBirthDay().get(Calendar.YEAR));
		prefs.putInt(BIRTHDAY_MONTH_KEY + appWidgetId, configuration.user
				.getBirthDay().get(Calendar.MONTH));
		prefs.putInt(BIRTHDAY_DAY_KEY + appWidgetId, configuration.user
				.getBirthDay().get(Calendar.DAY_OF_MONTH));
		prefs.putFloat(LONGEVITY_KEY + appWidgetId,
				(float) configuration.user.getLongevity());
		prefs.putString(TIMESCALE_KEY + appWidgetId,
				configuration.timeScaleName);
		prefs.putBoolean(REVERSE_TIME_KEY + appWidgetId,
				configuration.reverseTime);
		prefs.putBoolean(USE_MSEC_KEY + appWidgetId, configuration.useMsec);
		prefs.putInt(LAST_APP_WIDGET_ID, appWidgetId);
		prefs.putBoolean(SHOW_NOTIFICATIONS_KEY + appWidgetId,
				configuration.showNotifications);
		prefs.putInt(NOTIFICATION_SOUND_KEY + appWidgetId,
				configuration.notificationSound);
		prefs.commit();
	}

	static Configuration loadPrefs(Context context, int appWidgetId) {
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
		Configuration configuration = new Configuration();
		String name = prefs.getString(USER_NAME_KEY + appWidgetId,
				context.getString(R.string.default_user_name));
		int year = prefs.getInt(BIRTHDAY_YEAR_KEY + appWidgetId, 1970);
		int month = prefs.getInt(BIRTHDAY_MONTH_KEY + appWidgetId, 1);
		int day = prefs.getInt(BIRTHDAY_DAY_KEY + appWidgetId, 1);
		Calendar birthDay = new GregorianCalendar();
		birthDay.set(year, month, day);
		double longevity = prefs.getFloat(LONGEVITY_KEY + appWidgetId, 79.0f);
		configuration.user = new User(name, birthDay, longevity);
		configuration.timeScaleName = prefs.getString(TIMESCALE_KEY
				+ appWidgetId, context.getString(R.string.ts_time));
		configuration.reverseTime = prefs.getBoolean(REVERSE_TIME_KEY
				+ appWidgetId, false);
		configuration.useMsec = prefs.getBoolean(USE_MSEC_KEY + appWidgetId,
				false);
		configuration.showNotifications = prefs.getBoolean(
				SHOW_NOTIFICATIONS_KEY + appWidgetId, false);
		configuration.notificationSound = prefs.getInt(NOTIFICATION_SOUND_KEY
				+ appWidgetId, R.id.no_sound);
		return configuration;
	}

	static int loadLastAppWidgetId(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
		return prefs.getInt(LAST_APP_WIDGET_ID, 0);
	}
}
