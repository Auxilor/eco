package com.willfp.eco.util;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import net.kyori.adventure.chat.ChatType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * An audience for Spigot, where chat messages and action bars go through Spigot's own chat API.
 * <p>
 * adventure-platform-bukkit cannot send either on current Spigot versions: chat messages never
 * arrive, and the action bar is sent empty, which disconnects the player. Everything else is
 * forwarded to the adventure-platform audience.
 */
final class SpigotAudience implements ForwardingAudience.Single {
    /**
     * The command sender.
     */
    private final CommandSender sender;

    /**
     * The audience everything other than chat messages and action bars goes to.
     */
    private final Audience fallback;

    /**
     * Create a new Spigot audience.
     *
     * @param sender   The command sender.
     * @param fallback The audience everything other than chat messages and action bars goes to.
     */
    SpigotAudience(@NotNull final CommandSender sender,
                   @NotNull final Audience fallback) {
        this.sender = sender;
        this.fallback = fallback;
    }

    @Override
    @NotNull
    public Audience audience() {
        return fallback;
    }

    @Override
    public void sendMessage(@NotNull final Component message) {
        sender.spigot().sendMessage(toBungee(message));
    }

    @Override
    public void sendMessage(@NotNull final Component message,
                            @NotNull final ChatType.Bound boundChatType) {
        sendMessage(message);
    }

    @Override
    public void sendActionBar(@NotNull final Component message) {
        if (sender instanceof Player player) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, toBungee(message));
        }
    }

    /**
     * Convert a component to Spigot's chat components.
     *
     * @param component The component.
     * @return The chat components.
     */
    @NotNull
    private static BaseComponent toBungee(@NotNull final Component component) {
        return ComponentSerializer.deserialize(GsonComponentSerializer.gson().serialize(component));
    }
}
