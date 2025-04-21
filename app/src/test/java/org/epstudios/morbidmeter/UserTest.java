package org.epstudios.morbidmeter;

import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserTest {

    @Test
    public void testDeathDay() {
        GregorianCalendar bd = new GregorianCalendar();
        bd.set(Calendar.YEAR, 1950);
        bd.set(Calendar.MONTH, 1);
        bd.set(Calendar.DAY_OF_MONTH, 1);
        User user = new User("default", bd, 80.0);
        int deathYear = user.deathDay().get(Calendar.YEAR);
        assertEquals(2030, deathYear);
    }

    @Test
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
        double percent = user.percentAlive(testDate);
        assertEquals(0.5, percent, 0.01);
//        assertEquals(0.5, user.percentAlive(testDate), 0.01);
    }

    @Test
    public void testIsSane() {
        double longevity = 80.1;
        GregorianCalendar bd = new GregorianCalendar();
        bd.set(Calendar.YEAR, 1950);
        bd.set(Calendar.MONTH, 1);
        bd.set(Calendar.DAY_OF_MONTH, 1);
        User user = new User("", bd, longevity);
        assertTrue(user.isSane());
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

    @Test
    public void testIsDead() {
        GregorianCalendar bd = new GregorianCalendar(1950, Calendar.JANUARY, 1);
        User user = new User("", bd, 60.0); // should die in 2010
        assertTrue(user.isDead());
        user.setLongevity(90.0); // should die in 2040, if MM still around this
        // test will fail!
        assertFalse(user.isDead());
    }

    @Test
    public void testGetApostophedName() {
        GregorianCalendar bd = new GregorianCalendar(1950, Calendar.JANUARY, 1);
        User user = new User("David", bd, 70.0);
        assertEquals("David's", user.getApostrophedName());
        User user1 = new User("Semis", bd, 70.0);
        assertEquals("Semis'", user1.getApostrophedName());
        User user2 = new User("DAVID", bd, 70.0);
        assertEquals("DAVID's", user2.getApostrophedName());

    }
}
