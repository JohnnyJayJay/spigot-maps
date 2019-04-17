package com.github.johnnyjayjay.spigotmaps;

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
    private final Predicate<RenderContext> precondition;

    protected AbstractMapRenderer(Set<Player> receivers, Predicate<RenderContext> precondition) {
        super(!receivers.isEmpty());
        this.renderForAllPlayers = receivers.isEmpty();
        this.receivers = new HashSet<>(receivers);
        this.precondition = precondition;
        this.initialReceivers = receivers.stream().map(Player::getUniqueId).collect(Collectors.toSet());
    }

    @Override
    public final void render(MapView map, MapCanvas canvas, Player player) {
        RenderContext context = RenderContext.create(map, canvas, player);
        if (mayRender(context)) {
            render(context);
            receivers.remove(player);
        }
    }

    private boolean mayRender(RenderContext context) {
        return (renderForAllPlayers || receivers.contains(context.getPlayer()))
                && precondition.test(context);
    }

    /**
     * Returns an immutable, unordered {@link Set} of players, which contains the players this renderer
     * has not rendered a map for yet or an empty Set if this renderer renders for all players anyway.
     *
     * @return a Set.
     */
    public Set<Player> getRemainingPlayers() {
        return Collections.unmodifiableSet(receivers);
    }

    /**
     * Similar to {@link this#getRemainingPlayers()}, but returns a {@link Stream} to work with.
     * Useful if you want to operate on the remaining players and want to avoid the overhead of
     * creating an immutable set.
     *
     * @return a Stream of the remaining players.
     */
    public Stream<Player> streamRemainingPlayers() {
        return receivers.stream();
    }

    /**
     * Returns an immutable, unordered Set of UUIDs belonging to the players that were added to this
     * renderer when it was created. This will be empty if the renderer renders for all players.
     *
     * @return a Set.
     */
    public Set<UUID> getInitialReceivers() {
        return Collections.unmodifiableSet(initialReceivers);
    }

    /**
     * Similar to {@link this#getInitialReceivers()}, but as a {@link Stream}.
     *
     * @return a Stream of UUIDs belonging to the players this renderer was originally created for.
     */
    public Stream<UUID> streamInitialReceivers() {
        return initialReceivers.stream();
    }

    /**
     * Renders the map after the preconditions have passed, i.e.:
     * <ul>
     *     <li>The predicate's test was successful</li>
     *     <li>This renderer applies to the player or</li>
     *     <li>This renderer renders maps for all players</li>
     * </ul>
     *
     * @param context the context for this rendering operation.
     */
    protected abstract void render(RenderContext context);

    /**
     * The base Builder class for children of {@link AbstractMapRenderer}.
     *
     * @param <T> The type that implements {@link AbstractMapRenderer}.
     * @param <U> The type that extends this class.
     * @author Johnny_JayJay (https://www.github.com/JohnnyJayJay)
     */
    @SuppressWarnings("unchecked")
    protected static abstract class Builder<T, U extends Builder<T, U>> {

        protected final Set<Player> receivers = new HashSet<>();
        protected Predicate<RenderContext> precondition = (ctx) -> true;

        /**
         * Returns an instance of the renderer the builder is made for.
         *
         * @implNote The {@link this#check() check method} of this class as
         *           well as any other checks to avoid illegal settings should be called before building.
         * @return a never-null instance.
         */
        @NotNull
        public abstract T build();

        /**
         * Checks whether the precondition is null. Should be called when building instances of {@link AbstractMapRenderer}.
         */
        protected final void check() {
            Checks.checkNotNull(precondition, "Precondition");
        }

        /**
         * Adds a {@link Collection} of players to the set of players this renderer will apply for.
         *
         * Not adding any players will result in a renderer that renders the map for every player.
         *
         * @param players a collection of players. May be empty.
         * @return this.
         */
        @NotNull
        public U addPlayers(@NotNull Collection<Player> players) {
            this.receivers.addAll(players);
            return (U) this;
        }

        /**
         * Adds zero or more players for whom this renderer should apply.
         *
         * Not adding any players will result in a renderer that renders the map for every player.
         *
         * @param players 0-n players or an array of players to add.
         * @return this.
         */
        @NotNull
        public U addPlayers(@NotNull Player... players) {
            return this.addPlayers(Arrays.asList(players));
        }

        /**
         * Sets a {@link Predicate} that will be tested before every attempt to render the map.
         * If the test fails, this renderer will not render the map.
         *
         * This is an optional setting, the default value is {@code (context} -> true}.
         *
         * @param precondition a {@link Predicate} that tests a {@link RenderContext}.
         * @return this.
         */
        @NotNull
        public U precondition(@NotNull Predicate<RenderContext> precondition) {
            this.precondition = precondition;
            return (U) this;
        }
    }
}
