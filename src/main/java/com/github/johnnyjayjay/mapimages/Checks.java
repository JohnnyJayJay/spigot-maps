package com.github.johnnyjayjay.mapimages;

import java.util.Collection;

/**
 * @author Johnny_JayJay (https://www.github.com/JohnnyJayJay)
 */
final class Checks {

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

}
