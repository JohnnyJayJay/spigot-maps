package com.github.johnnyjayjay.spigotmaps.rendering;

import com.github.johnnyjayjay.spigotmaps.Checks;
import org.bukkit.entity.Player;

import java.awt.Point;
import java.util.Set;
import java.util.function.Predicate;

/**
 * TODO documentation
 * @author Johnny_JayJay (https://www.github.com/JohnnyJayJay)
 */
public class GifRenderer extends AbstractMapRenderer {

    public static final int REPEAT_FOREVER = -1;

    private final GifImage image;

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
    }

    private int msToTicks(int millis) {
        return millis / 1000 * 20;
    }

    @Override
    protected void render(RenderContext context) {
        if (ticksToWait-- > 0)
            return;

        if (currentFrame + 1 >= image.getFrameCount()) {
            currentFrame = 0;
            if (--toRepeat == 0) {
                this.stopRendering();
                return;
            }
        }

        GifImage.Frame frame = image.get(currentFrame++);
        context.getCanvas().drawImage(startingPoint.x, startingPoint.y, frame.getImage());
        ticksToWait = msToTicks(frame.getMsDelay());
    }


    public int getToRepeat() {
        return toRepeat;
    }

    public int getCurrentFrame() {
        return currentFrame;
    }

    public void setFrame(int frame) {
        Checks.checkBounds(frame, 0, image.getFrameCount(), "Frame index");
        this.currentFrame = frame;
    }

    public static class Builder extends AbstractMapRenderer.Builder<GifRenderer, Builder> {

        private GifImage gifImage = null;
        private int startFrame = 0;
        private int repeat = REPEAT_FOREVER;

        @Override
        public GifRenderer build() {
            renderOnce = false;
            super.check();
            Checks.checkNotNull(gifImage, "GIF image");
            Checks.checkBounds(startFrame, 0, gifImage.getFrameCount(), "Frame index");
            return new GifRenderer(startingPoint, receivers, precondition, gifImage, startFrame, repeat);
        }

        public Builder gif(GifImage gifImage) {
            this.gifImage = gifImage;
            return this;
        }

        public Builder startAt(int frame) {
            this.startFrame = frame;
            return this;
        }

        public Builder repeat(int times) {
            this.repeat = times;
            return this;
        }

        /**
         * Not a supported operation, as a GifRenderer MUST render more than once.
         */
        @Override
        public Builder renderOnce(boolean renderOnce) {
            throw new UnsupportedOperationException("renderOnce is always false for GifRenderers and thus not allowed to be set");
        }
    }
}
