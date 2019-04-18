# Spigot-Maps

[![Release](https://jitpack.io/v/User/Repo.svg)] 
[JavaDoc](https://javadoc.jitpack.io/com/github/johnnyjayjay/spigot-maps/master-SNAPSHOT/javadoc/index.html)

A small library that makes the use of customised maps in Spigot very easy.

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
        <version>1.0</version>
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
    implementation "com.github.johnnyjayjay:spigot-maps:1.0"
}
```

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
TextRenderer messageRenderer = TextRenderer.builder()
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
TextRenderer renderer = TextRenderer.create("This", "is", "noice");
```

**Create a RenderedMap with just some MapRenderers**:

```java
RenderedMap map = RenderedMap.create(renderer1, renderer2); // not providing any renderers returns a map without renderers
```

### Advanced

Images that take more than 1 map to display can be created using `ImageTools.resizeIntoMapSizedParts(BufferedImage, boolean)`. This uses an algorithm that makes a square version of the image first. How this is done can be determined via the second `boolean` parameter. If it is set to `true`, a square cropped out from the middle of the image will be used. If it is `false`, the whole image will be resized to 1:1.

```java
BufferedImage[][] parts = ImageTools.resizeIntoMapSizedParts(image, true);
```

To turn these into map items:

```java
ItemStack[][] maps = Arrays.stream(parts).map(
        (part) -> Arrays.stream(part)
                .map(ImageRenderer::create)
                .map(RenderedMap::create)
                .map(RenderedMap::createItemStack)
                .toArray(ItemStack[]::new)
).toArray(ItemStack[][]::new);
```

This results in the same as:

```java
ItemStack[][] maps = new ItemStack[parts.length][];
for (int row = 0; row < parts.length; row++) {
    for (int column = 0; column < parts[row].length, column++) {
        maps[row][column] = RenderedMap.create(ImageRenderer
                .create(parts[row][column]))
                .createItemStack();
    }
}
```
