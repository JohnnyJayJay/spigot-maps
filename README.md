# Spigot-Maps

![Release](https://jitpack.io/v/JohnnyJayJay/spigot-maps.svg)

[JavaDoc](https://javadoc.jitpack.io/com/github/johnnyjayjay/spigot-maps/1.1/javadoc/index.html)

A small library that makes the use of customised maps in Spigot very easy.

## Features
- Dynamic map rendering (based on render context)
- (Animated) Text, image and animated gif rendering with convenient usage
- Base class for own renderer implementations
- API to store renderers persistently
- Tools to resize / crop / divide images so that they fit the minecraft maps
- Convenient builder classes and factory methods
- Implementation of MapView to use directly with the Spigot API

**[YOU CAN FIND AN EXAMPLE PLUGIN HERE](./example-plugin)**

## Add as dependency

### Maven

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
 </repositories>

<dependencies>
    <dependency>
        <groupId>com.github.johnnyjayjay</groupId>
        <artifactId>spigot-maps</artifactId>
        <version>1.11+-SNAPSHOT</version>
    </dependency>
</dependencies>
```

### Gradle

```groovy
repositories {
    maven {
        url "https://jitpack.io"
    }
}

dependencies {
    implementation "com.github.johnnyjayjay:spigot-maps:1.11+-SNAPSHOT"
}
```

### I don't use a build tool

An already built .jar file can be found [here](https://github.com/JohnnyJayJay/spigot-maps/releases).
Download it and add it to your project like any other jar file (Eclipse -> Build Path, IntelliJ -> Add as Library).

## Quick Start

You've got various options when creating renderers and maps:

```java
BufferedImage catImage = ImageIO.read(file); // read an image from a source, e.g. a file
catImage = ImageTools.resizeToMapSize(catImage); // resize the image to the minecraft map size
ImageRenderer catRenderer = ImageRenderer.builder()
        .addPlayers(player1, player2) // set the players this map should be rendered to (omitting this means it renders for everyone)
        .image(catImage) // set the image to render
        .build(); // build the instance

Dimension mapSize = ImageTools.MINECRAFT_MAP_SIZE; // the dimensions of a Minecraft map (in pixels)
SimpleTextRenderer messageRenderer = SimpleTextRenderer.builder()
        .addLines("Cats", "are", "so", "cute") // set the lines that will be drawn onto the map
        .addPlayers(player1, player2)
        .font(myFont) // set a text font
        .startingPoint(new Point(mapSize.width / 2, mapSize.height / 2)) // start in the middle
        .build(); // build the instance 

RenderedMap map = MapBuilder.create() // make a new builder
        .store(myStorage) // set a MapStorage for the map
        .addRenderers(catRenderer, messageRenderer) // add the renderers to this map
        .world(player1.getWorld()) // set the world this map is bound to, e.g. the world of the target player
        .build(); // build the map

ItemStack mapItem = map.createItemStack(); // get an ItemStack from this map to work with
```

This example would result in a map that has an image of a cat in the background and the text "Cats are so cute" in the foreground.

You can still mutate the renderers afterwards:
```java
messageRenderer.setText("Cats\nare\nstill\ncute");
```
Note that this will only have effect if `renderOnce` is set to true while building. 
The reason for that decision is performance. Only rendering once saves resources, but 
disables later modification.
If you want to mutate a renderer after building but then leave it that way, you may call
```java
messageRenderer.stopRendering();
```
to save the resources.

### Quicker ways

There are even quicker ways to accomplish some of these operations.

**Create an ImageRenderer with just an image**:

```java
ImageRenderer renderer = ImageRenderer.create(image, player1, player2); // the player arguments are optional
```

**Create an ImageRenderer that renders a single color (e.g. as a background for text)**:

```java
ImageRenderer backgroundRenderer = ImageRenderer.createSingleColorRenderer(Color.BLUE, player1, player2) // the player arguments are optional
```

See `ImageTools.createSingleColoredImage(Color)` to get an instance of `BufferedImage` for that matter.

**Create a TextRenderer with just some lines of text**:

```java
SimpleTextRenderer renderer = SimpleTextRenderer.create("This", "is", "noice");
```

**Create a RenderedMap with just some MapRenderers**:

```java
RenderedMap map = RenderedMap.create(renderer1, renderer2); // not providing any renderers returns a map without renderers
```

### Advanced

#### Gifs 

This library can handle animated gifs using `GifRenderer` and `GifImage`.
Instances of `GifImage` can be obtained via an instance of `GifDecoder` from this library's dependencies:
```java
GifDecoder decoder = new GifDecoder();
decoder.read("./example.gif"); // this also works with URLs, InputStreams etc.
GifImage gif = GifImage.fromDecoder(decoder);
```
For more info about `GifDecoder`, look at [this](https://github.com/rtyley/animated-gif-lib-for-java).

You can also implement an algorithm to decode gifs yourself and then make use of `GifImage#create(List<Frame>)`.

`GifRenderer`s are created like any other renderer:
```java
GifRenderer renderer = GifRenderer.builder()
        .gif(gif) // set the GifImage we just created
        .repeat(5) // repeat the gif 5 times: to repeat it indefinitely, omit this setting or set it to GifRenderer.REPEAT_FOREVER
        .build();
```
This renderer stops rendering automatically after 5 repetitions and 
can now be added to a `MapView` / `RenderedMap` as shown above.

#### Animated Text

Text doesn't have to be static. This library provides a map renderer that renders text character by character.
```java
AnimatedTextRenderer renderer = AnimatedTextRenderer.builder()
        .addText("This text will appear char by char.")
        .charsPerSecond(10) // 10 characters should appear each second
        .delay(20) // start the animation after 20 ticks (1 second)
        .build();
```
This renderer automatically stops rendering after having finished.

#### Splitting images

Images that take more than 1 map to display can be created using `ImageTools.divideIntoMapSizedParts(BufferedImage, boolean)`.
This uses an algorithm that makes a square version of the image first. How this is done can be determined via the second 
`boolean` parameter. If it is set to `true`, a square cropped out from the middle of the image will be used 
(if the image is big enough). If it is `false`, the whole image will be resized to 1:1.

```java
List<BufferedImage> parts = ImageTools.divideIntoMapSizedParts(image, true);
```

**The exact same methods are available to `GifImage`s.**

To turn these into map items:

```java
for (BufferedImage part : parts) {
    ImageRenderer renderer = ImageRenderer.create(part);
    ItemStack mapItem = RenderedMap.create(renderer).createItemStack();
    // do something with it
}
```

Or, if you want to do it stream-like:
```java
parts.stream()
        .map(ImageRenderer::create)
        .map(RenderedMap::create)
        .map(RenderedMap::createItemStack)
        .forEach((mapItem) -> {
    // do something with it
})
```

#### Using MapStorage

The `MapStorage` API makes it possible to save renderers persistently. To utilise it, you have to implement MapStorage:

```java
public class FileStorage implements MapStorage {

    @Override
    public void store(int id, MapRenderer renderer) {
        // serialize the renderer associated with the given id
    }
    
    @Override
    public boolean remove(int id, MapRenderer renderer) {
        // remove the given renderer's association with the given id
    }
    
    @Override
    public List<MapRenderer> provide(int id) {
        // fetch / deserialize / read all renderers stored for the given id
    }
}
```

Then, do the following (e.g. on start up):
```java
InitializationListener.register(new FileStorage(), plugin);
```