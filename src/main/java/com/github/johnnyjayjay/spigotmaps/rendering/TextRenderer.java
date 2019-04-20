package com.github.johnnyjayjay.spigotmaps.rendering;

import com.github.johnnyjayjay.spigotmaps.util.Checks;
import org.bukkit.entity.Player;
import org.bukkit.map.MapFont;
import org.bukkit.map.MinecraftFont;

import java.awt.Point;
import java.util.Set;
import java.util.function.Predicate;

/**
 * @author Johnny_JayJay (https://www.github.com/JohnnyJayJay)
 */
public abstract class TextRenderer extends AbstractMapRenderer {

    protected CharSequence text;
    protected MapFont font;

    protected TextRenderer(
            Point startingPoint,
            Set<Player> receivers,
            boolean renderOnce,
            Predicate<RenderContext> precondition,
            CharSequence text,
            MapFont font
    ) {
        super(startingPoint, receivers, renderOnce, precondition);
        this.text = text;
        this.font = font;
    }

    /**
     * Returns the text that this renderer renders, including new lines {@code \n}.
     */
    public String getText() {
        return text.toString();
    }

    /**
     * Sets the text rendered by this renderer.
     *
     * @param text a new text String. New lines must be included if needed.
     * @throws IllegalArgumentException if the argument is {@code null}.
     */
    public void setText(CharSequence text) {
        Checks.checkNotNull(text, "Text");
        this.text = text;
    }

    /**
     * Returns the font used to render the given text.
     */
    public MapFont getFont() {
        return font;
    }

    /**
     * Sets the font the rendered text should use.
     *
     * @param font a new MapFont.
     * @throws IllegalArgumentException if the argument is {@code null}.
     */
    public void setFont(MapFont font) {
        Checks.checkNotNull(font, "Font");
        this.font = font;
    }

    /**
     * A base builder class for every extension of {@link TextRenderer}.
     *
     * @author Johnny_JayJay (https://github.com/johnnyjayjay)
     */
    protected static abstract class Builder<T extends TextRenderer, U extends Builder<T, U>>
            extends AbstractMapRenderer.Builder<T, U> {

        protected final StringBuilder text = new StringBuilder();
        protected MapFont font = MinecraftFont.Font;

        /**
         * Makes the checks from {@link AbstractMapRenderer.Builder#check()} and additionally checks if the font is {@code null}.
         */
        @Override
        protected void check() {
            super.check();
            Checks.checkNotNull(font, "Font");
        }

        /**
         * Adds zero or more lines (Strings) to the text this renderer will draw
         * <p>
         * Not adding any lines is a valid option and will result in a map without text.
         *
         * @param lines 0-n Strings or an array of Strings. Must not be {@code null}.
         * @return this.
         */
        public U addLines(CharSequence... lines) {
            for (CharSequence line : lines)
                text.append(line).append('\n');
            return (U) this;
        }

        /**
         * Adds an {@link Iterable} of lines (Strings) to the text this renderer will draw.
         * <p>
         * Not adding any lines is a valid option and will result in a map without text.
         *
         * @param lines an Iterable of Strings, e.g. a List. Must not be {@code null}.
         * @return this.
         */
        public U addLines(Iterable<? extends CharSequence> lines) {
            for (CharSequence line : lines)
                text.append(line).append('\n');
            return (U) this;
        }

        /**
         * Adds a {@link CharSequence} to the text this renderer will draw.
         *
         * @param text A CharSequence, e.g. a String to add. This must include new line characters
         *             if the result should be multiple lines.
         * @return this.
         */
        public U addText(CharSequence text) {
            this.text.append(text);
            return (U) this;
        }

        /**
         * Sets the font to use for the text.
         * <p>
         * This is not required. By default, it is set to {@link MinecraftFont#Font}.
         *
         * @param font a {@link MapFont} to use as a font for the text.
         * @return this.
         */
        public U font(MapFont font) {
            this.font = font;
            return (U) this;
        }
    }
}
