package com.github.johnnyjayjay.spigotmaps;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * A utility class containing several methods to adjust and get images fitting the Minecraft map format.
 *
 * @author Johnny_JayJay (https://www.github.com/JohnnyJayJay)
 */
public class ImageTools {

    /**
     * A {@link Dimension} representing the proportions of a Minecraft map.
     */
    public static final Dimension MINECRAFT_MAP_SIZE = new Dimension(128, 128);

    /**
     * Tries to read an image from a URL using an explicit user-agent.
     * This might be useful to avoid 401 - Unauthorized responses.
     *
     * @param url a URL to fetch the image from.
     * @return the image or {@code null} if no image could be created.
     * @throws IOException see {@link ImageIO#read(URL)}.
     */
    @Nullable
    public static BufferedImage loadWithUserAgentFrom(@NotNull URL url) throws IOException {
        URLConnection connection = url.openConnection();
        connection.addRequestProperty("User-Agent", "Mozilla/5.0");
        try (InputStream inputStream = connection.getInputStream()) {
            return ImageIO.read(inputStream);
        }
    }

    /**
     * Creates a {@link BufferedImage} with the size of {@link this#MINECRAFT_MAP_SIZE}.
     * The whole image will have one color. This can be used as a background for {@link TextRenderer}s, for example.
     *
     * @param color the {@link Color} this image will have.
     * @return a never-null image.
     */
    @NotNull
    public static BufferedImage createSingleColoredImage(@NotNull Color color) {
        BufferedImage image = new BufferedImage(MINECRAFT_MAP_SIZE.width, MINECRAFT_MAP_SIZE.height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setPaint(color);
        graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
        graphics.dispose();
        return image;
    }

    /**
     * Resizes an image to the size specified in {@link this#MINECRAFT_MAP_SIZE}.
     *
     * @param image the image to resize.
     * @return a new image with the according size.
     */
    @NotNull
    public static BufferedImage resizeToMapSize(@NotNull BufferedImage image) {
        Dimension size = MINECRAFT_MAP_SIZE;
        BufferedImage resized = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = resized.createGraphics();
        graphics.drawImage(image, 0, 0, size.width, size.height, null);
        graphics.dispose();
        return resized;
    }

    /**
     * Takes an image and resizes it in such a way that the parts returned by  this method can be put together
     * to form the whole image.
     * The result will then be a square image and the parts will all be of the size specified in
     * {@link this#MINECRAFT_MAP_SIZE}.
     *
     * The algorithm will make a square version of the image argument first and then divide it into parts.
     *
     * @param image The image to be divided.
     * @param crop true, if the image should be cropped to a square part in the middle (i.e. the image will not be
     *             resized) or false, if the image should be resized (i.e. the whole image will be visible,
     *             but compressed to 1:1).
     * @return a never-null 2-dimensional array of images. The outer index represents a row, the inner
     *         one a column in the square arrangement of parts.
     */
    @NotNull
    public static BufferedImage[][] resizeIntoMapSizedParts(@NotNull BufferedImage image, boolean crop) {
        return divideIntoParts(crop ? cropToMapDividableSquare(image) : scaleToMapDividableSquare(image));
    }

    @NotNull
    private static BufferedImage[][] divideIntoParts(@NotNull BufferedImage image) {
        Dimension partSize = MINECRAFT_MAP_SIZE;
        int linearParts = image.getWidth() / partSize.width;
        BufferedImage[][] result = new BufferedImage[linearParts][linearParts];
        for (int i = 0; i < linearParts; i++) {
            for (int j = 0; j < linearParts; j++) {
                result[j][i] = (image.getSubimage(partSize.width * j, partSize.height * i, partSize.width, partSize.height));
            }
        }
        return result;
    }

    @NotNull
    private static BufferedImage scaleToMapDividableSquare(@NotNull BufferedImage image) {
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
    private static BufferedImage cropToMapDividableSquare(@NotNull BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int measure = width > height
                ? MINECRAFT_MAP_SIZE.width * (width / MINECRAFT_MAP_SIZE.width)
                : MINECRAFT_MAP_SIZE.height * (height / MINECRAFT_MAP_SIZE.height);
        return copyOf(image).getSubimage(width - measure / 2, height - measure / 2, measure, measure);
    }

    /**
     * Creates a copy of a given {@link BufferedImage} by creating a new one and populating
     * it with the content of the old one.
     *
     * @param image the image to make a copy of.
     * @return a copy of the image.
     */
    @NotNull
    public static BufferedImage copyOf(@NotNull BufferedImage image) {
        BufferedImage copy = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        Graphics2D graphics = copy.createGraphics();
        graphics.drawImage(image, 0, 0, null);
        graphics.dispose();
        return copy;
    }


}
