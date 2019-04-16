package com.github.johnnyjayjay.mapimages;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapFont;
import org.bukkit.map.MapView;
import org.bukkit.map.MinecraftFont;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

/**
 * @author Johnny_JayJay (https://www.github.com/JohnnyJayJay)
 */
public class TextRenderer extends AbstractMapRenderer {

    private final String text;
    private final Point startingPoint;
    private final MapFont font;

    private TextRenderer(
            Set<Player> receivers,
            Predicate<MapView> precondition,
            String text,
            Point startingPoint,
            MapFont font) {
        super(receivers, precondition);
        this.text = text;
        this.startingPoint = startingPoint;
        this.font = font;
    }

    @Override
    protected void render(MapView map, MapCanvas canvas) {
        canvas.drawText(startingPoint.x, startingPoint.y, font, text);
    }

    @NotNull
    public String getText() {
        return text;
    }

    @NotNull
    public Point getStartingPoint() {
        return startingPoint;
    }

    @NotNull
    public MapFont getFont() {
        return font;
    }

    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends AbstractMapRenderer.Builder<TextRenderer, Builder> {

        private List<String> lines = new ArrayList<>();
        private Point startingPoint = new Point();
        private MapFont font = MinecraftFont.Font;

        private Builder() {}

        @NotNull
        @Override
        public TextRenderer build() {
            super.check();
            Checks.checkNotNull(font, "Font");
            Checks.checkNotNull(startingPoint, "Starting point");
            Checks.check(startingPoint.x >= 0 && startingPoint.y >= 0, "Negative coordinates are not allowed");
            return new TextRenderer(receivers, precondition, String.join("\n", lines), startingPoint, font);
        }

        @NotNull
        public Builder addLines(@NotNull List<String> lines) {
            this.lines.addAll(lines);
            return this;
        }

        @NotNull
        public Builder addLines(@NotNull String... lines) {
            return addLines(Arrays.asList(lines));
        }

        @NotNull
        public Builder startingPoint(@NotNull Point point) {
            this.startingPoint = point;
            return this;
        }

        @NotNull
        public Builder font(@NotNull MapFont font) {
            this.font = font;
            return this;
        }
    }
}
