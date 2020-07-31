package com.github.johnnyjayjay.spigotmaps.rendering;

import com.github.johnnyjayjay.spigotmaps.util.Checks;
import com.madgag.gif.fmsware.GifDecoder;
import org.bukkit.entity.Player;

import java.awt.Point;
import java.util.Set;
import java.util.function.Predicate;

/**
 * An implementation of {@link AbstractMapRenderer} that is able to render animated gifs.
 *
 * @see Builder
 * @author Johnny_JayJay (https://www.github.com/JohnnyJayJay)
 */
public class GifRenderer extends AbstractMapRenderer {

    /**
     * A constant that can be used instead of magic values for {@link Builder#repeat(int)}. Value: -1.
     */
    public static final int REPEAT_FOREVER = -1;

    private final GifImage image;
    private final boolean repeatForever;

    private int currentFrame;
    private int toRepeat;
    private int ticksToWait;

    private GifRenderer(
            Point startingPoint,
            Set<Player> receivers,
            Predicate<RenderContext> precondition,
            GifImage image,
            int startFrame,
            int repeat
    ) {
        super(startingPoint, receivers, false, precondition);
        this.image = image;
        this.currentFrame = startFrame;
        this.toRepeat = repeat;
        this.ticksToWait = msToTicks(image.get(currentFrame).getMsDelay());
        this.repeatForever = toRepeat < 0;
    }

    private int msToTicks(int millis) {
        return millis / 1000 * 20;
    }

    @Override
    protected void render(RenderContext context) {
        if (ticksToWait-- > 0)
            return;

        if (currentFrame >= image.getFrameCount()) {
            currentFrame = 0;
            if (!repeatForever && --toRepeat == 0) {
                this.stopRendering();
                return;
            }
        }

        GifImage.Frame frame = image.get(currentFrame++);
        context.getCanvas().drawImage(startingPoint.x, startingPoint.y, frame.getImage());
        ticksToWait = msToTicks(frame.getMsDelay());
    }

    /**
     * Returns the {@link GifImage} used by this renderer.
     *
     * @return the image
     */
    public GifImage getImage() {
        return image;
    }

    /**
     * Returns how often the gif will still repeat itself or {@link #REPEAT_FOREVER} if it repeats indefinitely.
     */
    public int getToRepeat() {
        return repeatForever ? REPEAT_FOREVER : toRepeat;
    }

    /**
     * Returns the index of the {@link GifImage.Frame} this renderer is currently at.
     */
    public int getCurrentFrame() {
        return currentFrame;
    }

    /**
     * Sets the index of the {@link GifImage.Frame} this renderer will render next.
     *
     * @param frame the frame to set.
     * @throws IllegalArgumentException if the index is out of bounds.
     */
    public void setFrame(int frame) {
        Checks.checkBounds(frame, 0, image.getFrameCount(), "Frame index");
        this.currentFrame = frame;
    }

    /**
     * Creates a new {@link GifRenderer} that renders a specific gif for the specified players
     * or everyone if none are specified.
     *
     * @param image   the gif to render.
     * @param players the players to render for. Must not be {@code null}.
     * @return a never-null instance of {@link GifRenderer}.
     */
    public static GifRenderer create(GifImage image, Player... players) {
        return builder().gif(image).addPlayers(players).build();
    }

    /**
     * Creates and returns a new instance of this class' {@link Builder}.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * A builder class used to create instances of the enclosing {@link GifRenderer} class.
     *
     * @author Johnny_JayJay (https://github.com/johnnyjayjay)
     */
    public static class Builder extends AbstractMapRenderer.Builder<GifRenderer, Builder> {

        private GifImage gifImage = null;
        private int startFrame = 0;
        private int repeat = REPEAT_FOREVER;

        private Builder() {}

        /**
         * Builds a new instance of {@link ImageRenderer} based on the settings made.
         *
         * @return a new instance of {@link ImageRenderer}.
         * @throws IllegalArgumentException if
         *                                  <ul>
         *                                  <li>The precondition is {@code null}</li>
         *                                  <li>The starting point is {@code null}</li>
         *                                  <li>The gif image is {@code null}</li>
         *                                  <li>The starting point's coordinates are not positive</li>
         *                                  <li>The starting point's coordinates are out of the minecraft map size bounds</li>
         *                                  </ul>
         */
        @Override
        public GifRenderer build() {
            super.check();
            Checks.checkNotNull(gifImage, "GIF image");
            Checks.checkBounds(startFrame, 0, gifImage.getFrameCount(), "Frame index");
            return new GifRenderer(startingPoint, receivers, precondition, gifImage, startFrame, repeat);
        }

        /**
         * Sets the {@link GifImage} this renderer should render.
         *
         * @param gifImage A {@link GifImage} which can be obtained using {@link GifImage#fromDecoder(GifDecoder)} for example.
         * @return this.
         */
        public Builder gif(GifImage gifImage) {
            this.gifImage = gifImage;
            return this;
        }

        /**
         * Sets the frame this renderer should start at.
         *
         * @param frame the index of the frame to start at. Must be in bounds of the {@link GifImage} set.
         * @return this.
         */
        public Builder startAt(int frame) {
            this.startFrame = frame;
            return this;
        }

        /**
         * Sets how often the animated gif should be repeated.
         *
         * @param times the amount of repetitions or a negative value (e.g. {@link #REPEAT_FOREVER}) if it should repeat indefinitely.
         * @return this.
         */
        public Builder repeat(int times) {
            this.repeat = times;
            return this;
        }

        /**
         * Not a supported operation, because every GifRenderer MUST render more than once and this
         * value can therefore not be set individually.
         *
         * @throws UnsupportedOperationException always.
         */
        @Override
        public Builder renderOnce(boolean renderOnce) {
            throw new UnsupportedOperationException("renderOnce is always false for GifRenderers and thus not allowed to be set");
        }
    }
}
