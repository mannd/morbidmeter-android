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
package org.epstudios.morbidmeter

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.DatePicker
import android.widget.DatePicker.OnDateChangedListener
import android.widget.EditText
import android.widget.RemoteViews
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.edit
import org.epstudios.morbidmeter.Frequency.Companion.frequencyNameIds
import org.epstudios.morbidmeter.timescale.TimeScaleType
import org.epstudios.morbidmeter.timescale.TimeScaleType.Companion.timescaleNameIds
import java.text.DecimalFormat
import java.util.Calendar
import java.util.GregorianCalendar
import java.util.Objects
import kotlin.jvm.java

/**
 * The Activity that is used to configure each widget.
 */
class MmConfigure : AppCompatActivity(), ExactAlarmCallback {
    // Unique widget ID determined by OS.
    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    // Components of the configuration layout.
    private var userNameEditText: EditText? = null
    private var birthDayDatePicker: DatePicker? = null
    private var deathDayDatePicker: DatePicker? = null
    private var longevityTextView: TextView? = null
    private var longevityEditText: EditText? = null
    private var timeScaleSpinner: Spinner? = null
    private var frequencySpinner: Spinner? = null
    private var reverseTimeCheckBox: CheckBox? = null
    private var showNotificationsCheckBox: CheckBox? = null
    //private var notificationSoundRadioGroup: RadioGroup? = null
    private var doNotModifyNameCheckBox: CheckBox? = null
    private var useExactTimeCheckBox: CheckBox? = null

    // Configuration unique to each widget.
    private var configuration: MmConfiguration? = null

    // Notification related variables.
    private lateinit var notification: MmNotification
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(LOG_TAG, "onCreate")

        notification = MmNotification(this)

        // Register for notification permission
        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        )  {
            isGranted: Boolean ->
            if (isGranted) {
                Log.d(LOG_TAG, "POST_NOTIFICATION Permission granted")
            } else {
                Log.d(LOG_TAG, "POST_NOTIFICATION Permission denied")
                showNotificationsCheckBox?.isChecked = false
            }
        }

        val launchIntent = intent
        val extras = launchIntent.extras
        appWidgetId = Objects.requireNonNull<Bundle?>(extras).getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        )
        val cancelResultIntent = Intent()
        cancelResultIntent.putExtra(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            appWidgetId
        )
        setResult(RESULT_CANCELED, cancelResultIntent) // in case user hits
        // back
        // button
        setContentView(R.layout.configure)

        userNameEditText = findViewById<EditText>(R.id.user_name)
        birthDayDatePicker = findViewById<DatePicker>(R.id.birthday)
        deathDayDatePicker = findViewById<DatePicker>(R.id.deathday)
        longevityTextView = findViewById<TextView>(R.id.longevityTextView)
        longevityEditText = findViewById<EditText>(R.id.longevity)
        timeScaleSpinner = findViewById<Spinner>(R.id.timescale)
        frequencySpinner = findViewById<Spinner>(R.id.update_frequency)
        reverseTimeCheckBox = findViewById<CheckBox>(R.id.reverse_time)
        //notificationSoundRadioGroup = findViewById<RadioGroup>(R.id.notification_sound_radio_group)
        doNotModifyNameCheckBox = findViewById<CheckBox>(R.id.do_not_modify_name_checkbox)
        useExactTimeCheckBox = findViewById<CheckBox>(R.id.use_exact_time)

        showNotificationsCheckBox = findViewById<CheckBox>(R.id.show_notifications)

        useExactTimeCheckBox?.setOnCheckedChangeListener { _,
                                               isChecked ->
            onExactAlarmPermissionRequested(isChecked)
        }

        // setting the focus is kinda annoying
        // userNameEditText.requestFocus();
        this.window.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        )

        setAdapters()

        val context: Context = this@MmConfigure
        // Base configuration on last configuration. If no
        // last widget ID, default configuration will load.
        //configuration = loadPrefs(context, loadLastAppWidgetId(context));
        configuration = loadPrefs(context, appWidgetId)

        userNameEditText!!.setText(configuration!!.user.name)
        doNotModifyNameCheckBox!!.setChecked(configuration!!.doNotModifyName)
        val year = configuration!!.user.birthDay.get(Calendar.YEAR)
        val month = configuration!!.user.birthDay.get(Calendar.MONTH)
        val day = configuration!!.user.birthDay.get(Calendar.DAY_OF_MONTH)
        birthDayDatePicker!!
            .init(year, month, day, MyOnDateChangedListener())
        val maxDate: Calendar = GregorianCalendar()
        maxDate.set(2200, Calendar.JANUARY, 1, 0, 0)
        val maxDateTimeInMillis = maxDate.getTimeInMillis()
        val deathYear = configuration!!.user.deathDay().get(Calendar.YEAR)
        val deathMonth = configuration!!.user.deathDay().get(Calendar.MONTH)
        val deathDay = configuration!!.user.deathDay().get(Calendar.DAY_OF_MONTH)
        deathDayDatePicker!!.init(
            deathYear, deathMonth, deathDay,
            MyOnDateChangedListener()
        )
        deathDayDatePicker!!.maxDate = maxDateTimeInMillis
        longevityTextView!!.text = getLongevityText(
            configuration!!.user
                .longevity
        )
        longevityEditText!!.setText(formattedLongevity(configuration!!.user.longevity))
        longevityEditText!!.onFocusChangeListener = OnFocusChangeListener { _: View?, hasFocus: Boolean ->
            if (!hasFocus) {
                var longevity: Double = try {
                    longevityEditText!!
                        .getText().toString().toDouble()
                } catch (_: NumberFormatException) {
                    0.0
                }
                longevityTextView!!.text = getLongevityText(longevity)
                val deathDay1 = User.getDeathDate(
                    birthDayDatePicker!!.year,
                    birthDayDatePicker!!.month,
                    birthDayDatePicker!!.dayOfMonth, longevity
                )
                // disable the datePicker onDateChanged for this transaction
                INHIBIT_DATE_CHANGE_LISTENER = true
                deathDayDatePicker!!.updateDate(
                    deathDay1.get(Calendar.YEAR),
                    deathDay1.get(Calendar.MONTH),
                    deathDay1.get(Calendar.DAY_OF_MONTH)
                )
                INHIBIT_DATE_CHANGE_LISTENER = false
            }
        }

        // best way to do this is below, so suppress warning
//        val arrayAdapter = timeScaleSpinner!!
//            .getAdapter() as ArrayAdapter<Int?>?
        val position = TimeScaleType.indexFromString(configuration!!.timeScaleNameId)
        timeScaleSpinner!!.setSelection(position)
//        val frequencyArrayAdapter = frequencySpinner!!
//            .getAdapter() as ArrayAdapter<String?>?
        val frequencyPosition = Frequency.indexFromString(configuration!!.updateFrequencyId)
        frequencySpinner!!.setSelection(frequencyPosition)

        reverseTimeCheckBox!!.setChecked(configuration!!.reverseTime)
        showNotificationsCheckBox!!.setChecked(configuration!!.showNotifications)
        useExactTimeCheckBox!!.setChecked(configuration!!.useExactTime)

        setEnabledOptions(configuration!!.timeScaleNameId)

        showNotificationsCheckBox!!
            .setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
                if (isChecked) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        if (ActivityCompat.checkSelfPermission(
                                this,
                                Manifest.permission.POST_NOTIFICATIONS
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            // Permission not granted.  Show permission request.
                            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    }
                }
            })
//        notificationSoundRadioGroup!!.check(configuration!!.notificationSound)
//        if (!showNotificationsCheckBox!!.isChecked) {
//            for (i in 0..<notificationSoundRadioGroup!!.childCount) {
//                notificationSoundRadioGroup!!.getChildAt(i)
//                    .setEnabled(false)
//            }
//        }

        val ok = findViewById<Button>(R.id.ok_button)
        ok.setOnClickListener(View.OnClickListener { _: View? ->
            // get all variables from the various entry boxes
            configuration!!.user.name = userNameEditText!!.getText()
                .toString()
            configuration!!.doNotModifyName = doNotModifyNameCheckBox!!.isChecked
            val year1 = birthDayDatePicker!!.year
            val month1 = birthDayDatePicker!!.month
            val day1 = birthDayDatePicker!!.dayOfMonth
            configuration!!.user.birthDay.set(year1, month1, day1)
            if (longevityEditText!!.getText() != null
                && longevityEditText!!.getText().isNotEmpty()
            ) {
                try {
                    configuration!!.user.longevity = longevityEditText!!.getText()
                        .toString().toDouble()
                } catch (_: NumberFormatException) {
                    configuration!!.user.longevity = 0.0
                }
            } else {
                configuration!!.user.longevity = 0.0
            }

            val timeScaleNameIds = timescaleNameIds
            configuration!!.timeScaleNameId = timeScaleNameIds[timeScaleSpinner!!
                .selectedItemPosition]
            val frequencyIds = frequencyNameIds
            configuration!!.updateFrequencyId =
                frequencyIds[frequencySpinner!!.selectedItemPosition]
            configuration!!.reverseTime = reverseTimeCheckBox!!.isChecked
            configuration!!.showNotifications = showNotificationsCheckBox!!
                .isChecked
//            configuration!!.notificationSound = notificationSoundRadioGroup!!
//                .checkedRadioButtonId
            configuration!!.useExactTime = useExactTimeCheckBox!!.isChecked
            if (configuration!!.user.isSane()) {
                configuration!!.configurationComplete = true
                savePrefs(context, appWidgetId, configuration!!)
                val appWidgetManager = AppWidgetManager
                    .getInstance(context)
                val thisAppWidget = ComponentName(
                    context
                        .packageName, MorbidMeterWidgetProvider::class.java.getName()
                )
                val updateMmIntent = Intent(
                    context,
                    MorbidMeterWidgetProvider::class.java
                )
                val appWidgetIds = appWidgetManager
                    .getAppWidgetIds(thisAppWidget)
                updateMmIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
                updateMmIntent.putExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds
                )
                context.sendBroadcast(updateMmIntent)
                Log.d(LOG_TAG, "onUpdate broadcast sent")

                // Clear milestones
                val notification = MmNotification(context)
                notification.clearLastMilestone(appWidgetId)

                val resultValue = Intent()
                resultValue.putExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    appWidgetId
                )
                setResult(RESULT_OK, resultValue)
                finish()
            } else {
                // dialog saying user not sane
                val alert = AlertDialog.Builder(context)
                    .create()
                val message = getString(R.string.sanity_message)
                alert.setMessage(message)
                alert.setTitle(getString(R.string.sanity_title))
                alert.show()
            }
        })

        val help = findViewById<Button>(R.id.help_button)
        help.setOnClickListener(View.OnClickListener { _: View? -> displayHelpMessage() })

        val cancel = findViewById<Button>(R.id.cancel_button)
        cancel.setOnClickListener(View.OnClickListener { _: View? -> finish() })
    }

    override fun onExactAlarmPermissionRequested(useExactAlarm: Boolean) {
        if (useExactAlarm) {
            val intent = Intent(this, ExactAlarmPermissionRequestActivity::class.java)
            startActivity(intent)
        } else {
            cancelAlarm()
        }
    }

    private fun cancelAlarm() {
        // TODO
    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmAllowed = getSystemService(AlarmManager::class.java)
                .canScheduleExactAlarms()
            if (alarmAllowed) {
                useExactTimeCheckBox!!.setChecked(true)
            } else {
                useExactTimeCheckBox!!.setChecked(false)
            }
        }
    }

    private fun getLongevityText(longevity: Double): String {
        return (getString(R.string.longevity_label) + " "
                + formattedLongevity(longevity) + " "
                + getString(R.string.longevity_label_completion))
    }

    private fun formattedLongevity(longevity: Double): String {
        val format = DecimalFormat("###.0000")
        return format.format(longevity)
    }

    private fun displayHelpMessage() {
        val dialog = AlertDialog.Builder(this).create()
        val message = getString(R.string.help_message)
        dialog.setMessage(message)
        dialog.setTitle(getString(R.string.help_title))
        dialog.show()
    }

    private fun setAdapters() {
        val timescaleAdapter = ArrayAdapter<CharSequence?>(
            this,
            android.R.layout.simple_spinner_item,
            TimeScaleType.getStringArray(this)
        )
        //        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
//                this, R.array.timescalenames, android.R.layout.simple_spinner_item);
        timescaleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        timeScaleSpinner!!.setAdapter(timescaleAdapter)
        // do nothing
        val itemListener: OnItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, v: View?,
                position: Int, id: Long
            ) {
                setEnabledOptions(timescaleNameIds[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // do nothing
            }
        }

        timeScaleSpinner!!.onItemSelectedListener = itemListener

        val adapterFrequency = ArrayAdapter<CharSequence?>(
            this, android.R.layout.simple_spinner_item,
            Frequency.getStringArray(this)
        )
        adapterFrequency
            .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        frequencySpinner!!.setAdapter(adapterFrequency)
        // do nothing
        val frequencyItemListener: OnItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, v: View?,
                position: Int, id: Long
            ) {
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // do nothing
            }
        }

        frequencySpinner!!.onItemSelectedListener = frequencyItemListener
    }

    private fun setEnabledOptions(timeScaleNameId: Int) {
        // note that all timescales have at least notifications for death.
        // but lite version has no notifications
        if (this.isLite) {
            showNotificationsCheckBox!!.setEnabled(false)
            showNotificationsCheckBox!!.setChecked(false)
            // we will cut Lite users some slack & let'm reverse time
            //reverseTimeCheckBox.setEnabled(false);
            //reverseTimeCheckBox.setChecked(false);
            return
        }
        val reverseTimeNotOkSet: MutableSet<Int?> = HashSet<Int?>(
            listOf<Int?>(
                R.string.ts_long_time,
                R.string.ts_long_military_time,
                R.string.ts_short_military_time,
                R.string.ts_short_time,
                R.string.ts_none,
                R.string.ts_debug
            )
        )
        val noReverseTime = reverseTimeNotOkSet.contains(timeScaleNameId)
        reverseTimeCheckBox!!.setEnabled(!noReverseTime)
        if (noReverseTime) {
            reverseTimeCheckBox!!.setChecked(false)
        }
    }

    @get:SuppressLint("DefaultLocale")
    val isLite: Boolean
        get() = false // lite version no longer supported
    //return getPackageName().toLowerCase().contains("lite");

    private inner class MyOnDateChangedListener : OnDateChangedListener {
        override fun onDateChanged(
            view: DatePicker?, year: Int, monthOfYear: Int,
            dayOfMonth: Int
        ) {
            if (INHIBIT_DATE_CHANGE_LISTENER) {
                return
            }
            val longevity = User.getLongevity(
                birthDayDatePicker!!.year,
                birthDayDatePicker!!.month,
                birthDayDatePicker!!.dayOfMonth,
                deathDayDatePicker!!.year,
                deathDayDatePicker!!.month,
                deathDayDatePicker!!.dayOfMonth
            )
            longevityEditText!!.setText(formattedLongevity(longevity))
            longevityTextView!!.text = getLongevityText(longevity)
        }
    }

    companion object {
        const val USER_NAME_KEY: String = "user_name"
        const val BIRTHDAY_YEAR_KEY: String = "birthday_year"
        const val BIRTHDAY_MONTH_KEY: String = "birthday_month"
        const val BIRTHDAY_DAY_KEY: String = "birthday_day"
        const val LONGEVITY_KEY: String = "longevity"
        const val TIMESCALE_KEY: String = "timescale"
        const val FREQUENCY_ID_KEY: String = "frequency_id"
        const val REVERSE_TIME_KEY: String = "reverse_time"
        const val USE_MSEC_KEY: String = "use_msec"
        const val LAST_APP_WIDGET_ID: String = "last_app_widget_id"
        const val SHOW_NOTIFICATIONS_KEY: String = "show_notifications"
        //const val NOTIFICATION_SOUND_KEY: String = "notification_sound"
        const val CONFIGURATION_COMPLETE_KEY: String = "configuration_complete"
        const val DO_NOT_MODIFY_NAME_KEY: String = "do_not_modify_name"
        const val USE_EXACT_TIME_KEY: String = "use_exact_time"
        private const val LOG_TAG = "MmConfigure"
        private const val PREFS_NAME = "org.epstudios.morbidmeter.MmConfigure"
        private var INHIBIT_DATE_CHANGE_LISTENER = false
        private fun savePrefs(
            context: Context, appWidgetId: Int,
            configuration: MmConfiguration
        ) {
            Log.d(LOG_TAG, "Saving configuration for widget $appWidgetId")
            context.getSharedPreferences(
                PREFS_NAME, 0
            ).edit {
                putString(
                    USER_NAME_KEY + appWidgetId,
                    configuration.user.name
                )
                putInt(
                    BIRTHDAY_YEAR_KEY + appWidgetId, configuration.user
                        .birthDay.get(Calendar.YEAR)
                )
                putInt(
                    BIRTHDAY_MONTH_KEY + appWidgetId, configuration.user
                        .birthDay.get(Calendar.MONTH)
                )
                putInt(
                    BIRTHDAY_DAY_KEY + appWidgetId, configuration.user
                        .birthDay.get(Calendar.DAY_OF_MONTH)
                )
                putFloat(
                    LONGEVITY_KEY + appWidgetId,
                    configuration.user.longevity.toFloat()
                )
                putInt(
                    TIMESCALE_KEY + appWidgetId,
                    configuration.timeScaleNameId
                )
                putInt(
                    FREQUENCY_ID_KEY + appWidgetId,
                    configuration.updateFrequencyId
                )
                putBoolean(
                    REVERSE_TIME_KEY + appWidgetId,
                    configuration.reverseTime
                )
                putInt(LAST_APP_WIDGET_ID, appWidgetId)
                putBoolean(
                    SHOW_NOTIFICATIONS_KEY + appWidgetId,
                    configuration.showNotifications
                )
                putBoolean(
                    CONFIGURATION_COMPLETE_KEY + appWidgetId,
                    configuration.configurationComplete
                )
                putBoolean(
                    DO_NOT_MODIFY_NAME_KEY + appWidgetId,
                    configuration.doNotModifyName
                )
                putBoolean(USE_EXACT_TIME_KEY + appWidgetId, configuration.useExactTime)
            }
        }

        @JvmStatic
        internal fun loadPrefs(context: Context, appWidgetId: Int): MmConfiguration {
            Log.d(LOG_TAG, "Loading configuration for widget $appWidgetId")
            val prefs = context.getSharedPreferences(PREFS_NAME, 0)
            val configuration = MmConfiguration()
            val name: String = prefs.getString(
                USER_NAME_KEY + appWidgetId,
                context.getString(R.string.default_user_name)
            )!!
            val year = prefs.getInt(BIRTHDAY_YEAR_KEY + appWidgetId, 1970)
            val month = prefs.getInt(BIRTHDAY_MONTH_KEY + appWidgetId, 1)
            val day = prefs.getInt(BIRTHDAY_DAY_KEY + appWidgetId, 1)
            val birthDay = GregorianCalendar()
            // make sure birthday is normalized to midnight
            birthDay.set(year, month, day, 0, 0, 0)
            //birthDay.setTimeZone(TimeZone.getTimeZone("UTC"));
            val longevity = prefs.getFloat(LONGEVITY_KEY + appWidgetId, 79.0f).toDouble()
            configuration.user = User(name, birthDay, longevity)
            configuration.timeScaleNameId = prefs.getInt(
                TIMESCALE_KEY
                        + appWidgetId, R.string.ts_long_time
            )
            configuration.updateFrequencyId = prefs.getInt(
                FREQUENCY_ID_KEY
                        + appWidgetId, R.string.f_one_min
            )
            configuration.reverseTime = prefs.getBoolean(
                REVERSE_TIME_KEY
                        + appWidgetId, false
            )
            configuration.showNotifications = prefs.getBoolean(
                SHOW_NOTIFICATIONS_KEY + appWidgetId, false
            )
            configuration.configurationComplete = prefs.getBoolean(
                CONFIGURATION_COMPLETE_KEY + appWidgetId, false
            )
            configuration.doNotModifyName = prefs.getBoolean(
                DO_NOT_MODIFY_NAME_KEY + appWidgetId, false
            )
            configuration.useExactTime = prefs.getBoolean(USE_EXACT_TIME_KEY + appWidgetId, false)
            return configuration
        }

        fun deletePrefs(context: Context) {
            Log.d(LOG_TAG, "Deleting configuration")
            context.getSharedPreferences(PREFS_NAME, 0).edit {
                clear()
            }
        }

        /**
         * Configure the skull button.
         * Pressing the skull button will call the configure activity with
         * the current widget ID.  This is necessary because each widget
         * has a different configuration.
         * @param context
         * @param appWidgetId
         * @param views
         */
        fun configureSkullButton(
            context: Context?, appWidgetId: Int,
            views: RemoteViews
        ) {
            val intent = Intent(context, MmConfigure::class.java)
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.getActivity(
                    context, appWidgetId, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            } else {
                PendingIntent.getActivity(
                    context, appWidgetId, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            }
            views.setOnClickPendingIntent(R.id.update_button, pendingIntent)
        }
    }
}
