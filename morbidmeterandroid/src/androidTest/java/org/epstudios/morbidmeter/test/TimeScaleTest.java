package org.epstudios.morbidmeter.test;

import java.util.Calendar;
import java.util.GregorianCalendar;

import junit.framework.TestCase;

import org.epstudios.morbidmeter.lib.TimeScale;
import org.epstudios.morbidmeter.lib.User;

public class TimeScaleTest extends TestCase {
	public void testTimeScaleDuration() {
		TimeScale ts = new TimeScale("test", 0, 100);
		assertEquals(100, ts.duration());
	}

	public void testProportionalTime() {
		TimeScale ts = new TimeScale("", 0, 100);
		assertEquals(50.0, ts.proportionalTime(.5));
		assertEquals(0.0, ts.proportionalTime(0));
		assertEquals(100.0, ts.proportionalTime(1));
	}

	public void testReverseProportionalTime() {
		TimeScale ts = new TimeScale("", 500, 600);
		assertEquals(550.0, ts.reverseProportionalTime(.5));
		assertEquals(500.0, ts.reverseProportionalTime(1));
		assertEquals(600.0, ts.reverseProportionalTime(0));
	}

	public void testPercentTimeScale() {
		TimeScale ts = new TimeScale("percent", 0, 100);
		GregorianCalendar bd = new GregorianCalendar();
		bd.set(1950, 1, 1);
		User u = new User("test", bd, 50.0);
		Calendar date1 = Calendar.getInstance();
		Calendar date2 = Calendar.getInstance();
		date1.set(1975, 1, 1);
		date2.set(1960, 1, 1);
		assertTrue(ts.proportionalTime(u.percentAlive(date1)) > ts
				.proportionalTime(u.percentAlive(date2)));
		assertEquals(50.0, ts.proportionalTime(u.percentAlive(date1)), 1.0);
	}

	public void testLifeDurationMsec() {
		GregorianCalendar bd = new GregorianCalendar();
		bd.set(1950, 1, 1);
		User u = new User("test", bd, 50.5);
		assertEquals((long) (50.5 * (long) (365.25 * 24 * 60 * 60 * 1000)),
				u.lifeDurationMsec());
	}
}
