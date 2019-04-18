package com.github.johnnyjayjay.spigotmaps;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Set;
import java.util.function.Predicate;


/**
 * An implementation of {@link AbstractMapRenderer} that renders an image onto a map.
 *
 * @author Johnny_JayJay (https://www.github.com/JohnnyJayJay)
 * @see Builder
 */
public class ImageRenderer extends AbstractMapRenderer {

    private final BufferedImage image;
    private final Point startingPoint;

    private ImageRenderer(Set<Player> receivers, Predicate<RenderContext> precondition, BufferedImage image, Point startingPoint) {
        super(receivers, precondition);
        this.image = image;
        this.startingPoint = startingPoint;
    }

    @Override
    protected void render(RenderContext context) {
        context.getCanvas().drawImage(startingPoint.x, startingPoint.y, image);
    }

    /**
     * Returns a copy of the {@link BufferedImage} used by this renderer.
     */
    @NotNull
    public BufferedImage getImage() {
        return ImageTools.copyOf(image);
    }

    /**
     * Returns a copy of the point where the renderer begins to render text on a map.
     */
    @NotNull
    public Point getStartingPoint() {
        return new Point(startingPoint);
    }

    /**
     * Creates a new {@link ImageRenderer} that renders a specific image for the specified players
     * or everyone if none are specified..
     *
     * @param image   the image to render.
     * @param players the players to render for.
     * @return a never-null instance of {@link ImageRenderer}.
     */
    @NotNull
    public static ImageRenderer create(@NotNull BufferedImage image, @NotNull Player... players) {
        return builder().image(image).addPlayers(players).build();
    }

    /**
     * Creates a new {@link ImageRenderer} that renders a single color onto a map for the specified players
     * or everybody if none are specified.
     *
     * @param color   the color to use.
     * @param players the players to render for.
     * @return a never-null instance of {@link ImageRenderer}.
     */
    @NotNull
    public static ImageRenderer createSingleColorRenderer(Color color, Player... players) {
        return builder().image(ImageTools.createSingleColoredImage(color)).addPlayers(players).build();
    }

    /**
     * Creates and returns a new instance of this class' {@link Builder}.
     */
    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    /**
     * A builder class used to create instances of the enclosing {@link ImageRenderer} class.
     *
     * @author Johnny_JayJay (https://github.com/johnnyjayjay)
     */
    public static class Builder extends AbstractMapRenderer.Builder<ImageRenderer, Builder> {

        private BufferedImage image = null;
        private Point startingPoint = new Point();

        private Builder() {
        }

        /**
         * Creates a new {@link ImageRenderer}.
         *
         * @return a new instance of {@link ImageRenderer}.
         * @throws IllegalArgumentException if
         *                                  <ul>
         *                                  <li>The precondition is {@code null}</li>
         *                                  <li>The starting point is {@code null}</li>
         *                                  <li>The image is {@code null}</li>
         *                                  <li>The starting point's coordinates are not positive</li>
         *                                  <li>The starting point's coordinates are out of the minecraft map size bounds</li>
         *                                  </ul>
         */
        @NotNull
        @Override
        public ImageRenderer build() {
            super.check();
            Checks.checkNotNull(startingPoint, "Starting point");
            Checks.check(startingPoint.x >= 0 && startingPoint.y >= 0, "Negative coordinates are not allowed");
            Checks.check(startingPoint.x <= ImageTools.MINECRAFT_MAP_SIZE.width
                            && startingPoint.y <= ImageTools.MINECRAFT_MAP_SIZE.height,
                    "Starting point is out of minecraft map bounds");
            Checks.checkNotNull(image, "Image");
            return new ImageRenderer(receivers, precondition, image, startingPoint);
        }

        /**
         * Sets the image that should be rendered onto the map by this renderer.
         * <p>
         * This makes a defensive copy of the {@link BufferedImage}, so changes to the argument will not
         * have any effect on this instance.
         * <p>
         * This is a required setting.
         *
         * @param image the {@link BufferedImage} to draw.
         * @return this.
         * @see ImageTools
         */
        @NotNull
        public Builder image(@NotNull BufferedImage image) {
            this.image = ImageTools.copyOf(image);
            return this;
        }

        /**
         * Sets the coordinates (via a {@link Point} determining where to begin drawing the image on the map.
         * <p>
         * This makes a defensive copy of the {@link Point}, so changes to the argument will not have any
         * effect on this instance.
         * <p>
         * This is not required. By default, it will start drawing from the upper left corner (0, 0).
         *
         * @param point a {@link Point} representing the coordinates, i.e. where to begin drawing.
         * @return this.
         * @see ImageTools#MINECRAFT_MAP_SIZE
         */
        @NotNull
        public Builder startingPoint(@NotNull Point point) {
            this.startingPoint = new Point(point);
            return this;
        }
    }
}
