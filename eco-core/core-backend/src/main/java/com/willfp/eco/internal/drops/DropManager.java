package com.willfp.eco.internal.drops;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public final class DropManager {
    /**
     * The currently used type, or implementation, of {@link AbstractDropQueue}.
     * <p>
     * Default is {@link DropQueueType#COLLATED}, however this can be changed.
     */
    @Getter
    private DropQueueType type = DropQueueType.COLLATED;

    /**
     * Sets the type of Drop Queue to be used.
     *
     * @param type The type.
     */
    public static void setType(@NotNull final DropQueueType type) {
        DropManager.type = type;
    }
}
