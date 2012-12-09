package org.epstudios.morbidmeter;

import java.util.Calendar;

public class MorbidMeterClock {
	public MorbidMeterClock(String userName, String timeScaleName,
			Calendar birthday, double longevity) {
		this.userName = userName;
		this.timeScaleName = timeScaleName;
		this.birthday = birthday;
		this.longevity = longevity;

	}

	public Calendar getBirthday() {
		return birthday;
	}

	private String userName;
	private String timeScaleName;
	private Calendar birthday;
	private double longevity;

}
