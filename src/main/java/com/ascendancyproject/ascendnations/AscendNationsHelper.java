package com.ascendancyproject.ascendnations;

import java.time.Duration;

public class AscendNationsHelper {
    public static String durationToString(long duration) {
        Duration dur = Duration.ofMillis(duration);

        StringBuilder sb = new StringBuilder();

        if (dur.toDays() > 0) {
            sb.append(dur.toDays());
            sb.append("d ");
        }

        if (dur.toHours() % 24 > 0) {
            sb.append(dur.toHours() % 24);
            sb.append("h ");
        }

        if (dur.toMinutes() % 60 > 0) {
            sb.append(dur.toMinutes() % 60);
            sb.append("m ");
        }

        sb.append(dur.getSeconds() % 60);
        sb.append("s");

        return sb.toString();
    }
}
