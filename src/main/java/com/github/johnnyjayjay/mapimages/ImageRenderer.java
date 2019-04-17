package com.github.johnnyjayjay.mapimages;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Set;
import java.util.function.Predicate;


/**
 * @author Johnny_JayJay (https://www.github.com/JohnnyJayJay)
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

        private Builder() {}

        /**
         * Creates a new {@link ImageRenderer}.
         *
         * @throws IllegalArgumentException if
         * <ul>
         *     <li>The precondition is {@code null}</li>
         *     <li>The starting point is {@code null}</li>
         *     <li>The image is {@code null}</li>
         * </ul>
         * @return a new instance of {@link ImageRenderer}.
         */
        @NotNull
        @Override
        public ImageRenderer build() {
            super.check();
            Checks.checkNotNull(startingPoint, "Starting point");
            Checks.checkNotNull(image, "Image");
            return new ImageRenderer(receivers, precondition, image, startingPoint);
        }

        /**
         * Sets the image that should be rendered onto the map by this renderer.
         *
         * This makes a defensive copy of the {@link BufferedImage}, so changes to the argument will not
         * have any effect on this instance.
         *
         * This is a required setting.
         *
         * @see ImageTools
         * @param image the {@link BufferedImage} to draw.
         * @return this.
         */
        @NotNull
        public Builder image(@NotNull BufferedImage image) {
            this.image = ImageTools.copyOf(image);
            return this;
        }

        /**
         * Sets the coordinates (via a {@link Point} determining where to begin drawing the image on the map.
         *
         * This makes a defensive copy of the {@link Point}, so changes to the argument will not have any
         * effect on this instance.
         *
         * This is not required. By default, it will start drawing from the upper left corner (0, 0).
         *
         * @see ImageTools#MINECRAFT_MAP_SIZE
         * @param point a {@link Point} representing the coordinates, i.e. where to begin drawing.
         * @return this.
         */
        @NotNull
        public Builder startingPoint(@NotNull Point point) {
            this.startingPoint = new Point(point);
            return this;
        }
    }
}
