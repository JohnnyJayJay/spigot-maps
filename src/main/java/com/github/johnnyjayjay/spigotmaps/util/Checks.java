package com.github.johnnyjayjay.spigotmaps.util;

import java.awt.Point;

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

    public static void checkStartingPoint(Point startingPoint) {
        checkNotNull(startingPoint, "Starting point");
        checkBounds(startingPoint.x, 0, ImageTools.MINECRAFT_MAP_SIZE.width, "Starting point");
        checkBounds(startingPoint.y, 0, ImageTools.MINECRAFT_MAP_SIZE.height, "Starting point");
    }

    public static void checkBounds(int number, int startInclusive, int endExclusive, String name) {
        check(number >= startInclusive && number < endExclusive, name + " out of bounds");
    }

}
