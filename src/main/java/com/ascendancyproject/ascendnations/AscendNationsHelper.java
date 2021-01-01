package com.ascendancyproject.ascendnations;

import java.time.Duration;

public class AscendNationsHelper {
    public static String durationToString(long duration) {
        return Duration.ofMillis(duration).toString().substring(2).replaceAll("(\\d[HMS])(?!$)", "$1 ").toLowerCase();
    }
}
