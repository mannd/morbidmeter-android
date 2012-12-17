package org.epstudios.morbidmeter;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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

	static public String getFormattedTime() {
		// for testing, clock time for now
		Format formatter = new SimpleDateFormat("hh:mm:ss a");
		String time = formatter.format(new Date());
		return time;
	}

	private String userName;
	private String timeScaleName;
	private Calendar birthday;
	private double longevity;

}
