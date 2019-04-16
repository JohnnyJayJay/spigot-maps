package com.github.johnnyjayjay.mapimages;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author Johnny_JayJay (https://www.github.com/JohnnyJayJay)
 */
public class ImageTools {

    public static final Dimension MINECRAFT_MAP_SIZE = new Dimension(128, 128);

    @Nullable
    public static BufferedImage loadWithUserAgentFrom(@NotNull URL url) throws IOException {
        URLConnection connection = url.openConnection();
        connection.addRequestProperty("User-Agent", "Mozilla/5.0");
        try (InputStream inputStream = connection.getInputStream()) {
            return ImageIO.read(inputStream);
        }
    }

    @NotNull
    public static BufferedImage resizeToMapSize(@NotNull BufferedImage image) {
        Dimension size = MINECRAFT_MAP_SIZE;
        BufferedImage resized = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = resized.createGraphics();
        graphics.drawImage(image, 0, 0, size.width, size.height, null);
        graphics.dispose();
        return resized;
    }

    @NotNull
    public static BufferedImage[][] resizeIntoMapSizedParts(@NotNull BufferedImage image, boolean crop) {
        Dimension partSize = MINECRAFT_MAP_SIZE;
        BufferedImage squared = scaleToMapDividableSquare(image);
        int linearParts = squared.getWidth() / partSize.width;
        BufferedImage[][] result = new BufferedImage[linearParts][linearParts];
        for (int i = 0; i < linearParts; i++) {
            for (int j = 0; j < linearParts; j++) {
                result[j][i] = (squared.getSubimage(partSize.width * j, partSize.height * i, partSize.width, partSize.height));
            }
        }
        return result;
    }

    @NotNull
    public static BufferedImage scaleToMapDividableSquare(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int measure = width > height ? width + (width % MINECRAFT_MAP_SIZE.width) : height + (height % MINECRAFT_MAP_SIZE.height);
        BufferedImage squared = new BufferedImage(measure, measure, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = squared.createGraphics();
        graphics.drawImage(image, 0, 0, squared.getWidth(), squared.getHeight(), null);
        graphics.dispose();
        return squared;
    }

    @NotNull
    public static BufferedImage cropToMapDividableSquare(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int measure = width > height
                ? MINECRAFT_MAP_SIZE.width * (width / MINECRAFT_MAP_SIZE.width)
                : MINECRAFT_MAP_SIZE.height * (height / MINECRAFT_MAP_SIZE.height);
        return copyOf(image).getSubimage(width - measure / 2, height - measure / 2, measure, measure);
    }

    @NotNull
    public static BufferedImage copyOf(BufferedImage image) {
        BufferedImage copy = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        Graphics2D graphics = copy.createGraphics();
        graphics.drawImage(image, 0, 0, null);
        graphics.dispose();
        return copy;
    }


}
