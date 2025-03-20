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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RemoteViews;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class MmConfigure extends Activity {
    public static final String USER_NAME_KEY = "user_name";
    public static final String BIRTHDAY_YEAR_KEY = "birthday_year";
    public static final String BIRTHDAY_MONTH_KEY = "birthday_month";
    public static final String BIRTHDAY_DAY_KEY = "birthday_day";
    public static final String LONGEVITY_KEY = "longevity";
    public static final String TIMESCALE_KEY = "timescale";
    public static final String FREQUENCY_KEY = "frequency";
    public static final String REVERSE_TIME_KEY = "reverse_time";
    public static final String USE_MSEC_KEY = "use_msec";
    public static final String LAST_APP_WIDGET_ID = "last_app_widget_id";
    public static final String SHOW_NOTIFICATIONS_KEY = "show_notifications";
    public static final String NOTIFICATION_SOUND_KEY = "notification_sound";
    public static final String CONFIGURATION_COMPLETE_KEY = "configuration_complete";
    public static final String DO_NOT_MODIFY_NAME_KEY = "do_not_modify_name";
    public static final String USE_EXACT_TIME_KEY = "use_exact_time";
    private static final String LOG_TAG = "MM";
    private static final String PREFS_NAME = "org.epstudios.morbidmeter.MmConfigure";
    private static boolean INHIBIT_DATE_CHANGE_LISTENER = false;
    private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private EditText userNameEditText;
    private DatePicker birthDayDatePicker;
    private DatePicker deathDayDatePicker;
    private TextView longevityTextView;
    private EditText longevityEditText;
    private Spinner timeScaleSpinner;
    private Spinner frequencySpinner;
    private CheckBox reverseTimeCheckBox;
    private CheckBox useMsecCheckBox;
    private CheckBox showNotificationsCheckBox;
    private RadioGroup notificationSoundRadioGroup;
    private CheckBox doNotModifyNameCheckBox;
    private CheckBox useExactTimeCheckBox;

    private Configuration configuration;

    static void savePrefs(Context context, int appWidgetId,
                          Configuration configuration) {
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
        prefs.putInt(TIMESCALE_KEY + appWidgetId,
                configuration.timeScaleNameId);
        prefs.putString(FREQUENCY_KEY + appWidgetId,
                configuration.updateFrequency);
        prefs.putBoolean(REVERSE_TIME_KEY + appWidgetId,
                configuration.reverseTime);
        prefs.putBoolean(USE_MSEC_KEY + appWidgetId, configuration.useMsec);
        prefs.putInt(LAST_APP_WIDGET_ID, appWidgetId);
        prefs.putBoolean(SHOW_NOTIFICATIONS_KEY + appWidgetId,
                configuration.showNotifications);
        prefs.putInt(NOTIFICATION_SOUND_KEY + appWidgetId,
                configuration.notificationSound);
        prefs.putBoolean(CONFIGURATION_COMPLETE_KEY + appWidgetId,
                configuration.configurationComplete);
        prefs.putBoolean(DO_NOT_MODIFY_NAME_KEY + appWidgetId,
                configuration.doNotModifyName);
        prefs.putBoolean(USE_EXACT_TIME_KEY + appWidgetId, configuration.useExactTime);
        prefs.apply();
    }

    static Configuration loadPrefs(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        Configuration configuration = new Configuration();
        String name = prefs.getString(USER_NAME_KEY + appWidgetId,
                context.getString(R.string.default_user_name));
        int year = prefs.getInt(BIRTHDAY_YEAR_KEY + appWidgetId, 1970);
        int month = prefs.getInt(BIRTHDAY_MONTH_KEY + appWidgetId, 1);
        int day = prefs.getInt(BIRTHDAY_DAY_KEY + appWidgetId, 1);
        GregorianCalendar birthDay = new GregorianCalendar();
        // make sure birthday is normalized to midnight
        birthDay.set(year, month, day, 0, 0, 0);
        //birthDay.setTimeZone(TimeZone.getTimeZone("UTC"));
        double longevity = prefs.getFloat(LONGEVITY_KEY + appWidgetId, 79.0f);
        configuration.user = new User(name, birthDay, longevity);
        configuration.timeScaleNameId = prefs.getInt(TIMESCALE_KEY
                + appWidgetId, R.string.ts_time);
        configuration.updateFrequency = prefs.getString(FREQUENCY_KEY
                + appWidgetId, context.getString(R.string.one_min));
        configuration.reverseTime = prefs.getBoolean(REVERSE_TIME_KEY
                + appWidgetId, false);
        configuration.useMsec = prefs.getBoolean(USE_MSEC_KEY + appWidgetId,
                false);
        configuration.showNotifications = prefs.getBoolean(
                SHOW_NOTIFICATIONS_KEY + appWidgetId, false);
        configuration.notificationSound = prefs.getInt(NOTIFICATION_SOUND_KEY
                + appWidgetId, R.id.no_sound);
        configuration.configurationComplete = prefs.getBoolean(
                CONFIGURATION_COMPLETE_KEY + appWidgetId, false);
        configuration.doNotModifyName = prefs.getBoolean(
                DO_NOT_MODIFY_NAME_KEY + appWidgetId, false);
        configuration.useExactTime = prefs.getBoolean(USE_EXACT_TIME_KEY + appWidgetId, false);
        return configuration;
    }

    public static void configureSkullButton(Context context, int appWidgetId,
                                            RemoteViews views) {
        Intent intent = new Intent(context, MmConfigure.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity(context, appWidgetId, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        } else {
            pendingIntent = PendingIntent.getActivity(context, appWidgetId, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        }
        views.setOnClickPendingIntent(R.id.update_button, pendingIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent launchIntent = getIntent();
        Bundle extras = launchIntent.getExtras();
        appWidgetId = Objects.requireNonNull(extras).getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        Intent cancelResultIntent = new Intent();
        cancelResultIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                appWidgetId);
        setResult(RESULT_CANCELED, cancelResultIntent); // in case user hits
        // back
        // button
        setContentView(R.layout.configure);

        // TODO: If exact alarm wanted, and if permission not granted, then ask for permission
        // by launching a permission request activity.
//        Intent intent = new Intent(this, PermissionRequestActivity.class);
//        startActivity(intent);

        userNameEditText = findViewById(R.id.user_name);
        birthDayDatePicker = findViewById(R.id.birthday);
        deathDayDatePicker = findViewById(R.id.deathday);
        longevityTextView = findViewById(R.id.longevityTextView);
        longevityEditText = findViewById(R.id.longevity);
        timeScaleSpinner = findViewById(R.id.timescale);
        frequencySpinner = findViewById(R.id.update_frequency);
        reverseTimeCheckBox = findViewById(R.id.reverse_time);
        useMsecCheckBox = findViewById(R.id.show_msec);
        showNotificationsCheckBox = findViewById(R.id.show_notifications);
        notificationSoundRadioGroup = findViewById(R.id.notification_sound_radio_group);
        doNotModifyNameCheckBox = findViewById(R.id.do_not_modify_name_checkbox);
        useExactTimeCheckBox = findViewById(R.id.use_exact_time);

        // setting the focus is kinda annoying
        // userNameEditText.requestFocus();
        this.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        setAdapters();

        final Context context = MmConfigure.this;
        // Base configuration on last configuration. If no
        // last widget ID, default configuration will load.
        //configuration = loadPrefs(context, loadLastAppWidgetId(context));
        configuration = loadPrefs(context, appWidgetId);

        userNameEditText.setText(configuration.user.getName());
        doNotModifyNameCheckBox.setChecked(configuration.doNotModifyName);
        int year = configuration.user.getBirthDay().get(Calendar.YEAR);
        int month = configuration.user.getBirthDay().get(Calendar.MONTH);
        int day = configuration.user.getBirthDay().get(Calendar.DAY_OF_MONTH);
        birthDayDatePicker
                .init(year, month, day, new MyOnDateChangedListener());
        Calendar maxDate = new GregorianCalendar();
        maxDate.set(2200, Calendar.JANUARY, 1, 0, 0);
        final long maxDateTimeInMillis = maxDate.getTimeInMillis();
        int deathYear = configuration.user.deathDay().get(Calendar.YEAR);
        int deathMonth = configuration.user.deathDay().get(Calendar.MONTH);
        int deathDay = configuration.user.deathDay().get(Calendar.DAY_OF_MONTH);
        deathDayDatePicker.init(deathYear, deathMonth, deathDay,
                new MyOnDateChangedListener());
        deathDayDatePicker.setMaxDate(maxDateTimeInMillis);
        longevityTextView.setText(getLongevityText(configuration.user
                .getLongevity()));
        longevityEditText.setText(formattedLongevity(configuration.user.getLongevity()));
        longevityEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                double longevity;
                try {
                    longevity = Double.parseDouble(longevityEditText
                            .getText().toString());
                } catch (final NumberFormatException e) {
                    longevity = 0.0;
                }
                longevityTextView.setText(getLongevityText(longevity));
                Calendar deathDay1 = User.getDeathDate(
                        birthDayDatePicker.getYear(),
                        birthDayDatePicker.getMonth(),
                        birthDayDatePicker.getDayOfMonth(), longevity);
                // disable the datePicker onDateChanged for this transaction
                INHIBIT_DATE_CHANGE_LISTENER = true;
                deathDayDatePicker.updateDate(deathDay1.get(Calendar.YEAR),
                        deathDay1.get(Calendar.MONTH),
                        deathDay1.get(Calendar.DAY_OF_MONTH));
                INHIBIT_DATE_CHANGE_LISTENER = false;
            }
        });

        // best way to do this is below, so suppress warning
        @SuppressWarnings("unchecked")
        ArrayAdapter<Integer> arrayAdapter = (ArrayAdapter<Integer>) timeScaleSpinner
                .getAdapter();
        int position = arrayAdapter.getPosition(configuration.timeScaleNameId);
        timeScaleSpinner.setSelection(position);
        @SuppressWarnings("unchecked")
        ArrayAdapter<String> frequencyArrayAdapter = (ArrayAdapter<String>) frequencySpinner
                .getAdapter();
        int frequencyPosition = frequencyArrayAdapter
                .getPosition(configuration.updateFrequency);
        frequencySpinner.setSelection(frequencyPosition);

        reverseTimeCheckBox.setChecked(configuration.reverseTime);
        useMsecCheckBox.setChecked(configuration.useMsec);
        showNotificationsCheckBox.setChecked(configuration.showNotifications);
        useExactTimeCheckBox.setChecked(configuration.useExactTime);

        setEnabledOptions(configuration.timeScaleNameId);
        showNotificationsCheckBox
                .setOnCheckedChangeListener((buttonView, isChecked) -> {
                    for (int i = 0; i < notificationSoundRadioGroup
                            .getChildCount(); ++i) {
                        notificationSoundRadioGroup
                                .getChildAt(i).setEnabled(isChecked);
                    }
                });
        notificationSoundRadioGroup.check(configuration.notificationSound);
        if (!showNotificationsCheckBox.isChecked()) {
            for (int i = 0; i < notificationSoundRadioGroup.getChildCount(); ++i) {
                notificationSoundRadioGroup.getChildAt(i)
                        .setEnabled(false);
            }
        }

        Button ok = findViewById(R.id.ok_button);
        ok.setOnClickListener(v -> {
            // get all variables from the various entry boxes
            configuration.user.setName(userNameEditText.getText()
                    .toString());
            configuration.doNotModifyName = doNotModifyNameCheckBox.isChecked();
            int year1 = birthDayDatePicker.getYear();
            int month1 = birthDayDatePicker.getMonth();
            int day1 = birthDayDatePicker.getDayOfMonth();
            configuration.user.getBirthDay().set(year1, month1, day1);
            if (longevityEditText.getText() != null
                    && longevityEditText.getText().length() > 0) {
                try {
                    configuration.user.setLongevity(Double
                            .parseDouble(longevityEditText.getText()
                                    .toString()));
                } catch (final NumberFormatException e) {
                    configuration.user.setLongevity(0.0);
                }
            } else {
                configuration.user.setLongevity(0.0);
            }

            //configuration.timeScaleNameId = (String) timeScaleSpinner
                    //.getSelectedItem();
            int[] timeScaleNameIds = TimeScaleType.getTimescaleNameIds();
            configuration.timeScaleNameId = timeScaleNameIds[timeScaleSpinner.getSelectedItemPosition()];
//            configuration.timeScaleNameId = (Integer) timeScaleSpinner
//                    .getSelectedItem();

            configuration.updateFrequency = (String) frequencySpinner
                    .getSelectedItem();

            configuration.reverseTime = reverseTimeCheckBox.isChecked();
            configuration.useMsec = useMsecCheckBox.isChecked();
            configuration.showNotifications = showNotificationsCheckBox
                    .isChecked();
            configuration.notificationSound = notificationSoundRadioGroup
                    .getCheckedRadioButtonId();
            configuration.useExactTime = useExactTimeCheckBox.isChecked();

            if (configuration.user.isSane()) {
                configuration.configurationComplete = true;
                savePrefs(context, appWidgetId, configuration);
                AppWidgetManager appWidgetManager = AppWidgetManager
                        .getInstance(context);
                ComponentName thisAppWidget = new ComponentName(context
                        .getPackageName(), MorbidMeterWidgetProvider.class.getName());
                Intent updateMmIntent = new Intent(context,
                        MorbidMeterWidgetProvider.class);
                int[] appWidgetIds = appWidgetManager
                        .getAppWidgetIds(thisAppWidget);
                updateMmIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
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
        });

        Button help = findViewById(R.id.help_button);
        help.setOnClickListener(v -> displayHelpMessage());

        Button cancel = findViewById(R.id.cancel_button);
        cancel.setOnClickListener(v -> finish());

    }

    private String getLongevityText(double longevity) {
        return getString(R.string.longevity_label) + " "
                + formattedLongevity(longevity) + " "
                + getString(R.string.longevity_label_completion);
    }

    private String formattedLongevity(double longevity) {
        DecimalFormat format = new DecimalFormat("###.0000");
        return format.format(longevity);
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
                this, R.array.timescalenames, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeScaleSpinner.setAdapter(adapter);
        // do nothing
        OnItemSelectedListener itemListener = new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v,
                                       int position, long id) {
                setEnabledOptions(TimeScaleType.getTimescaleNameIds()[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing
            }

        };

        timeScaleSpinner.setOnItemSelectedListener(itemListener);

        // In versions higher than 19, AlarmManager.setRepeating is equivalent to
        // setInexactRepeating and, apparently to save battery life,
        // repeat intervals < 1 minute are not allowed (they turn out to be
        // a minute instead).  So, these frequencies aren't offered for Android O
        // and above.
        int frequencyArrayInt;
        frequencyArrayInt = R.array.android_o_frequencies;
        ArrayAdapter<CharSequence> adapterFrequency = ArrayAdapter
                .createFromResource(this, frequencyArrayInt,
                        android.R.layout.simple_spinner_item);
        adapterFrequency
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        frequencySpinner.setAdapter(adapterFrequency);
        // do nothing
        OnItemSelectedListener frequencyItemListener = new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v,
                                       int position, long id) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing
            }

        };

        frequencySpinner.setOnItemSelectedListener(frequencyItemListener);

    }

    private void setEnabledOptions(int timeScaleNameId) {
        // note that all timescales have at least notifications for death.
        // but lite version has no notifications
        if (isLite()) {
            showNotificationsCheckBox.setEnabled(false);
            showNotificationsCheckBox.setChecked(false);
            useMsecCheckBox.setEnabled(false);
            useMsecCheckBox.setChecked(false);
            // we will cut Lite users some slack & let'm reverse time
            //reverseTimeCheckBox.setEnabled(false);
            //reverseTimeCheckBox.setChecked(false);
            return;
        }
        final Set<Integer> okMsecSet = new HashSet<>(Arrays.asList(
                R.string.ts_day,
                R.string.ts_hour,
                R.string.ts_month,
                R.string.ts_year));
        boolean okMsec = okMsecSet.contains(timeScaleNameId);
        useMsecCheckBox.setEnabled(okMsec);
        if (!okMsec) {
            useMsecCheckBox.setChecked(false);
        }
        final Set<Integer> reverseTimeNotOkSet = new HashSet<>(
                Arrays.asList(R.string.ts_time,
                        R.string.ts_time_military,
                        R.string.ts_time_military_no_seconds,
                        R.string.ts_time_no_seconds,
                        R.string.ts_none,
                        R.string.ts_debug));
        boolean noReverseTime = reverseTimeNotOkSet.contains(timeScaleNameId);
        reverseTimeCheckBox.setEnabled(!noReverseTime);
        if (noReverseTime) {
            reverseTimeCheckBox.setChecked(false);
        }
    }

    @SuppressLint("DefaultLocale")
    public boolean isLite() {
        return getPackageName().toLowerCase().contains("lite");
    }

    private class MyOnDateChangedListener implements OnDateChangedListener {
        @Override
        public void onDateChanged(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
            if (INHIBIT_DATE_CHANGE_LISTENER) {
                return;
            }
            double longevity = User.getLongevity(birthDayDatePicker.getYear(),
                    birthDayDatePicker.getMonth(),
                    birthDayDatePicker.getDayOfMonth(),
                    deathDayDatePicker.getYear(),
                    deathDayDatePicker.getMonth(),
                    deathDayDatePicker.getDayOfMonth());
            longevityEditText.setText(formattedLongevity(longevity));
            longevityTextView.setText(getLongevityText(longevity));
        }
    }

}
