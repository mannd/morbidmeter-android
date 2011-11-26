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

public class User {
	public User(String name, Calendar birthDay, double longevity) {
		this.name = name;
		this.birthDay = birthDay;
		this.longevity = longevity;
	}
	
	public Calendar birthDay() {
		return this.birthDay;
	}
	
	public Calendar deathDay() {
		Calendar deathDay = Calendar.getInstance();
		deathDay.setTimeInMillis(deathDayMsec());
		return deathDay;
	}
	
	private long deathDayMsec() {
		return birthDayMsec() + lifeDurationMsec();
	}
	
	private long lifeDurationMsec() {
		return (long) longevity * msecsPerYear;
	}
	
	private long birthDayMsec() {
		return birthDay.getTimeInMillis();
	}
	
	public long msecAlive(Calendar date) {
		return date.getTimeInMillis() - birthDayMsec();
	}
	
	private long msecAlive() {
		Calendar now = Calendar.getInstance();
		return msecAlive(now);
	}
	
	public double percentAlive(Calendar date) {
		return ((double) msecAlive(date)) / lifeDurationMsec();
	}
	
	public double percentAlive() {
		return ((double) msecAlive()) / lifeDurationMsec();
	}
	
	final private double daysPerYear = 365.25; 
	final private long msecsPerYear = (long) (daysPerYear * 24 * 60 * 60 * 1000);
	
	
	private String name;
	private Calendar birthDay;
	private double longevity;

}
