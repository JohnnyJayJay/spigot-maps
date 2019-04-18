package com.github.johnnyjayjay.spigotmaps;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.MapInitializeEvent;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.plugin.Plugin;

import java.util.List;

/**
 * Listens for {@link MapInitializeEvent}s and assigns the renderers provided by a {@link MapStorage}
 * for the subject {@link MapView} of this event, if registered.
 *
 * If you do not want or need your map renderers to be persistent, you need not use this class.
 *
 * @see InitializationListener#register(MapStorage, Plugin)
 * @author Johnny_JayJay (https://www.github.com/JohnnyJayJay)
 */
public final class InitializationListener implements Listener {

    private final MapStorage storage;

    private InitializationListener(MapStorage storage) {
        this.storage = storage;
    }

    @EventHandler
    public void onMapInitialize(MapInitializeEvent event) {
        MapView map = event.getMap();
        List<MapRenderer> renderers = storage.provide(map.getId());
        if (renderers != null) {
            map.getRenderers().forEach(map::removeRenderer);
            renderers.forEach(map::addRenderer);
        }
    }

    /**
     * Registers an instance of this class as a listener for the specified plugin.
     * Using this only makes sense if you want to use a {@link MapStorage} implementation that
     * stores renderers persistently.
     *
     * @param storage the {@link MapStorage} to be used as a resource for renderers.
     * @param plugin the plugin to register this listener for.
     */
    public static void register(MapStorage storage, Plugin plugin) {
        Bukkit.getPluginManager().registerEvents(new InitializationListener(storage), plugin);
    }

}
