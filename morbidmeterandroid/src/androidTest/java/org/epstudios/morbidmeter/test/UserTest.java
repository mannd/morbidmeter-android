package org.epstudios.morbidmeter.test;

import java.util.Calendar;
import java.util.GregorianCalendar;

import junit.framework.TestCase;

import org.epstudios.morbidmeter.lib.User;

public class UserTest extends TestCase {

	public UserTest(String name) {
		super(name);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testDeathDay() {
		GregorianCalendar bd = new GregorianCalendar();
		bd.set(Calendar.YEAR, 1950);
		bd.set(Calendar.MONTH, 1);
		bd.set(Calendar.DAY_OF_MONTH, 1);
		User user = new User("default", bd, 80.0);
		int deathYear = user.deathDay().get(Calendar.YEAR);
		assertEquals(2030, deathYear);
	}

	public void testPercentAlive() {
		GregorianCalendar bd = new GregorianCalendar();
		bd.set(Calendar.YEAR, 1950);
		bd.set(Calendar.MONTH, 1);
		bd.set(Calendar.DAY_OF_MONTH, 1);
		User user = new User("default", bd, 80.0);
		Calendar testDate = Calendar.getInstance();
		testDate.set(Calendar.YEAR, 1990);
		testDate.set(Calendar.MONTH, 1);
		testDate.set(Calendar.DAY_OF_MONTH, 1);
		assertEquals(0.5, user.percentAlive(testDate), 0.01);
	}

	public void testIsSane() {
		double longevity = 80.1;
		GregorianCalendar bd = new GregorianCalendar();
		bd.set(Calendar.YEAR, 1950);
		bd.set(Calendar.MONTH, 1);
		bd.set(Calendar.DAY_OF_MONTH, 1);
		User user = new User("", bd, longevity);
		assertEquals(true, user.isSane());
		longevity = -1;
		user = new User("", bd, longevity);
		assertFalse(user.isSane());
		user.setLongevity(88);
		assertTrue(user.isSane());
		bd.set(Calendar.YEAR, 1799);
		user.setBirthDay(bd);
		assertFalse(user.isSane());
		bd.set(Calendar.YEAR, 1801);
		user.setBirthDay(bd);
		assertTrue(user.isSane());
		bd.set(Calendar.YEAR, 2110);
		user.setBirthDay(bd);
		assertFalse(user.isSane());
		bd.set(Calendar.YEAR, 1999);
		assertTrue(user.isSane());
	}

	public void testIsDead() {
		Calendar bd = new GregorianCalendar(1950, Calendar.JANUARY, 1);
		User user = new User("", bd, 60.0); // should die in 2010
		assertTrue(user.isDead());
		user.setLongevity(90.0); // should die in 2040, if MM still around this
									// test will fail!
		assertFalse(user.isDead());
	}

	public void testGetApostophedName() {
		Calendar bd = new GregorianCalendar(1950, Calendar.JANUARY, 1);
		User user = new User("David", bd, 70.0);
		assertEquals(user.getApostrophedName(), "David's");
		User user1 = new User("Semis", bd, 70.0);
		assertEquals(user1.getApostrophedName(), "Semis'");
		User user2 = new User("DAVID", bd, 70.0);
		assertEquals(user2.getApostrophedName(), "DAVID's");

	}
}
