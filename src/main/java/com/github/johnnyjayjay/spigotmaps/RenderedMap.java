package com.github.johnnyjayjay.spigotmaps;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * A class representing a {@link MapView} with a storage, renderers and some convenience methods.
 *
 * @see MapBuilder
 * @see RenderedMap#create(MapView, MapStorage)
 * @author Johnny_JayJay (https://www.github.com/JohnnyJayJay)
 */
public class RenderedMap implements MapView {

    private final MapView view;
    private final MapStorage storage;

    private RenderedMap(MapView view, MapStorage storage) {
        this.view = view;
        this.storage = storage;
        view.getRenderers().forEach((renderer) -> storage.store(view.getId(), renderer));
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
     * Adds a renderer to this map and stores it in the storage, if one is present.
     *
     * @param renderer the {@link MapRenderer} to add.
     */
    @Override
    public void addRenderer(MapRenderer renderer) {
        storage.store(view.getId(), renderer);
        view.addRenderer(renderer);
    }

    /**
     * Removes a renderer from this map and from the storage, if one is present.
     *
     * @param renderer the {@link MapRenderer} to remove.
     */
    @Override
    public boolean removeRenderer(MapRenderer renderer) {
        storage.remove(view.getId(), renderer);
        return view.removeRenderer(renderer);
    }

    @Override
    public boolean isUnlimitedTracking() {
        return view.isUnlimitedTracking();
    }

    @Override
    public void setUnlimitedTracking(boolean b) {
        view.setUnlimitedTracking(b);
    }

    /**
     * Returns the renderers set for this map.
     *
     * @return a never-null and immutable List containing the renderers.
     */
    @Override
    public List<MapRenderer> getRenderers() {
        return Collections.unmodifiableList(view.getRenderers());
    }

    /**
     * Returns the renderers of this map as a {@link Stream}.
     *
     * @return the renderer list, streamed.
     */
    public Stream<MapRenderer> streamRenderers() {
        return view.getRenderers().stream();
    }

    /**
     * Returns the identifier of the underlying {@link MapView}.
     *
     * @return the map id.
     */
    @Override
    public int getId() {
        return view.getId();
    }

    @Override
    public boolean isVirtual() {
        return view.isVirtual();
    }

    @Override
    public Scale getScale() {
        return view.getScale();
    }

    @Override
    public void setScale(Scale scale) {
        view.setScale(scale);
    }

    @Override
    public int getCenterX() {
        return view.getCenterX();
    }

    @Override
    public int getCenterZ() {
        return view.getCenterZ();
    }

    @Override
    public void setCenterX(int i) {
        view.setCenterX(i);
    }

    @Override
    public void setCenterZ(int i) {
        view.setCenterZ(i);
    }

    @Override
    public World getWorld() {
        return view.getWorld();
    }

    @Override
    public void setWorld(World world) {
        view.setWorld(world);
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
        MapMeta mapMeta = (MapMeta) Bukkit.getItemFactory().getItemMeta(Material.MAP);
        mapMeta.setMapView(view);
        mapMeta.setDisplayName(displayName);
        mapMeta.setLore(lore.length == 0 ? null : Arrays.asList(lore));
        ItemStack itemStack = new ItemStack(Material.MAP);
        itemStack.setItemMeta(mapMeta);
        return itemStack;
    }

    /**
     * Creates an {@link ItemStack} using {@link #createItemStack()} and adds it to a player's inventory.
     *
     * @param player the player to give the item to. Must not be {@code null}.
     */
    public void give(Player player) {
        player.getInventory().addItem(createItemStack());
        player.updateInventory();
    }
}
