package com.willfp.eco.core;

import com.willfp.eco.core.proxy.ProxyConstants;
import com.willfp.eco.util.ClassUtils;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class Prerequisite {
    /**
     * All existing prerequisites are registered on creation.
     */
    private static final List<Prerequisite> VALUES = new ArrayList<>();

    /**
     * Requires the server to be running an implementation of paper.
     */
    public static final Prerequisite HAS_PAPER = new Prerequisite(
            () -> ClassUtils.exists("com.destroystokyo.paper.event.player.PlayerElytraBoostEvent"),
            "Requires server to be running paper (or a fork)"
    );

    /**
     * Requires the server to be running 1.17.
     */
    @Deprecated
    public static final Prerequisite v1_17 = new Prerequisite(
            () -> ProxyConstants.NMS_VERSION.contains("17"),
            "Requires server to be running 1.17+"
    );

    /**
     * Requires the server to be running 1.17.
     */
    public static final Prerequisite HAS_1_17 = new Prerequisite(
            () -> ProxyConstants.NMS_VERSION.contains("17"),
            "Requires server to be running 1.17+"
    );

    /**
     * If the necessary prerequisite condition has been met.
     */
    @Getter
    private boolean isMet;

    /**
     * Retrieve if the necessary prerequisite condition is met.
     */
    private final Supplier<Boolean> isMetSupplier;

    /**
     * The description of the requirements of the prerequisite.
     */
    @Getter
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
}
