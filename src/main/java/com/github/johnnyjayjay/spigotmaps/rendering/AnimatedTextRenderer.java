package com.github.johnnyjayjay.spigotmaps.rendering;

import com.github.johnnyjayjay.spigotmaps.util.Checks;
import org.bukkit.entity.Player;
import org.bukkit.map.MapFont;

import java.awt.Point;
import java.util.Set;
import java.util.function.Predicate;

/**
 * An implementation of {@link TextRenderer} that renders texts onto a map character by character.
 * <p>
 * This class is not thread safe and will stop rendering automatically once the given text has fully rendered.
 *
 * @author Johnny_JayJay (https://www.github.com/JohnnyJayJay)
 * @see Builder
 */
public class AnimatedTextRenderer extends TextRenderer {

    private StringBuilder renderedText;
    private int currentChar;
    private int charsPerSecond;
    private long ticksToWait;

    private AnimatedTextRenderer(
            Point startingPoint,
            Set<Player> receivers,
            Predicate<RenderContext> precondition,
            CharSequence text,
            MapFont font,
            int charsPerSecond,
            int tickDelay
    ) {
        super(startingPoint, receivers, false, precondition, text, font);
        this.charsPerSecond = charsPerSecond;
        this.currentChar = 0;
        this.renderedText = new StringBuilder();
        this.ticksToWait = tickDelay + 1;
    }

    private double calculateTicksToWait() {
        double charsPerTick = charsPerSecond / 20D;
        return (currentChar + 1) / charsPerTick - currentChar / charsPerTick;
    }

    private int charsToAppend(double ticksToWait) {
        return ticksToWait < 1
                ? (int) (1 / ticksToWait)
                : 1;
    }

    @Override
    protected void render(RenderContext context) {
        if (--ticksToWait > 0)
            return;

        if (currentChar >= text.length()) {
            stopRendering();
        } else {
            double ticksToWait = calculateTicksToWait();
            this.ticksToWait = ticksToWait < 1 ? 1 : Math.round(ticksToWait);
            for (int destinationLength = charsToAppend(ticksToWait) + currentChar;
                 currentChar < destinationLength; currentChar++) {
                renderedText.append(text.charAt(currentChar));
            }
            context.getCanvas().drawText(startingPoint.x, startingPoint.y, font, renderedText.toString());
        }
    }

    /**
     * Returns how many characters are rendered each second by this class.
     */
    public int getCharsPerSecond() {
        return charsPerSecond;
    }

    /**
     * Sets how many characters are rendered each second.
     *
     * @param charsPerSecond the new amount.
     * @throws IllegalArgumentException if the argument is not positive.
     */
    public void setCharsPerSecond(int charsPerSecond) {
        Checks.check(charsPerSecond > 0, "Chars per second must be positive");
        this.charsPerSecond = charsPerSecond;
    }

    /**
     * Creates and returns a new instance of this class' {@link Builder}.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * A builder class used to create instances of the enclosing {@link AnimatedTextRenderer} class.
     *
     * @author Johnny_JayJay (https://github.com/johnnyjayjay)
     * @see #builder()
     */
    public static class Builder extends TextRenderer.Builder<AnimatedTextRenderer, Builder> {

        private int charsPerSecond = 20;
        private int delay = 0;

        private Builder() {
        }

        @Override
        public AnimatedTextRenderer build() {
            super.check();
            Checks.check(charsPerSecond > 0, "Chars per second must be positive");
            Checks.check(delay >= 0, "Delay must not be negative");
            return new AnimatedTextRenderer(startingPoint, receivers, precondition, text, font, charsPerSecond, delay);
        }

        /**
         * Sets the amount of characters that should be rendered each second.
         * <p>
         * This is optional. The default value is 20, i.e. 20 characters will be rendered each second.
         *
         * @param amount a positive int amount.
         * @return this.
         */
        public Builder charsPerSecond(int amount) {
            this.charsPerSecond = amount;
            return this;
        }

        /**
         * Sets a delay in ticks that should be applied to this renderer, i.e.
         * it will delay the animation for the time of this delay at the beginning.
         * <p>
         * This is optional. The default value is 0, i.e. there won't be a delay.
         *
         * @param ticks the amount of ticks to wait (20 ticks = 1 second).
         * @return this.
         */
        public Builder delay(int ticks) {
            this.delay = ticks;
            return this;
        }

        /**
         * Not a supported operation, because every AnimatedTextRenderer MUST render more than once and this
         * value can therefore not be set individually.
         *
         * @throws UnsupportedOperationException always.
         */
        @Override
        public Builder renderOnce(boolean renderOnce) {
            throw new UnsupportedOperationException("renderOnce is always false for AnimatedTextRenderers and thus not allowed to be set");
        }
    }
}
