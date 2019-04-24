package com.github.johnnyjayjay.spigotmaps.rendering;

import com.github.johnnyjayjay.spigotmaps.util.Checks;
import com.madgag.gif.fmsware.GifDecoder;

import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * A class representing a .gif image, in particular animated ones.
 *
 * @see GifRenderer
 * @author Johnny_JayJay (https://www.github.com/JohnnyJayJay)
 */
public class GifImage implements Iterable<GifImage.Frame> {

    private final List<Frame> frames;

    private GifImage(List<Frame> frames) {
        this.frames = Collections.unmodifiableList(frames);
    }

    /**
     * Creates a new {@link GifImage} based on the data of a {@link com.madgag.gif.fmsware.GifDecoder}.
     *
     * @param decoder a GifDecoder that contains an already read gif image.
     * @return a new, never-{@code null} GifImage.
     */
    public static GifImage fromDecoder(GifDecoder decoder) {
        return new GifImage(IntStream.range(0, decoder.getFrameCount())
                .mapToObj((i) -> Frame.create(decoder.getFrame(i), decoder.getDelay(i)))
                .collect(Collectors.toList()));
    }

    /**
     * Creates a new {@link GifImage} based on a List of Frames.
     *
     * @param frames a List of {@link Frame}s to be used in this gif.
     * @return a new, never-{@code null} GifImage.
     * @throws IllegalArgumentException if not all frames are of the same size.
     */
    public static GifImage create(List<Frame> frames) {
        if (frames.isEmpty())
            return new GifImage(Collections.emptyList());

        BufferedImage first = frames.get(0).getImage();
        int width = first.getWidth();
        int height = first.getHeight();
        Checks.check(
                frames.stream()
                        .map(Frame::getImage)
                        .allMatch((image) -> image.getWidth() == width && image.getHeight() == height),
                "The frames must all have the same size"
        );
        return new GifImage(frames);
    }

    @Override
    public Iterator<Frame> iterator() {
        return frames.iterator();
    }

    @Override
    public void forEach(Consumer<? super Frame> action) {
        frames.forEach(action);
    }

    @Override
    public Spliterator<Frame> spliterator() {
        return frames.spliterator();
    }

    /**
     * Returns the amount of frames this gif holds.
     */
    public int getFrameCount() {
        return frames.size();
    }

    /**
     * Returns the frame at the position of the specified index.
     *
     * @param index the index of the {@link Frame} to retrieve.
     * @return a never-{@code null} {@link Frame} that gives access to its image and delay.
     * @throws IndexOutOfBoundsException if the the index is out of bounds for the list of frames.
     */
    public Frame get(int index) {
        return frames.get(index);
    }

    /**
     * A class representing a single frame in an animated gif.
     *
     * @see GifImage
     */
    public static class Frame {
        private final BufferedImage image;
        private final int msDelay;

        private Frame(BufferedImage image, int msDelay) {
            this.image = image;
            this.msDelay = msDelay;
        }

        /**
         * A factory method to create instances of this class.
         *
         * @param image the image this frame displays.
         * @param msDelay a duration in milliseconds, i.e. how long this frame should be displayed.
         * @return a new, never-{@code null} instance of {@link Frame}.
         * @throws IllegalArgumentException if the given duration/delay is not positive.
         */
        public static Frame create(BufferedImage image, int msDelay) {
            Checks.checkNotNull(image, "Image");
            Checks.check(msDelay > 0, "Duration must be positive");
            return new Frame(image, msDelay);
        }

        /**
         * Returns the duration in milliseconds indicating how long this particular frame should be displayed in a gif.
         */
        public int getMsDelay() {
            return msDelay;
        }

        /**
         * Returns the image of this frame.
         */
        public BufferedImage getImage() {
            return image;
        }
    }

}
