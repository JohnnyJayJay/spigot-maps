package com.github.johnnyjayjay.spigotmaps.example

import com.github.johnnyjayjay.spigotmaps.*
import com.github.johnnyjayjay.spigotmaps.rendering.*
import com.github.johnnyjayjay.spigotmaps.util.ImageTools
import com.madgag.gif.fmsware.GifDecoder
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.map.MapRenderer
import org.bukkit.plugin.java.JavaPlugin

import java.awt.image.BufferedImage
import java.io.IOException
import java.net.URL

class SamplePlugin : JavaPlugin() {

    @Override
    fun onEnable() {
        getCommand("image")
        getCommand("text")
        getCommand("atext")
        getCommand("gif")
    }

    @Override
    fun onCommand(sender: CommandSender, command: Command, name: String, args: Array<String>): Boolean {
        if (sender !is Player)
            return false

        val player = sender as Player
        try {
            val map = createMap(player, name, String.join(" ", args))
            map.give(player)
            player.sendMessage("§aLook in your inventory!")
        } catch (e: IOException) {
            player.sendMessage("§cCould not create map: " + e.getMessage())
        }

        return true
    }

    @Throws(IOException::class)
    private fun createMap(player: Player, kind: String, text: String): RenderedMap {
        val renderer = switch(kind) {
            case "image" -> {
            URL imageUrl = new URL(text);
            BufferedImage image = ImageTools . resizeToMapSize ImageTools.loadWithUserAgentFrom(imageUrl);
            break ImageRenderer . builder ().addPlayers(player).image(image).build();
        }
            case "text" -> SimpleTextRenderer.builder().addPlayers(player).addText(text).build();
            case "atext" -> AnimatedTextRenderer.builder().addPlayers(player).addText(text).build();
            case "gif" -> {
            GifDecoder decoder = new GifDecoder();
            int code = decoder . read text;
            if (code != GifDecoder.STATUS_OK)
                throw new IOException "Could not load gif image. Code: $code";

            GifImage gif = ImageTools . resizeToMapSize GifImage.fromDecoder(decoder);
            break GifRenderer . builder ()
                    .addPlayers(player)
                    .gif(gif)
                    .repeat(decoder.getLoopCount() == 0 ? GifRenderer . REPEAT_FOREVER : decoder . getLoopCount ())
            .build();
        }
            default -> throw new AssertionError();
        }
        return MapBuilder.create().world(player.getWorld()).addRenderers(renderer).build()
    }
}