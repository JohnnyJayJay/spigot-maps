package com.github.johnnyjayjay.spigotmaps.util;

import org.bukkit.Bukkit;
import org.bukkit.map.MapView;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Arrays;

/**
 * @author Johnny_JayJay (https://www.github.com/JohnnyJayJay)
 */
public final class Compatibility {

  private static final MethodHandle getId;

  static {
    MethodType methodType = MethodType.methodType(isLegacy() ? short.class : int.class);
    try {
      getId = MethodHandles.publicLookup().findVirtual(MapView.class, "getId", methodType);
    } catch (NoSuchMethodException | IllegalAccessException e) {
      throw new AssertionError("MapView#getId() could not be found. This should never happen.");
    }
  }

  public static boolean isLegacy() {
    int[] version = Arrays.stream(Bukkit.getVersion().split("\\."))
        .mapToInt(Integer::parseInt)
        .toArray();
    return version[0] < 1 || version[1] < 13 || version[2] < 2;
  }

  public static int getId(MapView map) {
    try {
      return ((Number) getId.invoke(map)).intValue();
    } catch (Throwable throwable) {
      throw new RuntimeException(throwable);
    }
  }

}
