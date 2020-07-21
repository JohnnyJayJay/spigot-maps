package com.github.johnnyjayjay.spigotmaps;

import com.github.johnnyjayjay.spigotmaps.util.Compatibility;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Arrays;
import java.util.List;

/**
 * A class representing a {@link MapView} with a storage, renderers and some convenience methods.
 *
 * @see MapBuilder
 * @see RenderedMap#create(MapView, MapStorage)
 * @author Johnny_JayJay (https://www.github.com/JohnnyJayJay)
 */
public class RenderedMap {

    private final MapView view;
    private final MapStorage storage;

    private RenderedMap(MapView view, MapStorage storage) {
        this.view = view;
        this.storage = storage;
        int id = Compatibility.getId(view);
        view.getRenderers().forEach((renderer) -> storage.store(id, renderer));
    }

    /**
     * Creates a default instance of this class with the renderers set in the {@link MapView} argument.
     *
     * @param view a user-specific {@link MapView} that is used as the base of this instance.
     * @param storage a storage to keep track of the renderers or {@code null} if this {@link RenderedMap}
     *                should not store its renderers.
     * @return a never-null instance of {@link RenderedMap}.
     */
    public static RenderedMap create(MapView view, MapStorage storage) {
        MapStorage effectiveStorage = storage == null ? new MapStorage() {
            @Override
            public void remove(int mapId, MapRenderer renderer) {}
            @Override
            public void store(int mapId, MapRenderer renderer) {}
            @Override
            public List<MapRenderer> provide(int mapId) {
                return null;
            }
        } : storage;
        return new RenderedMap(view, effectiveStorage);
    }

    /**
     * Creates a simple instance of {@link RenderedMap} just taking the provided renderers.
     *
     * @param renderers 0-n renderers that should apply to this map. Must not be {@code null}.
     * @return a never-null RenderedMap.
     */
    public static RenderedMap create(MapRenderer... renderers) {
        return MapBuilder.create().addRenderers(renderers).build();
    }

    /**
     * Creates a copy of this map. The copy will use a different {@link MapView} with this map's renderers.
     * The copy will use the same world, if it is present, otherwise it will choose one randomly. Finally, the copy
     * will use the same {@link MapStorage} as this map.
     *
     * @return a copy of this map.
     */
    public RenderedMap createCopy() {
        return MapBuilder.create()
                .addRenderers(view.getRenderers())
                .store(storage)
                .world(view.getWorld())
                .build();
    }

    /**
     * Returns the view backing this RenderedMap.
     *
     * @return a MapView instance.
     */
    public MapView getView() {
        return view;
    }

    /**
     * Creates and returns an {@link ItemStack} of the type {@code Material.MAP} associated with this instance's
     * underlying {@link MapView} and no further metadata.
     *
     * @return a never-{@code null} ItemStack.
     * @see #createItemStack(String, String...)
     */
    public ItemStack createItemStack() {
        return createItemStack(null);
    }

    /**
     * Creates and returns an {@link ItemStack} of the type {@code Material.MAP} associated with this instance's
     * underlying {@link MapView}.
     *
     * @param displayName the display name of the result.
     * @param lore the lore of the result or nothing, if there shouldn't be lore.
     * @return a new ItemStack.
     */
    public ItemStack createItemStack(String displayName, String... lore) {
        ItemStack itemStack = new ItemStack(Material.MAP);
        MapMeta mapMeta = (MapMeta) itemStack.getItemMeta();
        mapMeta.setMapView(view);
        mapMeta.setDisplayName(displayName);
        mapMeta.setLore(lore.length == 0 ? null : Arrays.asList(lore));
        itemStack.setItemMeta(mapMeta);
        return itemStack;
    }
}
