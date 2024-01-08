package com.willfp.eco.core;

import com.willfp.eco.core.proxy.ProxyConstants;
import com.willfp.eco.util.ClassUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

/**
 * A prerequisite is a requirement for something.
 * <p>
 * For example, you can require the server to have paper or be a specific version,
 * or have some other dependency.
 */
public class Prerequisite {
    /**
     * All existing prerequisites are registered on creation.
     */
    private static final List<Prerequisite> VALUES = new ArrayList<>();

    /**
     * Requires the server to be running an implementation of paper.
     */
    public static final Prerequisite HAS_PAPER = new Prerequisite(
            () -> ClassUtils.exists("com.destroystokyo.paper.event.block.BeaconEffectEvent"),
            "Requires server to be running paper (or a fork)"
    );

    /**
     * Requires the server to be running an implementation of paper.
     */
    public static final Prerequisite HAS_PROTOCOLLIB = new Prerequisite(
            () -> ClassUtils.exists("com.comphenix.protocol.events.PacketAdapter"),
            "Requires server to have ProtocolLib"
    );

    /**
     * Requires the server to be running 1.20.3.
     */
    public static final Prerequisite HAS_1_20_3 = new Prerequisite(
            () -> ProxyConstants.NMS_VERSION.contains("20_R3"),
            "Requires server to be running 1.20.3+"
    );

    /**
     * Requires the server to be running 1.20.
     */
    public static final Prerequisite HAS_1_20 = new Prerequisite(
            () -> ProxyConstants.NMS_VERSION.contains("20"),
            "Requires server to be running 1.20+"
    );

    /**
     * Requires the server to be running 1.19.4.
     */
    public static final Prerequisite HAS_1_19_4 = new Prerequisite(
            () -> ProxyConstants.NMS_VERSION.contains("19_R3") || HAS_1_20.isMet(),
            "Requires server to be running 1.19.4+"
    );

    /**
     * Requires the server to be running 1.19.
     */
    public static final Prerequisite HAS_1_19 = new Prerequisite(
            () -> ProxyConstants.NMS_VERSION.contains("19") || HAS_1_20.isMet(),
            "Requires server to be running 1.19+"
    );

    /**
     * Requires the server to be running 1.18.
     */
    public static final Prerequisite HAS_1_18 = new Prerequisite(
            () -> ProxyConstants.NMS_VERSION.contains("18") || HAS_1_19.isMet(),
            "Requires server to be running 1.18+"
    );

    /**
     * Requires the server to be running an implementation of BungeeCord.
     *
     * @deprecated This will never return true.
     */
    @Deprecated(since = "6.49.0", forRemoval = true)
    public static final Prerequisite HAS_BUNGEECORD = new Prerequisite(
            () -> ClassUtils.exists("net.md_5.bungee.api.event.ServerConnectedEvent"),
            "Requires server to be running BungeeCord (or a fork)"
    );

    /**
     * Requires the server to be running an implementation of Velocity.
     *
     * @deprecated This will never return true.
     */
    @Deprecated(since = "6.49.0", forRemoval = true)
    public static final Prerequisite HAS_VELOCITY = new Prerequisite(
            () -> ClassUtils.exists("com.velocitypowered.api.event.player.ServerConnectedEvent"),
            "Requires server to be running Velocity (or a fork)"
    );

    /**
     * If the necessary prerequisite condition has been met.
     */
    private boolean isMet;

    /**
     * Retrieve if the necessary prerequisite condition is met.
     */
    private final Supplier<Boolean> isMetSupplier;

    /**
     * The description of the requirements of the prerequisite.
     */
    private final String description;

    /**
     * Create a prerequisite.
     *
     * @param isMetSupplier An {@link Supplier<Boolean>} that returns if the prerequisite is met
     * @param description   The description of the prerequisite, shown to the user if it isn't
     */
    public Prerequisite(@NotNull final Supplier<Boolean> isMetSupplier,
                        @NotNull final String description) {
        this.isMetSupplier = isMetSupplier;
        this.isMet = isMetSupplier.get();
        this.description = description;
        VALUES.add(this);
    }

    /**
     * Refresh the condition set in the supplier, updates {@link this#isMet}.
     */
    private void refresh() {
        this.isMet = this.isMetSupplier.get();
    }

    /**
     * Update all prerequisites' {@link Prerequisite#isMet}.
     */
    public static void update() {
        VALUES.forEach(Prerequisite::refresh);
    }

    /**
     * Check if all prerequisites in array are met.
     *
     * @param prerequisites A primitive array of prerequisites to check.
     * @return If all the prerequisites are met.
     */
    public static boolean areMet(@NotNull final Prerequisite[] prerequisites) {
        update();
        return Arrays.stream(prerequisites).allMatch(Prerequisite::isMet);
    }

    static {
        update();
    }

    /**
     * Get if the prerequisite is met.
     *
     * @return If the condition is met.
     */
    public boolean isMet() {
        return this.isMet;
    }

    /**
     * Get the description.
     *
     * @return The description.
     */
    public String getDescription() {
        return this.description;
    }
}
