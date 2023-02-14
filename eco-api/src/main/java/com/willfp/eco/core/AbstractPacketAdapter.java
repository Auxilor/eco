package com.willfp.eco.core;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

/**
 * Wrapper class for ProtocolLib packets.
 *
 * @deprecated ProtocolLib is no longer used by eco. Use {@link com.willfp.eco.core.packet.PacketListener} instead.
 */
@Deprecated(since = "6.51.0")
public abstract class AbstractPacketAdapter extends PacketAdapter {
    /**
     * The handle type to listen for.
     */
    private final PacketType type;

    /**
     * Whether the handle adapter should be registered after the server has loaded.
     * <p>
     * Useful for monitor priority adapters that <b>must</b> be ran last.
     */
    private final boolean postLoad;

    /**
     * Create a new handle adapter for a specified plugin and type.
     *
     * @param plugin   The plugin that ProtocolLib should mark as the owner.
     * @param type     The {@link PacketType} to listen for.
     * @param priority The priority at which the adapter should be ran on handle send/receive.
     * @param postLoad If the handle adapter should be registered after the server has loaded.
     */
    protected AbstractPacketAdapter(@NotNull final EcoPlugin plugin,
                                    @NotNull final PacketType type,
                                    @NotNull final ListenerPriority priority,
                                    final boolean postLoad) {
        super(plugin, priority, Collections.singletonList(type));
        this.type = type;
        this.postLoad = postLoad;
    }

    /**
     * Create a new handle adapter for a specified plugin and type.
     *
     * @param plugin   The plugin that ProtocolLib should mark as the owner.
     * @param type     The {@link PacketType} to listen for.
     * @param postLoad If the handle adapter should be registered after the server has loaded.
     */
    protected AbstractPacketAdapter(@NotNull final EcoPlugin plugin,
                                    @NotNull final PacketType type,
                                    final boolean postLoad) {
        this(plugin, type, ListenerPriority.NORMAL, postLoad);
    }

    /**
     * The code that should be executed once the handle has been received.
     *
     * @param packet The handle.
     * @param player The player.
     * @param event  The event.
     */
    public void onReceive(@NotNull final PacketContainer packet,
                          @NotNull final Player player,
                          @NotNull final PacketEvent event) {
        // Empty rather than abstract as implementations don't need both
    }

    /**
     * THe code that should be executed once the handle has been sent.
     *
     * @param packet The handle.
     * @param player The player.
     * @param event  The event.
     */
    public void onSend(@NotNull final PacketContainer packet,
                       @NotNull final Player player,
                       @NotNull final PacketEvent event) {
        // Empty rather than abstract as implementations don't need both
    }

    /**
     * Boilerplate to assert that the handle is of the specified type.
     *
     * @param event The ProtocolLib event.
     */
    @Override
    public final void onPacketReceiving(final PacketEvent event) {
        if (event.getPacket() == null) {
            return;
        }

        if (!event.getPacket().getType().equals(type)) {
            return;
        }

        onReceive(event.getPacket(), event.getPlayer(), event);
    }

    /**
     * Boilerplate to assert that the handle is of the specified type.
     *
     * @param event The ProtocolLib event.
     */
    @Override
    public final void onPacketSending(final PacketEvent event) {
        if (event.getPacket() == null) {
            return;
        }

        if (!event.getPacket().getType().equals(type)) {
            return;
        }

        onSend(event.getPacket(), event.getPlayer(), event);
    }

    @Override
    public final EcoPlugin getPlugin() {
        return (EcoPlugin) super.getPlugin();
    }

    /**
     * Register the handle adapter with ProtocolLib.
     */
    public final void register() {
        if (!ProtocolLibrary.getProtocolManager().getPacketListeners().contains(this)) {
            ProtocolLibrary.getProtocolManager().addPacketListener(this);
        }
    }

    /**
     * Get if the handle adapter should be loaded last.
     *
     * @return If post load.
     */
    public boolean isPostLoad() {
        return this.postLoad;
    }
}
