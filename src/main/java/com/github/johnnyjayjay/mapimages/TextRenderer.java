package com.github.johnnyjayjay.mapimages;

import org.bukkit.entity.Player;
import org.bukkit.map.MapFont;
import org.bukkit.map.MinecraftFont;
import org.jetbrains.annotations.NotNull;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

/**
 * An implementation of {@link AbstractMapRenderer} that can be used to render text on a map.
 *
 * @see Builder
 * @author Johnny_JayJay (https://www.github.com/JohnnyJayJay)
 */
public class TextRenderer extends AbstractMapRenderer {

    private final String text;
    private final Point startingPoint;
    private final MapFont font;

    private TextRenderer(
            Set<Player> receivers,
            Predicate<RenderContext> precondition,
            String text,
            Point startingPoint,
            MapFont font) {
        super(receivers, precondition);
        this.text = text;
        this.startingPoint = startingPoint;
        this.font = font;
    }

    @Override
    protected void render(RenderContext context) {
        context.getCanvas().drawText(startingPoint.x, startingPoint.y, font, text);
    }

    /**
     * Returns the text that this renderer renders, including new lines {@code \n}.
     */
    @NotNull
    public String getText() {
        return text;
    }

    /**
     * Returns a copy of the point where the renderer begins to render text on a map.
     */
    @NotNull
    public Point getStartingPoint() {
        return new Point(startingPoint);
    }

    /**
     * Returns the font used to render the given text.
     */
    @NotNull
    public MapFont getFont() {
        return font;
    }

    /**
     * Creates and returns a new instance of this class' {@link Builder}.
     */
    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    /**
     * A builder class used to create instances of the enclosing {@link TextRenderer} class.
     *
     * @author Johnny_JayJay (https://github.com/johnnyjayjay)
     */
    public static class Builder extends AbstractMapRenderer.Builder<TextRenderer, Builder> {

        private List<String> lines = new ArrayList<>();
        private Point startingPoint = new Point();
        private MapFont font = MinecraftFont.Font;

        private Builder() {}

        /**
         * Builds a new instance of {@link TextRenderer}.
         *
         * @throws IllegalArgumentException if:
         * <ul>
         *     <li>The precondition is {@code null}</li>
         *     <li>The font is {@code null}</li>
         *     <li>The starting point is {@code null}</li>
         *     <li>The starting point's coordinates are not positive</li>
         * </ul>
         * @return a never-null instance of {@link TextRenderer}.
         */
        @NotNull
        @Override
        public TextRenderer build() {
            super.check();
            Checks.checkNotNull(font, "Font");
            Checks.checkNotNull(startingPoint, "Starting point");
            Checks.check(startingPoint.x >= 0 && startingPoint.y >= 0, "Negative coordinates are not allowed");
            return new TextRenderer(receivers, precondition, String.join("\n", lines), startingPoint, font);
        }

        /**
         * Adds a {@link List} of lines (Strings) to the text this renderer will draw.
         *
         * Not adding any lines is a valid option and will result in a map without text.
         *
         * @param lines a list of Strings. Must not be {@code null}.
         * @return this.
         */
        @NotNull
        public Builder addLines(@NotNull List<String> lines) {
            this.lines.addAll(lines);
            return this;
        }

        /**
         * Adds zero or more lines (Strings) to the text this renderer will draw
         *
         * Not adding any lines is a valid option and will result in a map without text.
         *
         * @param lines 0-n Strings or an array of Strings. Must not be {@code null}.
         * @return this.
         */
        @NotNull
        public Builder addLines(@NotNull String... lines) {
            return addLines(Arrays.asList(lines));
        }

        /**
         * Sets the coordinates (via a {@link Point} determining where to begin writing the text on the map.
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

        /**
         * Sets the font to use for the text.
         *
         * This is not required. By default, it is set to {@link MinecraftFont#Font}.
         *
         * @param font a {@link MapFont} to use as a font for the text.
         * @return this.
         */
        @NotNull
        public Builder font(@NotNull MapFont font) {
            this.font = font;
            return this;
        }
    }
}
