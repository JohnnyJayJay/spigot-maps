package com.github.johnnyjayjay.spigotmaps.rendering;

import com.github.johnnyjayjay.spigotmaps.Checks;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapView;

import java.util.Objects;

/**
 * A container class for every map rendering operation. It contains the subject
 * {@link MapView}, a {@link MapCanvas} as well as the {@link Player} the map is rendered for.
 *
 * @see AbstractMapRenderer
 * @author Johnny_JayJay (https://www.github.com/JohnnyJayJay)
 */
public class RenderContext {

    private final MapView mapView;
    private final MapCanvas mapCanvas;
    private final Player player;

    private RenderContext(MapView mapView, MapCanvas mapCanvas, Player player) {
        this.mapView = mapView;
        this.mapCanvas = mapCanvas;
        this.player = player;
    }

    /**
     * Creates a new {@link RenderContext} based on the given arguments.
     *
     * @param mapView the {@link MapView} this rendering will apply to.
     * @param mapCanvas a {@link MapCanvas} that can be used to modify the map.
     * @param player the {@link Player} this map is being rendered for.
     * @return a never-null instance of {@link RenderContext}.
     * @throws IllegalArgumentException if any of the arguments is {@code null}.
     */
    public static RenderContext create(MapView mapView, MapCanvas mapCanvas, Player player) {
        Checks.checkNotNull(mapView, "MapView");
        Checks.checkNotNull(mapCanvas, "MapCanvas");
        Checks.checkNotNull(player, "Player");
        return new RenderContext(mapView, mapCanvas, player);
    }

    /**
     * Returns the MapView.
     */
    public MapView getView() {
        return mapView;
    }

    /**
     * Returns the MapCanvas.
     */
    public MapCanvas getCanvas() {
        return mapCanvas;
    }

    /**
     * Returns the Player.
     */
    public Player getPlayer() {
        return player;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RenderContext that = (RenderContext) o;
        return mapView.getId() == that.mapView.getId()
                && Objects.equals(player, that.player);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mapView.getId(), player);
    }
}
