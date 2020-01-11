package mn.foreman.parker.utils;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Unit tests for {@link TimeUtils}. */
class TimeUtilsTest {

    /** Tests {@link TimeUtils#now()}. */
    @Test
    void testNow() {
        final Instant start = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        final Instant now = TimeUtils.now();
        final Instant end = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        assertEquals(
                0L,
                now.getNano());
        assertTrue(start.isBefore(now) || start.equals(now));
        assertTrue(now.isBefore(end) || now.equals(end));
    }
}