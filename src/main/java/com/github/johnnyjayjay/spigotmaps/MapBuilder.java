package com.github.johnnyjayjay.spigotmaps;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * A builder class used to create instances of {@link RenderedMap}.
 *
 * @see MapBuilder#create()
 * @author Johnny_JayJay (https://www.github.com/JohnnyJayJay)
 */
public class MapBuilder {

    private World world;
    private MapView mapView;
    private List<MapRenderer> renderers = new ArrayList<>();
    private MapStorage storage;

    private MapBuilder() {
    }

    /**
     * Creates an empty MapBuilder.
     *
     * @return a new instance of this class.
     */
    @NotNull
    public static MapBuilder create() {
        return new MapBuilder();
    }

    /**
     * Creates an instance of {@link RenderedMap} based on the settings made.
     *
     * @throws IllegalArgumentException if any renderer is {@code null}.
     * @return a new {@link RenderedMap}.
     */
    @NotNull
    public RenderedMap build() {
        world = world == null ? Bukkit.getWorlds().stream().findAny().orElse(null) : world;
        Checks.assertNotNull(world);
        Checks.check(renderers.stream().noneMatch(Objects::isNull), "MapRenderers must not be null");
        MapView mapView = this.mapView == null ? Bukkit.createMap(world) : this.mapView;
        mapView.getRenderers().forEach(mapView::removeRenderer);
        renderers.forEach(mapView::addRenderer);
        return RenderedMap.create(mapView, storage);
    }

    /**
     * Sets the world to be used for the {@link MapView}.
     *
     * Without setting this, a random world will be assigned.
     *
     * @param world the world the map should belong to or {@code null} if any world can be used.
     * @return this.
     */
    @NotNull
    public MapBuilder world(@Nullable World world) {
        this.world = world;
        return this;
    }

    /**
     * Adds a list of {@link MapRenderer}s to be used to render this map.
     *
     * Without setting any renderer, the map will have no renderers.
     *
     * @see AbstractMapRenderer
     * @see ImageRenderer
     * @see TextRenderer
     * @param renderers A non-null list of renderers.
     * @return this.
     */
    @NotNull
    public MapBuilder addRenderers(@NotNull List<MapRenderer> renderers) {
        this.renderers.addAll(renderers);
        return this;
    }

    /**
     * Adds one or more {@link MapRenderer}s to be used for the map.
     *
     * Without setting any renderer, the map will have no renderers.
     *
     * @see this#addRenderers(List)
     * @param renderers One or more renderers, or a non-null array of renderers.
     * @return this.
     */
    @NotNull
    public MapBuilder addRenderers(@NotNull MapRenderer... renderers) {
        return addRenderers(Arrays.asList(renderers));
    }

    /**
     * Sets the {@link MapView} to be used for this map. This can be used to apply settings to existing maps.
     *
     * Without setting this, a new MapView will be created.
     *
     * @param mapView a {@link MapView} or {@code null}, if a new one should be created.
     * @return this.
     */
    @NotNull
    public MapBuilder view(@Nullable MapView mapView) {
        this.mapView = mapView;
        return this;
    }

    /**
     * Sets the {@link MapStorage} to be used to store the map's renderers.
     *
     * Without setting this, no storage will be set, which means that renderers may not be
     * persistent (the map will be rendered nonetheless).
     *
     * @param storage a {@link MapStorage} or {@code null} if no storage should be set.
     * @return this.
     */
    @NotNull
    public MapBuilder store(@Nullable MapStorage storage) {
        this.storage = storage;
        return this;
    }

}
