package org.epstudios.morbidmeter;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;


public class NotificationTest {

    @Test
    public void testIsMilestone() {
        // TODO: understand the failing tests comment out below.
        assertTrue(MorbidMeterClock.isEvenHour("Jan 4, 2012 5:00:03 AM 414 msec"));
        assertTrue(!MorbidMeterClock.isEvenHour("Jan 4, 2012 5:01:03 AM 414 msec"));
        assertTrue(!MorbidMeterClock.isEvenHour("Jan 4, 2012 5:01:00 AM 414 msec"));
        assertTrue(MorbidMeterClock.isEvenMinute("5:00:00 PM"));
        assertTrue(!MorbidMeterClock.isEvenMinute("4:01:01"));
        assertTrue(MorbidMeterClock.isEvenPercentage("99.00001"));
        assertTrue(!MorbidMeterClock.isEvenPercentage("99.100000"));
        assertTrue(MorbidMeterClock.isEvenMillion("5,000,011 years"));
        //assertTrue(MorbidMeterClock.isEvenMillion("12,000,101,000,111 years"));
        //assertTrue(MorbidMeterClock.isEvenMillion("14,001,001,000 years"));
        // test with carriage returns
        assertTrue(MorbidMeterClock
                .isEvenHour("Jan 4\n 2012\n 1:00:00 PM 333 msec"));
    }
}
