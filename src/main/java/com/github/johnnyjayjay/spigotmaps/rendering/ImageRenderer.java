package com.github.johnnyjayjay.spigotmaps.rendering;

import com.github.johnnyjayjay.spigotmaps.Checks;
import com.github.johnnyjayjay.spigotmaps.ImageTools;
import org.bukkit.entity.Player;

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

    private BufferedImage image;

    private ImageRenderer(
            Set<Player> receivers,
            Predicate<RenderContext> precondition,
            boolean renderOnce,
            BufferedImage image,
            Point startingPoint
    ) {
        super(startingPoint, receivers, renderOnce, precondition);
        this.image = image;
    }

    @Override
    protected void render(RenderContext context) {
        context.getCanvas().drawImage(startingPoint.x, startingPoint.y, image);
    }

    /**
     * Returns the {@link BufferedImage} used by this renderer.
     */
    public BufferedImage getImage() {
        return image;
    }

    /**
     * Sets the image used by this renderer.
     *
     * @param image a BufferedImage to be rendered onto maps.
     * @throws IllegalArgumentException if the argument is {@code null}.
     */
    public void setImage(BufferedImage image) {
        Checks.checkNotNull(image, "Image");
        this.image = image;
    }

    /**
     * Creates a new {@link ImageRenderer} that renders a specific image for the specified players
     * or everyone if none are specified..
     *
     * @param image   the image to render.
     * @param players the players to render for. Must not be {@code null}.
     * @return a never-null instance of {@link ImageRenderer}.
     */
    public static ImageRenderer create(BufferedImage image, Player... players) {
        return builder().image(image).addPlayers(players).build();
    }

    /**
     * Creates a new {@link ImageRenderer} that renders a single color onto a map for the specified players
     * or everybody if none are specified.
     *
     * @param color   the color to use.
     * @param players the players to render for. Must not be {@code null}.
     * @return a never-null instance of {@link ImageRenderer}.
     */
    public static ImageRenderer createSingleColorRenderer(Color color, Player... players) {
        return builder().image(ImageTools.createSingleColoredImage(color)).addPlayers(players).build();
    }

    /**
     * Creates and returns a new instance of this class' {@link Builder}.
     */
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
        @Override
        public ImageRenderer build() {
            super.check();
            Checks.checkNotNull(image, "Image");
            return new ImageRenderer(receivers, precondition, renderOnce, image, startingPoint);
        }

        /**
         * Sets the image that should be rendered onto the map by this renderer.
         * <p>
         * This is a required setting.
         *
         * @param image the non-{@code null} {@link BufferedImage} to draw.
         * @return this.
         * @see ImageTools
         */
        public Builder image(BufferedImage image) {
            this.image = image;
            return this;
        }
    }
}
