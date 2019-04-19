package com.github.johnnyjayjay.spigotmaps.rendering;

import com.github.johnnyjayjay.spigotmaps.Checks;
import com.github.johnnyjayjay.spigotmaps.ImageTools;
import org.bukkit.entity.Player;
import org.bukkit.map.MapFont;
import org.bukkit.map.MinecraftFont;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

/**
 * An implementation of {@link AbstractMapRenderer} that can be used to render text on a map.
 *
 * @author Johnny_JayJay (https://www.github.com/JohnnyJayJay)
 * @see Builder
 */
public class TextRenderer extends AbstractMapRenderer {

    private String text;
    private MapFont font;

    private TextRenderer(
            Set<Player> receivers,
            Predicate<RenderContext> precondition,
            boolean renderOnce,
            String text,
            Point startingPoint,
            MapFont font
    ) {
        super(startingPoint, receivers, renderOnce, precondition);
        this.text = text;
        this.font = font;
    }

    @Override
    protected void render(RenderContext context) {
        context.getCanvas().drawText(startingPoint.x, startingPoint.y, font, text);
    }

    /**
     * Returns the text that this renderer renders, including new lines {@code \n}.
     */
    public String getText() {
        return text;
    }

    /**
     * Returns the font used to render the given text.
     */
    public MapFont getFont() {
        return font;
    }

    /**
     * Sets the text rendered by this renderer.
     *
     * @param text a new text String. New lines must be included if needed.
     * @throws IllegalArgumentException if the argument is {@code null}.
     */
    public void setText(String text) {
        Checks.checkNotNull(text, "Text");
        this.text = text;
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
     * Creates a {@link TextRenderer} that renders the given lines of text onto a map.
     *
     * @param lines 0-n Strings or an array of Strings to use as the lines. Must not be {@code null}.
     * @return a new, never-null instance of {@link TextRenderer}.
     */
    public static TextRenderer create(String... lines) {
        return builder().addLines(lines).build();
    }

    /**
     * Creates and returns a new instance of this class' {@link Builder}.
     */
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

        private Builder() {
        }

        /**
         * Builds a new instance of {@link TextRenderer}.
         *
         * @return a never-null instance of {@link TextRenderer}.
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
        public TextRenderer build() {
            super.check();
            Checks.checkNotNull(font, "Font");
            return new TextRenderer(receivers, precondition, renderOnce, String.join("\n", lines), startingPoint, font);
        }

        /**
         * Adds a {@link List} of lines (Strings) to the text this renderer will draw.
         * <p>
         * Not adding any lines is a valid option and will result in a map without text.
         *
         * @param lines a list of Strings. Must not be {@code null}.
         * @return this.
         */
        public Builder addLines(List<String> lines) {
            this.lines.addAll(lines);
            return this;
        }

        /**
         * Adds zero or more lines (Strings) to the text this renderer will draw
         * <p>
         * Not adding any lines is a valid option and will result in a map without text.
         *
         * @param lines 0-n Strings or an array of Strings. Must not be {@code null}.
         * @return this.
         */
        public Builder addLines(String... lines) {
            return addLines(Arrays.asList(lines));
        }

        /**
         * Sets the font to use for the text.
         * <p>
         * This is not required. By default, it is set to {@link MinecraftFont#Font}.
         *
         * @param font a {@link MapFont} to use as a font for the text.
         * @return this.
         */
        public Builder font(MapFont font) {
            this.font = font;
            return this;
        }
    }
}
