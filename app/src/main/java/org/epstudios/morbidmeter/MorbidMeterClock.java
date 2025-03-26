/*  MorbidMeter - Lifetime in perspective
    Copyright (C) 2014 EP Studios, Inc.
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

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import java.text.DecimalFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MorbidMeterClock {
    private final Context context;
    private final SimpleTimeScale timeScale;

    private static final String LOG_TAG = "MM";
    private static final String PREFS_NAME = "org.epstudios.morbidmeter.MmConfigure";
    private static final String IN_MILESTONE = "in_milestone";
    private static Configuration configuration = null;
    private static int appWidgetId = 0;

    // TODO: Change from static class to instance class.
    public MorbidMeterClock(Context context, SimpleTimeScale timeScale) {
        this.context = context;
        this.timeScale = timeScale;
    }

    static void resetConfiguration(Context context, int appWidgetId) {
        configuration = MmConfigure.loadPrefs(context, appWidgetId);
        MorbidMeterClock.appWidgetId = appWidgetId;
        Log.d(LOG_TAG, "resetConfiguration, appWidgetId = " + appWidgetId);
    }

    int getFrequency() {
        return getFrequency(context);
    }

    static int getFrequency(Context context) {
        int frequencyId = configuration.updateFrequencyId;
        Frequency.FrequencyType frequencyType =
                Frequency.getFrequencyIdToFrequencyType(frequencyId);
        Frequency frequency = new Frequency(frequencyType);
        return frequency.getFrequency(context);
    }

    static boolean configurationIsComplete() {
        return configuration.configurationComplete;
    }

    String getLabel() {
        return getLabel(context);
    }

    static String getLabel(Context context) {
        String timeScaleName = "Timescale:\n";
        if (configuration.reverseTime)
            timeScaleName += "REVERSE ";
        timeScaleName += context.getString(configuration.timeScaleNameId);
        String userName = (configuration.doNotModifyName ? configuration.user.getName()
                : configuration.user.getApostrophedName() + " MorbidMeter");

        //String userName = configuration.user.getApostrophedName();
        return userName + "\n" + timeScaleName;
    }

    static Integer getTimeScaleNameId() {
        return configuration.timeScaleNameId;
    }


    static TimeScale getTimeScale() {
        TimeScaleType type = TimeScaleType.fromStringId(configuration.timeScaleNameId);
        if (type == null) {
            return null;
        }
        switch (type) {
            case LONG_TIME:
                return new LongTimeScale();
            case SHORT_TIME:
                return new ShortTimeScale();
            case LONG_MILITARY_TIME:
                return new LongMilitaryTimeScale();
            case SHORT_MILITARY_TIME:
                return new ShortMilitaryTimeScale();
            case NONE:
                return new NoTimeScale();
            case PERCENT:
                return new PercentTimeScale();
            case DAYS:
                return new DaysTimeScale();
            default:
                return null;
        }
    }

    static long getMsecAlive() {
        return configuration.user.msecAlive();
    }

    static long getMsecTotal() {
        return configuration.user.lifeDurationMsec();
    }

    String getFormattedTime() {
        return getFormattedTime(context);
    }

    static String getFormattedTime(Context context) {

        final boolean fullDebug = false;

        final String DECIMAL_FORMAT_STRING = "#.000000";
        final String SHORT_DECIMAL_FORMAT_STRING = "#,###.0000";
        final String SHORT_INT_FORMAT_STRING = "#,###";
        String formatString = "";
        String timeString;
        String units = "";
        Format formatter = new DecimalFormat(formatString);
        SimpleTimeScale ts = new SimpleTimeScale();
        if (configuration.user.percentAlive() >= 1.0) {
            if (configuration.showNotifications) {
                showNotification(context,
                        context.getString(R.string.user_dead_message));
            }
            return context.getString(R.string.user_dead_message);
        }
        if (configuration.timeScaleNameId == R.string.ts_none) {
            return "0";
        }
        if (configuration.timeScaleNameId == R.string.ts_percent) {
            ts = new SimpleTimeScale(configuration.timeScaleNameId, 0, 100);
            formatString = DECIMAL_FORMAT_STRING;
            formatter = new DecimalFormat(formatString);
            units = "%";
            if (configuration.reverseTime)
                units += " left";

        }
        if (configuration.timeScaleNameId == R.string.ts_long_time) {
            formatter = new SimpleDateFormat("EEEE, MMMM d yyyy\nhh:mm:ss a z",
                    Locale.getDefault());
            timeString = formatter.format(new Date());
            return timeString; // early exit
        }
        if (configuration.timeScaleNameId == R.string.ts_short_time) {
            formatter = new SimpleDateFormat("EEEE, MMMM d yyyy\nhh:mm a z",
                    Locale.getDefault());
            timeString = formatter.format(new Date());
            return timeString; // early exit
        }
        if (configuration.timeScaleNameId == R.string.ts_long_military_time) {
            formatter = new SimpleDateFormat("EEEE, MMMM d yyyy\nHH:mm:ss z",
                    Locale.getDefault());
            timeString = formatter.format(new Date());
            return timeString; // early exit
        }
        if (configuration.timeScaleNameId == R.string.ts_short_military_time) {
            formatter = new SimpleDateFormat("EEEE, MMMM d yyyy\nHH:mm z",
                    Locale.getDefault());
            timeString = formatter.format(new Date());
            return timeString; // early exit
        }
        if (configuration.timeScaleNameId == R.string.ts_debug) {
            long currentSystemTime = System.currentTimeMillis();
            timeString = "System Time " + currentSystemTime + " ms";
            timeString += "\nBirth " + configuration.user.birthDayMsec()
                    + " ms";
            timeString += "\nDeath " + configuration.user.deathDayMsec()
                    + " ms";
            timeString += "\n%Alive " + configuration.user.percentAlive() + "%";
            return timeString;
        }
        if (configuration.timeScaleNameId == R.string.ts_year) {
            ts = new CalendarTimeScale(configuration.timeScaleNameId,
                    new GregorianCalendar(2000, Calendar.JANUARY, 1, 0, 0, 0),
                    new GregorianCalendar(2001, Calendar.JANUARY, 1, 0, 0, 0));
            formatString = "MMMM d\nh:mm:ss a" + msecSuffix(configuration.useMsec);
            formatter = new SimpleDateFormat(formatString, Locale.getDefault());
        }
        if (configuration.timeScaleNameId == R.string.ts_day) {
            ts = new CalendarTimeScale(configuration.timeScaleNameId,
                    new GregorianCalendar(2000, Calendar.JANUARY, 1, 0, 0, 0),
                    new GregorianCalendar(2000, Calendar.JANUARY, 2, 0, 0, 0));
            formatString = "h:mm:ss a" + msecSuffix(configuration.useMsec);
            formatter = new SimpleDateFormat(formatString, Locale.getDefault());
        }
        if (configuration.timeScaleNameId == R.string.ts_hour) {
            ts = new CalendarTimeScale(configuration.timeScaleNameId,
                    new GregorianCalendar(2000, Calendar.JANUARY, 1, 11, 0, 0),
                    new GregorianCalendar(2000, Calendar.JANUARY, 1, 12, 0, 0));
            formatString = "hh:mm:ss" + msecSuffix(configuration.useMsec);
            formatter = new SimpleDateFormat(formatString, Locale.getDefault());
        }
        if (configuration.timeScaleNameId == R.string.ts_month) {
            ts = new CalendarTimeScale(configuration.timeScaleNameId,
                    new GregorianCalendar(2000, Calendar.JANUARY, 1, 0, 0, 0),
                    new GregorianCalendar(2000, Calendar.FEBRUARY, 1, 0, 0, 0));
            formatString = "MMMM d\nh:mm:ss a" + msecSuffix(configuration.useMsec);
            formatter = new SimpleDateFormat(formatString, Locale.getDefault());
        }
        if (configuration.timeScaleNameId == R.string.ts_universe) {
            ts = new SimpleTimeScale(configuration.timeScaleNameId, 0, 15000000000L);
            formatString = "##,###,###,###";
            formatter = new DecimalFormat(formatString);
            if (configuration.reverseTime)
                units = " yrs to Present";
            else
                units = " yrs from Big Bang";
        }
        if (configuration.timeScaleNameId == R.string.ts_x_universe_2) {
            ts = new SimpleTimeScale(configuration.timeScaleNameId, 0, 6000L);
            formatString = "##,###,###,###.0000";
            formatter = new DecimalFormat(formatString);
            if (configuration.reverseTime)
                units = " yrs to Armageddon";
            else
                units = " yrs from Creation";
        }
        if (configuration.timeScaleNameId == R.string.ts_x_universe) {
            ts = new CalendarTimeScale(configuration.timeScaleNameId,
                    new GregorianCalendar(-4000, Calendar.JANUARY, 1, 0, 0, 0),
                    new GregorianCalendar(2001, Calendar.JANUARY, 1, 0, 0, 0));
            formatString = "y G MMMM d\nh:mm:ss a";
            formatter = new SimpleDateFormat(formatString, Locale.getDefault());

        }

        // deal with raw time scales, i.e. real time
        if (configuration.timeScaleNameId == R.string.ts_raw) {
            formatString = "#,###";
            formatter = new DecimalFormat(formatString);

            if (configuration.reverseTime)
                timeString = formatter.format(configuration.user
                        .reverseMsecAlive()) + " msec remaining";
            else
                timeString = formatter.format(configuration.user.msecAlive())
                        + " msec alive";
        } else if (configuration.timeScaleNameId == R.string.ts_seconds) {
            formatString = "#,###";
            formatter = new DecimalFormat(formatString);

            if (configuration.reverseTime)
                timeString = formatter.format(configuration.user
                        .reverseSecAlive()) + " sec remaining";
            else
                timeString = formatter.format(configuration.user.secAlive())
                        + " sec alive";
        }
        // age in days or years does a different calculation
        else if (configuration.timeScaleNameId == R.string.ts_days) {
            long lifeInMsec = configuration.user.lifeDurationMsec();
            ts = new SimpleTimeScale(configuration.timeScaleNameId, 0, lifeInMsec);
            formatString = SHORT_DECIMAL_FORMAT_STRING;
            formatter = new DecimalFormat(formatString);

            if (configuration.reverseTime) {
                timeString = formatter.format(numDays(ts
                        .reverseProportionalTime(configuration.user
                                .percentAlive())));
                units = " days left";
            } else {
                timeString = formatter.format(numDays(ts
                        .proportionalTime(configuration.user.percentAlive())));
                units = " days old";
            }
        } else if (configuration.timeScaleNameId == R.string.ts_years) {
            long lifeInMsec = configuration.user.lifeDurationMsec();
            ts = new SimpleTimeScale(configuration.timeScaleNameId, 0, lifeInMsec);
            formatString = DECIMAL_FORMAT_STRING;
            formatter = new DecimalFormat(formatString);

            if (configuration.reverseTime) {
                timeString = formatter.format(numYears(ts
                        .reverseProportionalTime(configuration.user
                                .percentAlive())));
                units = " years left";
            } else {
                timeString = formatter.format(numYears(ts
                        .proportionalTime(configuration.user.percentAlive())));
                units = " years old";
            }
        } else if (configuration.timeScaleNameId == R.string.ts_weeks) {
            long lifeInMsec = configuration.user.lifeDurationMsec();
            ts = new SimpleTimeScale(configuration.timeScaleNameId, 0, lifeInMsec);
            formatString = DECIMAL_FORMAT_STRING;
            formatter = new DecimalFormat(formatString);

            if (configuration.reverseTime) {
                timeString = formatter.format(numWeeks(ts
                        .reverseProportionalTime(configuration.user
                                .percentAlive())));
                units = " weeks left";
            } else {
                timeString = formatter.format(numWeeks(ts
                        .proportionalTime(configuration.user.percentAlive())));
                units = " weeks old";
            }

        } else if (configuration.timeScaleNameId == R.string.ts_months) {
            long lifeInMsec = configuration.user.lifeDurationMsec();
            ts = new SimpleTimeScale(configuration.timeScaleNameId, 0, lifeInMsec);
            formatString = DECIMAL_FORMAT_STRING;
            formatter = new DecimalFormat(formatString);

            if (configuration.reverseTime) {
                timeString = formatter.format(numMonths(ts
                        .reverseProportionalTime(configuration.user
                                .percentAlive())));
                units = " months left";
            } else {
                timeString = formatter.format(numMonths(ts
                        .proportionalTime(configuration.user.percentAlive())));
                units = " months old";
            }

        } else if (configuration.timeScaleNameId == R.string.ts_hours) {
            long lifeInMsec = configuration.user.lifeDurationMsec();
            ts = new SimpleTimeScale(configuration.timeScaleNameId, 0, lifeInMsec);
            formatString = SHORT_DECIMAL_FORMAT_STRING;
            formatter = new DecimalFormat(formatString);

            if (configuration.reverseTime) {
                timeString = formatter.format(numHours(ts
                        .reverseProportionalTime(configuration.user
                                .percentAlive())));
                units = " hours left";
            } else {
                timeString = formatter.format(numHours(ts
                        .proportionalTime(configuration.user.percentAlive())));
                units = " hours old";
            }
        } else if (configuration.timeScaleNameId == R.string.ts_minutes) {
            long lifeInMsec = configuration.user.lifeDurationMsec();
            ts = new SimpleTimeScale(configuration.timeScaleNameId, 0, lifeInMsec);
            formatString = SHORT_DECIMAL_FORMAT_STRING;
            formatter = new DecimalFormat(formatString);

            if (configuration.reverseTime) {
                timeString = formatter.format(numMinutes(ts
                        .reverseProportionalTime(configuration.user
                                .percentAlive())));
                units = " mins left";
            } else {
                timeString = formatter.format(numMinutes(ts
                        .proportionalTime(configuration.user.percentAlive())));
                units = " mins old";
            }
        } else if (configuration.timeScaleNameId == R.string.ts_d_h_m_s) {
            long lifeInMsec = configuration.user.lifeDurationMsec();
            ts = new SimpleTimeScale(configuration.timeScaleNameId, 0, lifeInMsec);
            double proportionalTime;
            if (configuration.reverseTime) {
                proportionalTime = ts.reverseProportionalTime(configuration.user.percentAlive());
                units = " left";
            } else {
                proportionalTime = ts.proportionalTime(configuration.user.percentAlive());
                units = " done";
            }
            long secs = (long) proportionalTime / 1000;
            long mins = secs / 60;
            long hours = mins / 60;
            long days = hours / 24;
            formatString = SHORT_INT_FORMAT_STRING;
            formatter = new DecimalFormat(formatString);
            timeString = formatter.format(days) + "d " + hours % 24 + "h " +
                    mins % 60 + "m " + secs % 60 + "s";
        } else if (configuration.timeScaleNameId == R.string.ts_d_h_m) {
            long lifeInMsec = configuration.user.lifeDurationMsec();
            ts = new SimpleTimeScale(configuration.timeScaleNameId, 0, lifeInMsec);
            double proportionalTime;
            if (configuration.reverseTime) {
                proportionalTime = ts.reverseProportionalTime(configuration.user.percentAlive());
                units = " left";
            } else {
                proportionalTime = ts.proportionalTime(configuration.user.percentAlive());
                units = " done";
            }
            long secs = (long) proportionalTime / 1000;
            long mins = secs / 60;
            long hours = mins / 60;
            long days = hours / 24;
            formatString = SHORT_INT_FORMAT_STRING;
            formatter = new DecimalFormat(formatString);
            timeString = formatter.format(days) + "d " + hours % 24 + "h " +
                    mins % 60 + "m";
        } else {
            if (configuration.reverseTime) {
                timeString = formatter.format(ts
                        .reverseProportionalTime(configuration.user
                                .percentAlive()));
            } else {
                timeString = formatter.format(ts
                        .proportionalTime(configuration.user.percentAlive()));
            }
        }
        if (configuration.useMsec && ts.okToUseMsec())
            timeString += " msec";
        timeString += units;
        if (fullDebug) {
            long currentSystemTime = System.currentTimeMillis();
            timeString += "\nSystem Time " + currentSystemTime + " ms";
            timeString += "\nBirth " + configuration.user.birthDayMsec()
                    + " ms";
            timeString += "\nDeath " + configuration.user.deathDayMsec()
                    + " ms";
            timeString += "\n%Alive " + configuration.user.percentAlive() + "%";
            timeString += "\nPropTime " + ts.proportionalTime(configuration.user.percentAlive());
        }
        if (configuration.showNotifications) {
            showNotification(context, timeString);
        }

        return timeString;
    }

    private static String msecSuffix(Boolean useMsec) {
        // return (useMsec ? " S" : "");
        // the above should return 1-3 digits of msec and does except in
        // Android 5 emulator it rounds to 1 digit (hundreds).  Code below
        // always returns 3 digits with leading zeros as needed.
        // Bug report submitted to Google.
        // Update: Google going with new behavior, thus changed to below:
        return (useMsec ? " SSS" : "");
    }

    static int percentAlive() {
        return (int) (configuration.user.percentAlive() * 100);
    }

    static double rawPercentAlive() {
        return configuration.user.percentAlive();
    }

    static TimeScaleDirection getTimeScaleDirection() {
        return configuration.reverseTime ? TimeScaleDirection.REVERSE : TimeScaleDirection.FORWARD;
    }

    private static double numDays(double timeInMsecs) {
        return timeInMsecs / (24 * 60 * 60 * 1000.0);
    }

    private static double numWeeks(double timeInMsecs) {
        return numDays(timeInMsecs) / 7.0;
    }

    private static double numMonths(double timeInMsecs) {
        // Note that average length of month is value below
        return numDays(timeInMsecs) / 30.44;
    }

    private static double numYears(double timeInMsecs) {
        return numDays(timeInMsecs) / 365.25;
    }

    private static double numHours(double timeInMsecs) {
        return timeInMsecs / (60 * 60 * 1000);
    }

    private static double numMinutes(double timeInMsecs) {
        return timeInMsecs / (60 * 1000);
    }

    // We need to suppress deprecation here because we are using
    // deprecated methods for older versions of Android.
    @SuppressWarnings("deprecation")
    private static void showNotification(Context context, String time) {
        final String CHANNEL_ID = "morbidmeter_channel";
        final int NOTIFICATION_ID = 1;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "MorbidMeter Channel";
            String description = "Notifications for MorbidMeter";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    name, importance);
            channel.setDescription(description);
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            if (configuration.notificationSound == R.id.default_sound)
                channel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), audioAttributes);
            else if (configuration.notificationSound == R.id.mm_sound)
                channel.setSound(Uri
                        .parse("android.resource://org.epstudios.morbidmeter/raw/bellsnotification"), audioAttributes);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        Boolean userDead = time.equals(context
                .getString(R.string.user_dead_message));
        Boolean atMilestone = isMilestone(context, time);
        boolean inMilestone;
        if ((atMilestone || userDead)) {
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME,
                    0);
            inMilestone = prefs.getBoolean(IN_MILESTONE + appWidgetId, false);
            if (!inMilestone) {
                Intent notificationIntent = new Intent(context,
                        MorbidMeterWidgetProvider.class);
                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent notificationPendingIntent = PendingIntent
                        .getActivity(context, appWidgetId, notificationIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT );
                Notification.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    builder = new Notification.Builder(context, CHANNEL_ID)
                            .setSmallIcon(R.drawable.ic_stat_notification)
                            .setContentTitle("MorbidMeter")
                            .setAutoCancel(true)
                            .setTicker("MorbidMeter Milestone")
                            .setWhen(System.currentTimeMillis())
                            .setContentText(time)
                            .setContentIntent(notificationPendingIntent);
                } else {
                    builder = new Notification.Builder(context)
                            .setAutoCancel(true)
                            .setSmallIcon(R.drawable.ic_stat_notification)
                            .setTicker("MorbidMeter Milestone")
                            .setWhen(System.currentTimeMillis())
                            .setContentTitle("MorbidMeter")
                            .setPriority(Notification.PRIORITY_DEFAULT)
                            .setContentText(time)
                            .setContentIntent(notificationPendingIntent);
                    if (configuration.notificationSound == R.id.default_sound)
                        builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                    else if (configuration.notificationSound == R.id.mm_sound)
                        builder.setSound(Uri
                                .parse("android.resource://org.epstudios.morbidmeter/raw/bellsnotification"));
                }

                NotificationManager notificationManager = (NotificationManager) context
                        .getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(NOTIFICATION_ID, builder.build());
                inMilestone = true;
            }
        } else {
            inMilestone = false;
        }
        SharedPreferences.Editor prefsEditor = context.getSharedPreferences(
                PREFS_NAME, 0).edit();
        prefsEditor.putBoolean(IN_MILESTONE + appWidgetId, inMilestone);
        prefsEditor.apply();

    }

    private static Boolean isMilestone(Context context, String time) {
        if (configuration.timeScaleNameId == R.string.ts_year) {
            return isEvenHour(time);
        } else if (configuration.timeScaleNameId == R.string.ts_month
                || configuration.timeScaleNameId == R.string.ts_day) {
            return isEvenMinute(time);
        } else if (configuration.timeScaleNameId == R.string.ts_percent) {
            return isEvenPercentage(time);
        } else if (configuration.timeScaleNameId == R.string.ts_universe) {
            return isEvenMillion(time);
        } else
            return false;
    }

    public static Boolean isEvenHour(String time) {
        return time.contains(":00:");
    }

    public static Boolean isEvenMinute(String time) {
        return time.contains(":00 ");
    }

    public static Boolean isEvenPercentage(String time) {
        return time.contains(".000");
    }

    public static Boolean isEvenMillion(String time) {
        Pattern p = Pattern.compile(".*,000,... y.*", Pattern.DOTALL);
        Matcher m = p.matcher(time);
        return m.find();
    }

    // for testing, allows quicker notifications than usual
    public static Boolean isTestTime(String time) {
        Pattern p = Pattern.compile(".*[1369] [AP]M.*", Pattern.DOTALL);
        Matcher m = p.matcher(time);
        return m.find();
    }

}
