package ru.hse.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DurationUtilTest {

    @Test
    public void testSeconds() {

        assertEquals(DurationUtil.parseDuration("2s"), 2);
    }

    @Test
    public void testMinutes() {
        assertEquals(DurationUtil.parseDuration("1m2s"), 62);
    }

    @Test
    public void testHours() {
        assertEquals(DurationUtil.parseDuration("1h1m1s"), 3661);
    }

    @Test
    public void testDays() {
        assertEquals(DurationUtil.parseDuration("1d1m"), 86460);
    }

    @Test
    public void testUnknown() {
        assertThrows(IllegalArgumentException.class, () -> DurationUtil.parseDuration("1l"));
    }

    @Test
    public void testUnknown2() {
        assertThrows(IllegalArgumentException.class, () -> DurationUtil.parseDuration("asdad"));
    }
}