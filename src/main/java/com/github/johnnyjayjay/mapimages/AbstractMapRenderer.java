package com.github.johnnyjayjay.mapimages;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The base class for {@link MapRenderer} implementations by this library.
 *
 * @implNote Extending classes should provide a nested Builder class, which, in turn, extends
 * the abstract nested {@link Builder} provided by this class.
 *
 * @see ImageRenderer
 * @see TextRenderer
 * @author Johnny_JayJay (https://www.github.com/JohnnyJayJay)
 */
public abstract class AbstractMapRenderer extends MapRenderer {

    private final Set<UUID> initialReceivers;

    private final boolean renderForAllPlayers;
    private final Set<Player> receivers;
    private final Predicate<MapView> precondition;

    protected AbstractMapRenderer(Set<Player> receivers, Predicate<MapView> precondition) {
        super(!receivers.isEmpty());
        this.renderForAllPlayers = receivers.isEmpty();
        this.receivers = new HashSet<>(receivers);
        this.precondition = precondition;
        this.initialReceivers = receivers.stream().map(Player::getUniqueId).collect(Collectors.toSet());
    }

    @Override
    public final void render(MapView map, MapCanvas canvas, Player player) {
        if (mayRender(map, player)) {
            render(map, canvas);
            receivers.remove(player);
        }
    }

    private boolean mayRender(MapView map, Player player) {
        return (renderForAllPlayers || receivers.contains(player))
                && precondition.test(map);
    }

    public Set<Player> getRemainingPlayers() {
        return Collections.unmodifiableSet(receivers);
    }

    public Stream<Player> streamRemainingPlayers() {
        return receivers.stream();
    }

    public Set<UUID> getInitialReceivers() {
        return Collections.unmodifiableSet(initialReceivers);
    }

    public Stream<UUID> streamInitialReceivers() {
        return initialReceivers.stream();
    }

    /**
     * Renders the map after the preconditions have passed.
     *
     * @param map the {@link MapView} to be rendered.
     * @param canvas the {@link MapCanvas} that can be used to draw on the map.
     */
    protected abstract void render(MapView map, MapCanvas canvas);

    /**
     * The base Builder class
     *
     * @param <T> The type that implements {@link AbstractMapRenderer}.
     * @param <U> The type that extends this class.
     */
    @SuppressWarnings("unchecked")
    protected static abstract class Builder<T, U extends Builder<T, U>> {

        protected final Set<Player> receivers = new HashSet<>();
        protected Predicate<MapView> precondition = (map) -> true;

        @NotNull
        public abstract T build();

        protected final void check() {
            Checks.checkNotNull(precondition, "Precondition");
        }

        @NotNull
        public U addPlayers(@NotNull Collection<Player> players) {
            this.receivers.addAll(players);
            return (U) this;
        }

        @NotNull
        public U addPlayers(@NotNull Player... players) {
            return this.addPlayers(Arrays.asList(players));
        }

        @NotNull
        public U precondition(@NotNull Predicate<MapView> precondition) {
            this.precondition = precondition;
            return (U) this;
        }
    }
}
