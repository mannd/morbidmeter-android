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

public class User {
	final private double daysPerYear = 365.25;
	final private long msecsPerYear = (long) (daysPerYear * 24 * 60 * 60 * 1000);

	public User(String name, Calendar birthDay, double longevity) {
		this.name = name;
		setBirthDay(birthDay);
		this.longevity = longevity;
	}

	public Calendar birthDay() {
		return this.birthDay;
	}

	public Calendar deathDay() {
		Calendar deathDay = new GregorianCalendar();
		deathDay.setTimeInMillis(deathDayMsec());
		return deathDay;
	}

	public boolean isDead() {
		return msecAlive() + birthDayMsec() > deathDay().getTimeInMillis();
	}

	public long deathDayMsec() {
		return birthDayMsec() + lifeDurationMsec();
	}

	public long lifeDurationMsec() {
		return (long) (longevity * msecsPerYear);
	}

	public long birthDayMsec() {
		return birthDay.getTimeInMillis();
	}

	public long msecAlive() {
		return System.currentTimeMillis() - birthDayMsec();
	}

	public long reverseMsecAlive() {
		return deathDayMsec() - msecAlive();
	}

	public long secAlive() {
		return msecAlive() / 60;
	}

	public double daysAlive() {
		return secAlive() / 60 * 60 * 24.0;
	}

	public double reverseDaysAlive() {
		return reverseSecAlive() / 60 * 60 * 24.0;
	}

	public double yearsAlive() {
		return daysAlive() / daysPerYear;
	}

	public double reverseYearsAlive() {
		return reverseDaysAlive() / daysPerYear;
	}

	public long reverseSecAlive() {
		return reverseMsecAlive() / 60;
	}

	public double percentAlive() {
		return ((double) msecAlive()) / lifeDurationMsec();
	}

	// next 2 only used in tests so far

	public long msecAlive(Calendar date) {
		return date.getTimeInMillis() - birthDayMsec();
	}

	public double percentAlive(Calendar date) {
		return ((double) msecAlive(date)) / lifeDurationMsec();
	}

	public boolean isSane() {
		boolean sane = longevity > 0 && longevity < 999;
		Calendar earliestbirthDay = GregorianCalendar.getInstance();
		earliestbirthDay.set(1800, 0, 0);
		Calendar latestbirthDay = GregorianCalendar.getInstance();
		latestbirthDay.set(2100, 0, 0);
		sane = sane && birthDay.after(earliestbirthDay)
				&& birthDay.before(latestbirthDay);
		return sane;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Calendar getBirthDay() {
		return birthDay;
	}

	public void setBirthDay(Calendar birthDay) {
		this.birthDay = birthDay;
		// normalize all birthdays to the stroke of midnight
		this.birthDay.set(Calendar.HOUR, 0);
		this.birthDay.set(Calendar.MINUTE, 0);
		this.birthDay.set(Calendar.SECOND, 0);
		this.birthDay.set(Calendar.MILLISECOND, 0);
	}

	public double getLongevity() {
		return longevity;
	}

	public void setLongevity(double longevity) {
		this.longevity = longevity;
	}

	public double getDaysPerYear() {
		return daysPerYear;
	}

	public long getMsecsPerYear() {
		return msecsPerYear;
	}

	private String name;
	private Calendar birthDay;
	private double longevity;

}
