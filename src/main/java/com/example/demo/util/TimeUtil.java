package com.example.demo.util;

import java.time.Instant;

public final class TimeUtil {

    private TimeUtil() {
    }

    public static Instant getExpirationFromNow(long millisToAdd) {
        return Instant.now().plusMillis(millisToAdd);
    }

    public static int millisToSeconds(long millis) {
        return (int) (millis / 1000);
    }
}
