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
import java.util.GregorianCalendar;
import java.util.Locale;

public class User {
    final static private double daysPerYear = 365.25;
    final static private long msecsPerYear = (long) (daysPerYear * 24 * 60 * 60 * 1000);
    private String name;
    private GregorianCalendar birthDay;
    private double longevity;

    public User(String name, GregorianCalendar birthDay, double longevity) {
        this.name = name;
        setBirthDay(birthDay);
        this.longevity = longevity;
    }

    static double getLongevity(int birthYear, int birthMonth,
                               int birthDayOfMonth, int deathYear, int deathMonth,
                               int deathDayOfMonth) {
        Calendar deathDate = GregorianCalendar.getInstance();
        deathDate.set(deathYear, deathMonth, deathDayOfMonth);
        // normalize all deathdays to the stroke of midnight
        deathDate.set(Calendar.HOUR_OF_DAY, 0);
        deathDate.set(Calendar.MINUTE, 0);
        deathDate.set(Calendar.SECOND, 0);
        deathDate.set(Calendar.MILLISECOND, 0);
        Calendar birthDate = GregorianCalendar.getInstance();
        birthDate.set(birthYear, birthMonth, birthDayOfMonth);
        // normalize all birthdays to the stroke of midnight
        birthDate.set(Calendar.HOUR_OF_DAY, 0);
        birthDate.set(Calendar.MINUTE, 0);
        birthDate.set(Calendar.SECOND, 0);
        birthDate.set(Calendar.MILLISECOND, 0);
        long longevityInMsec = deathDate.getTimeInMillis()
                - birthDate.getTimeInMillis();
        if (longevityInMsec <= 0) {
            return 0.0;
        } else {
            return (double) longevityInMsec / msecsPerYear;
        }
    }

    static Calendar getDeathDate(int birthYear, int birthMonth,
                                 int birthDayOfMonth, double longevity) {
        Calendar birthDate = GregorianCalendar.getInstance();
        birthDate.set(birthYear, birthMonth, birthDayOfMonth);
        // normalize all birthdays to the stroke of midnight
        birthDate.set(Calendar.HOUR_OF_DAY, 0);
        birthDate.set(Calendar.MINUTE, 0);
        birthDate.set(Calendar.SECOND, 0);
        birthDate.set(Calendar.MILLISECOND, 0);
        long deathDateInMsecs = birthDate.getTimeInMillis()
                + (long) (longevity * msecsPerYear);
        Calendar deathDate = GregorianCalendar.getInstance();
        deathDate.setTimeInMillis(deathDateInMsecs);
        return deathDate;
    }

    @SuppressWarnings("unused")
    public GregorianCalendar birthDay() {
        return this.birthDay;
    }

    public GregorianCalendar deathDay() {
        GregorianCalendar deathDay = new GregorianCalendar();
        deathDay.setTimeInMillis(deathDayMsec());
        return deathDay;
    }

    @SuppressWarnings("unused")
    public double longevityFromDeathDate(int year, int month, int dayOfMonth) {
        return getLongevity(birthDay.get(Calendar.YEAR),
                birthDay.get(Calendar.MONTH),
                birthDay.get(Calendar.DAY_OF_MONTH), year, month, dayOfMonth);
    }

    public boolean isDead() {
        return msecAlive() + birthDayMsec() > deathDay().getTimeInMillis();
    }

    long deathDayMsec() {
        return birthDayMsec() + lifeDurationMsec();
    }

    public long lifeDurationMsec() {
        return (long) (longevity * msecsPerYear);
    }

    long birthDayMsec() {
        return birthDay.getTimeInMillis();
    }

    long msecAlive() {
        return System.currentTimeMillis() - birthDayMsec();
        //return Calendar.getInstance().getTimeInMillis() - birthDayMsec();
    }

    long reverseMsecAlive() {
        // return deathDayMsec() - msecAlive();
        return lifeDurationMsec() - msecAlive();
    }

    long secAlive() {
        return msecAlive() / 1000;
    }

    private double daysAlive() {
        return (double)secAlive() / 60 * 60 * 24.0;
    }

    @SuppressWarnings("unused")
    public double minutesAlive() {
        return (double)secAlive() / 60;
    }

    @SuppressWarnings("unused")
    public double reverseMinutesAlive() {
        return (double)reverseSecAlive() / 60;
    }

    private double reverseDaysAlive() {
        return (double)reverseSecAlive() / 60 * 60 * 24.0;
    }

    @SuppressWarnings("unused")
    public double yearsAlive() {
        return daysAlive() / daysPerYear;
    }

    // next 2 only used in tests so far

    public double reverseYearsAlive() {
        return reverseDaysAlive() / daysPerYear;
    }

    long reverseSecAlive() {
        return reverseMsecAlive() / 1000;
    }

    double percentAlive() {
        return ((double) msecAlive()) / lifeDurationMsec();
    }

    private long msecAlive(Calendar date) {
        return date.getTimeInMillis() - birthDayMsec();
    }

//    public double percentAlive(Calendar date) {
//        return ((double) msecAlive(date)) / lifeDurationMsec();
//    }

    public double percentAlive(Calendar date) {
        return ((double) msecAlive(date)) / lifeDurationMsec();
    }

    public boolean isSane() {
        boolean sane = longevity > 0 && longevity < 999;
        Calendar earliestBirthDay = GregorianCalendar.getInstance();
        earliestBirthDay.set(1800, 0, 0);
        Calendar latestBirthDay = GregorianCalendar.getInstance();
        latestBirthDay.set(2100, 0, 0);
        sane = sane && birthDay.after(earliestBirthDay)
                && birthDay.before(latestBirthDay);
        return sane;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getApostrophedName() {
        if (name.length() > 0) {
            if (name.toUpperCase(Locale.getDefault()).charAt(name.length() - 1) == 'S')
                name += "'";
            else
                name += "'s";
        }
        return name;
    }

    Calendar getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(GregorianCalendar birthDay) {
        this.birthDay = birthDay;
        // normalize all birthdays to the stroke of midnight
        this.birthDay.set(Calendar.HOUR_OF_DAY, 0);
        this.birthDay.set(Calendar.MINUTE, 0);
        this.birthDay.set(Calendar.SECOND, 0);
        this.birthDay.set(Calendar.MILLISECOND, 0);
    }

    double getLongevity() {
        return longevity;
    }

    public void setLongevity(double longevity) {
        this.longevity = longevity;
    }

    @SuppressWarnings("unused")
    public double getDaysPerYear() {
        return daysPerYear;
    }

    @SuppressWarnings("unused")
    public long getMsecsPerYear() {
        return msecsPerYear;
    }

}
