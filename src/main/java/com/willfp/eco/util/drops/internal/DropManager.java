package com.willfp.eco.util.drops.internal;

import lombok.Getter;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class DropManager {
    /**
     * The currently used type, or implementation, of {@link AbstractDropQueue}.
     * <p>
     * Standard by default, used if drops.collate key is not present in config.
     */
    @Getter
    private DropQueueType type = DropQueueType.COLLATED;
}
