package com.github.johnnyjayjay.spigotmaps.rendering;

import org.bukkit.entity.Player;
import org.bukkit.map.MapFont;

import java.awt.Point;
import java.util.Set;
import java.util.function.Predicate;

/**
 * An implementation of {@link TextRenderer} that can be used to render text on a map.
 *
 * @author Johnny_JayJay (https://www.github.com/JohnnyJayJay)
 * @see Builder
 */
public class SimpleTextRenderer extends TextRenderer {

    private SimpleTextRenderer(
            Point startingPoint,
            Set<Player> receivers,
            Predicate<RenderContext> precondition,
            boolean renderOnce,
            String text,
            MapFont font
    ) {
        super(startingPoint, receivers, renderOnce, precondition, text, font);
    }

    @Override
    protected void render(RenderContext context) {
        context.getCanvas().drawText(startingPoint.x, startingPoint.y, font, text.toString());
    }

    /**
     * Creates a {@link SimpleTextRenderer} that renders the given lines of text onto a map.
     *
     * @param lines 0-n Strings or an array of Strings to use as the lines. Must not be {@code null}.
     * @return a new, never-null instance of {@link SimpleTextRenderer}.
     */
    public static SimpleTextRenderer create(String... lines) {
        return builder().addLines(lines).build();
    }

    /**
     * Creates and returns a new instance of this class' {@link Builder}.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * A builder class used to create instances of the enclosing {@link SimpleTextRenderer} class.
     *
     * @author Johnny_JayJay (https://github.com/johnnyjayjay)
     * @see #builder()
     */
    public static class Builder extends TextRenderer.Builder<SimpleTextRenderer, Builder> {

        private Builder() {
        }

        /**
         * Builds a new instance of {@link SimpleTextRenderer} based on the settings made.
         *
         * @return a never-null instance of {@link SimpleTextRenderer}.
         * @throws IllegalArgumentException if:
         *                                  <ul>
         *                                  <li>The precondition is {@code null}</li>
         *                                  <li>The font is {@code null}</li>
         *                                  <li>The starting point is {@code null}</li>
         *                                  <li>The starting point's coordinates are not positive</li>
         *                                  <li>The starting point's coordinates are out of the minecraft map size bounds</li>
         *                                  </ul>
         */
        @Override
        public SimpleTextRenderer build() {
            super.check();
            return new SimpleTextRenderer(startingPoint, receivers, precondition, renderOnce, text.toString(), font);
        }
    }
}
