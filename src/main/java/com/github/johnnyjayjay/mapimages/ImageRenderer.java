package com.github.johnnyjayjay.mapimages;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapView;
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

    private ImageRenderer(Set<Player> receivers, Predicate<MapView> precondition, BufferedImage image) {
        super(receivers, precondition);
        this.image = image;
    }

    @Override
    protected void render(MapView map, MapCanvas canvas) {
        canvas.drawImage(0, 0, image);
    }


    public BufferedImage getImage() {
        return image;
    }

    @NotNull
    public static ImageRenderer createSingleColorRenderer(Color color, Player... players) {
        BufferedImage image = new BufferedImage(ImageTools.MINECRAFT_MAP_SIZE.width, ImageTools.MINECRAFT_MAP_SIZE.height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setPaint(color);
        graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
        graphics.dispose();
        return builder().image(image).addPlayers(players).build();
    }

    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends AbstractMapRenderer.Builder<ImageRenderer, Builder> {

        private BufferedImage image = null;

        private Builder() {}

        @NotNull
        @Override
        public ImageRenderer build() {
            super.check();
            Checks.checkNotNull(image, "Image");
            return new ImageRenderer(receivers, precondition, image);
        }

        @NotNull
        public Builder image(@NotNull BufferedImage image) {
            this.image = image;
            return this;
        }
    }
}
