package com.willfp.eco.internal.drops;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.config.updating.ConfigUpdater;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public final class DropManager {
    @Getter
    private DropQueueType type = DropQueueType.COLLATED;

    @ConfigUpdater
    public static void update(@NotNull final EcoPlugin plugin) {
        type = plugin.getConfigYml().getBool("use-fast-collated-drops") ? DropQueueType.COLLATED : DropQueueType.STANDARD;
    }
}
