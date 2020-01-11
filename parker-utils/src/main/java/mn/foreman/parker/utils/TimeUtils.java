package mn.foreman.parker.utils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/** {@link TimeUtils} contains utility functions to working with time. */
public class TimeUtils {

    /** Constructor. */
    private TimeUtils() {
        // Do nothing
    }

    /**
     * Returns an {@link Instant} of now, but truncated to {@link
     * ChronoUnit#SECONDS}.
     *
     * @return The new {@link Instant}.
     */
    public static Instant now() {
        return Instant.now().truncatedTo(ChronoUnit.SECONDS);
    }
}
