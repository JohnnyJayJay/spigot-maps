package com.github.johnnyjayjay.spigotmaps.rendering;

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
 * TODO documentation
 * @author Johnny_JayJay (https://www.github.com/JohnnyJayJay)
 */
public class GifImage implements Iterable<GifImage.Frame> {

    private final List<Frame> frames;

    private GifImage(List<Frame> frames) {
        this.frames = Collections.unmodifiableList(frames);
    }

    public static GifImage fromDecoder(GifDecoder decoder) {
        return new GifImage(IntStream.range(0, decoder.getFrameCount())
                .mapToObj((i) -> Frame.create(decoder.getFrame(i), decoder.getDelay(i)))
                .collect(Collectors.toList()));
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

    public int getFrameCount() {
        return frames.size();
    }

    public Frame get(int index) {
        return frames.get(index);
    }

    public static class Frame {
        private final BufferedImage image;
        private final int msDelay;

        private Frame(BufferedImage image, int msDelay) {
            this.image = image;
            this.msDelay = msDelay;
        }

        public static Frame create(BufferedImage image, int msDelay) {
            return new Frame(image, msDelay);
        }

        public int getMsDelay() {
            return msDelay;
        }

        public BufferedImage getImage() {
            return image;
        }
    }

}
