package com.github.johnnyjayjay.spigotmaps.rendering;

import com.github.johnnyjayjay.spigotmaps.Checks;
import com.github.johnnyjayjay.spigotmaps.ImageTools;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.awt.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

/**
 * The base class for {@link MapRenderer} implementations by this library.
 * <p>
 * Extending classes should provide a nested Builder class, which, in turn, extends
 * the abstract nested {@link Builder} provided by this class.
 *
 * @author Johnny_JayJay (https://www.github.com/JohnnyJayJay)
 * @see ImageRenderer
 * @see TextRenderer
 */
public abstract class AbstractMapRenderer extends MapRenderer {

    protected Point startingPoint;

    private final Set<RenderContext> alreadyReceived;
    private final boolean renderForAllPlayers, renderOnce;
    private final Set<Player> receivers;
    private final Predicate<RenderContext> precondition;

    private boolean stop;

    protected AbstractMapRenderer(
            Point startingPoint,
            Set<Player> receivers,
            boolean renderOnce,
            Predicate<RenderContext> precondition
    ) {
        super(!receivers.isEmpty());
        this.startingPoint = startingPoint;
        this.renderForAllPlayers = receivers.isEmpty();
        this.receivers = receivers;
        this.renderOnce = renderOnce;
        this.precondition = precondition;
        this.alreadyReceived = new HashSet<>(); // TODO consider an implementation with less overhead
        this.stop = false;
    }

    @Override
    public final void render(MapView map, MapCanvas canvas, Player player) {
        RenderContext context = RenderContext.create(map, canvas, player);
        if (mayRender(context)) {
            render(context);
            if (renderOnce)
                alreadyReceived.add(context);
        }
    }

    private boolean mayRender(RenderContext context) {
        return !stop
                &&(renderForAllPlayers || receivers.contains(context.getPlayer()))
                && (!renderOnce || !alreadyReceived.contains(context))
                && precondition.test(context);
    }

    /**
     * Adds a player to this renderer's receivers.
     *
     * @param receiver the player to add.
     * @throws IllegalArgumentException if the argument is {@code null}.
     */
    public void addReceiver(Player receiver) {
        Checks.checkNotNull(receiver, "Receiver");
        receivers.add(receiver);
    }

    /**
     * Removes a player from this renderer's receivers.
     *
     * @param receiver the player to remove.
     * @return {@code true} if the specified player was a receiver.
     * @throws IllegalArgumentException if the argument is {@code null}.
     */
    public boolean removeReceiver(Player receiver) {
        Checks.checkNotNull(receiver, "Receiver");
        return receivers.remove(receiver);
    }

    /**
     * Returns an immutable, unordered {@link Set} of players, which contains the receivers of this
     * renderer or an empty Set if this renderer renders for all players anyway.
     *
     * @return a Set.
     */
    public Set<Player> getReceivers() {
        return Collections.unmodifiableSet(receivers);
    }

    /**
     * Returns a copy of the point where the renderer begins to render text on a map.
     */
    public Point getStartingPoint() {
        return new Point(startingPoint);
    }

    /**
     * Sets the point on the map where this renderer should start rendering.
     *
     * @param startingPoint the point to set.
     * @throws IllegalArgumentException if the given point cannot be applied to a minecraft map or is null.
     */
    public void setStartingPoint(Point startingPoint) {
        Checks.checkStartingPoint(startingPoint);
        this.startingPoint = new Point(startingPoint);
    }

    /**
     * Returns whether this renderer only renders once for every player.
     *
     * @return {@code true}, if it only renders once.
     */
    public boolean isRenderOnce() {
        return renderOnce;
    }

    /**
     * Makes this renderer stop rendering anything, forever.
     */
    public void stopRendering() {
        this.stop = true;
    }

    /**
     * Renders the map after the preconditions have passed, i.e.:
     * <ul>
     * <li>This renderer applies to the player or</li>
     * <li>This renderer renders maps for all players</li>
     * <li>This renderer does not only render once or</li>
     * <li>This renderer has not rendered for the given context yet</li>
     * <li>The predicate's test was successful</li>
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
        protected boolean renderOnce = true;
        protected Point startingPoint = new Point();

        /**
         * Returns an instance of the renderer the builder is made for.
         * <p>
         * The {@link #check() check method} of this class as
         * well as any other checks to avoid illegal settings should be called before building instances of this class.
         *
         * @return a never-null instance.
         */
        public abstract T build();

        /**
         * Checks whether the precondition is null or the starting point doesn't fit the minecraft map size.
         * Should be called before building instances of {@link AbstractMapRenderer}.
         */
        protected final void check() {
            Checks.checkNotNull(precondition, "Precondition");
            Checks.checkStartingPoint(startingPoint);
        }

        /**
         * Adds a {@link Collection} of players to the set of players this renderer will apply for.
         * <p>
         * Not adding any players will result in a renderer that renders the map for every player.
         *
         * @param players a collection of players. May be empty, but not {@code null}.
         * @return this.
         */
        public U addPlayers(Collection<Player> players) {
            this.receivers.addAll(players);
            return (U) this;
        }

        /**
         * Adds zero or more players for whom this renderer should apply.
         * <p>
         * Not adding any players will result in a renderer that renders the map for every player.
         *
         * @param players 0-n players or an array of players to add. Must not be {@code null}.
         * @return this.
         */
        public U addPlayers(Player... players) {
            return this.addPlayers(Arrays.asList(players));
        }

        /**
         * Sets a {@link Predicate} that will be tested before every attempt to render the map.
         * If the test fails, this renderer will not render the map.
         * <p>
         * This is an optional setting, the default predicate always returns {@code true}.
         *
         * @param precondition a non-{@code null} {@link Predicate} that tests a {@link RenderContext}.
         * @return this.
         */
        public U precondition(Predicate<RenderContext> precondition) {
            this.precondition = precondition;
            return (U) this;
        }

        /**
         * Decides whether this renderer should only render once for its receivers.
         * <p>
         * This is an optional setting, the default value is {@code true}.
         *
         * @param renderOnce true if this renderer renders once for every receiver, false if otherwise.
         * @return this.
         */
        public U renderOnce(boolean renderOnce) {
            this.renderOnce = renderOnce;
            return (U) this;
        }

        /**
         * Sets the coordinates (via a {@link Point} determining where to begin drawing the image on the map.
         * <p>
         * This makes a defensive copy of the {@link Point}, so changes to the argument will not have any
         * effect on this instance.
         * <p>
         * This is not required. By default, it will start drawing from the upper left corner (0, 0).
         *
         * @param point a non-{@code null} {@link Point} representing the coordinates, i.e. where to begin drawing.
         * @return this.
         * @see ImageTools#MINECRAFT_MAP_SIZE
         */
        public U startingPoint(Point point) {
            this.startingPoint = new Point(point);
            return (U) this;
        }
    }
}
