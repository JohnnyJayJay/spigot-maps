package com.github.johnnyjayjay.spigotmaps;

import java.awt.Point;
import java.util.Collection;

/**
 * internal class
 *
 * @author Johnny_JayJay (https://www.github.com/JohnnyJayJay)
 */
public final class Checks {

    private Checks() {}

    public static void check(boolean condition, String message) {
        if (!condition)
            throw new IllegalArgumentException(message);
    }

    public static void checkNotNull(Object o, String name) {
        check(o != null, name + " must not be null");
    }

    public static void assertNotNull(Object o) {
        if (o == null)
            throw new AssertionError("Unexpected null value");
    }

    public static void checkNotEmpty(Collection<?> collection, String name) {
        check(!collection.isEmpty(), name + " must not be empty");
    }

    public static void checkStartingPoint(Point point) {
        Checks.checkNotNull(point, "Starting point");
        Checks.check(point.x >= 0 && point.y >= 0, "Negative coordinates are not allowed");
        Checks.check(point.x <= ImageTools.MINECRAFT_MAP_SIZE.width
                        && point.y <= ImageTools.MINECRAFT_MAP_SIZE.height,
                "Starting point is out of minecraft map bounds");
    }

}
