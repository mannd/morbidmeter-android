package org.epstudios.morbidmeter;


import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TimeScaleTest {
    @Test
    public void testTimeScaleDuration() {
        SimpleTimeScale ts = new SimpleTimeScale(0, 0, 100);
        assertEquals(100, ts.duration());
    }

    @Test
    public void testProportionalTime() {
        SimpleTimeScale ts = new SimpleTimeScale(0, 0, 100);
        assertEquals(50.0, ts.proportionalTime(.5));
        assertEquals(0.0, ts.proportionalTime(0));
        assertEquals(100.0, ts.proportionalTime(1));
    }

    @Test
    public void testReverseProportionalTime() {
        SimpleTimeScale ts = new SimpleTimeScale(0, 500, 600);
        assertEquals(550.0, ts.reverseProportionalTime(.5));
        assertEquals(500.0, ts.reverseProportionalTime(1));
        assertEquals(600.0, ts.reverseProportionalTime(0));
    }

    @Test
    public void testPercentTimeScale() {
        SimpleTimeScale ts = new SimpleTimeScale(0, 0, 100);
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

    @Test
    public void testLifeDurationMsec() {
        GregorianCalendar bd = new GregorianCalendar();
        bd.set(1950, 1, 1);
        User u = new User("test", bd, 50.5);
        assertEquals((long) (50.5 * (long) (365.25 * 24 * 60 * 60 * 1000)),
                u.lifeDurationMsec());
    }
}
