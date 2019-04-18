package com.github.johnnyjayjay.spigotmaps;

import org.bukkit.map.MapRenderer;
import org.bukkit.plugin.Plugin;

import java.util.List;

/**
 * By implementing this interface, you can provide a storage for {@link RenderedMap}s
 * that may store their renderers or their properties, e.g. to be reused after a server restart.
 *
 * Instances of this interface are used by instances of {@link InitializationListener}, if they are registered.
 *
 * @see InitializationListener#register(MapStorage, Plugin)
 * @author Johnny_JayJay (https://www.github.com/JohnnyJayJay)
 */
public interface MapStorage {

    /**
     * Removes a renderer from the corresponding map storage.
     *
     * @param mapId the identifier of the map whose renderers are to be modified
     * @param renderer the renderer to be removed
     */
    void remove(int mapId, MapRenderer renderer);

    /**
     * Adds a renderer to a specific map in the storage.
     *
     * @param mapId the identifier of the map whose renderers are to be modified
     * @param renderer the renderer to be added
     */
    void store(int mapId, MapRenderer renderer);

    /**
     * Provides the renderers stored for a specific map.
     *
     * @param mapId the identifier of the map whose renderers are to be retrieved
     * @return  a list of renderers - empty, if no renderers should be applied or {@code null},
     *          if no specific renderers are stored for this map
     */
    List<MapRenderer> provide(int mapId);

}
