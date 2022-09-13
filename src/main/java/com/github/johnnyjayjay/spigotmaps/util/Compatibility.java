package com.github.johnnyjayjay.spigotmaps.util;

import org.bukkit.Bukkit;
import org.bukkit.map.MapView;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Johnny_JayJay (https://www.github.com/JohnnyJayJay)
 */
public final class Compatibility {

    private static final boolean legacy;
    private static final MethodHandle getId;

    static {
        Matcher versionFinder = Pattern.compile("(?<=\\(MC: )\\d+\\.\\d+(\\.\\d+)?(?=\\))").matcher(Bukkit.getVersion());
        if (!versionFinder.find()) {
            throw new AssertionError("Could not find MC version in Bukkit.getVersion()");
        }
        int[] version = Arrays.stream(versionFinder.group().split("\\."))
                .mapToInt(Integer::parseInt)
                .toArray();
        legacy = !(version[0] > 1
                || (version[0] == 1 && version[1] > 13)
                || (version.length > 2 && version[0] == 1 && version[1] == 13 && version[2] >= 2));

        MethodType methodType = MethodType.methodType(legacy ? short.class : int.class);
        try {
            getId = MethodHandles.publicLookup().findVirtual(MapView.class, "getId", methodType);
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new AssertionError("MapView#getId() could not be found. This should never happen.");
        }
    }

    public static boolean isLegacy() {
        return legacy;
    }

    public static int getId(MapView map) {
        try {
            return ((Number) getId.invoke(map)).intValue();
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

}
