package com.ascendancyproject.ascendnations;

import org.bukkit.Location;
import org.bukkit.Material;

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

    public static boolean isRail(Material material) {
        switch (material) {
            case RAIL:
            case ACTIVATOR_RAIL:
            case DETECTOR_RAIL:
            case POWERED_RAIL:
                return true;

            default:
                return false;
        }
    }

    public static boolean isRedstone(Material material) {
        switch (material) {
            // Buttons:
            case BIRCH_BUTTON:
            case ACACIA_BUTTON:
            case CRIMSON_BUTTON:
            case DARK_OAK_BUTTON:
            case JUNGLE_BUTTON:
            case OAK_BUTTON:
            case POLISHED_BLACKSTONE_BUTTON:
            case SPRUCE_BUTTON:
            case STONE_BUTTON:
            case WARPED_BUTTON:

            // Lever:
            case LEVER:

            // Pressure plates:
            case BIRCH_PRESSURE_PLATE:
            case ACACIA_PRESSURE_PLATE:
            case CRIMSON_PRESSURE_PLATE:
            case DARK_OAK_PRESSURE_PLATE:
            case JUNGLE_PRESSURE_PLATE:
            case OAK_PRESSURE_PLATE:
            case POLISHED_BLACKSTONE_PRESSURE_PLATE:
            case SPRUCE_PRESSURE_PLATE:
            case STONE_PRESSURE_PLATE:
            case WARPED_PRESSURE_PLATE:
            case LIGHT_WEIGHTED_PRESSURE_PLATE:
            case HEAVY_WEIGHTED_PRESSURE_PLATE:

            // Tripwire:
            case TRIPWIRE:
            case TRIPWIRE_HOOK:
                return true;

            default:
                return false;
        }
    }

    public static short shortFromLocation(Location location) {
        return (short) (
                (location.getBlockX() & 0xF) << 8 |
                (location.getBlockZ() & 0xF) << 4 |
                (location.getBlockY() & 0xF)
        );
    }
}
