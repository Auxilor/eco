package com.willfp.eco.internal.drops;

import com.willfp.eco.core.drops.DropQueueFactory;
import com.willfp.eco.core.drops.InternalDropQueue;
import com.willfp.eco.internal.drops.impl.EcoDropQueue;
import com.willfp.eco.internal.drops.impl.EcoFastCollatedDropQueue;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class EcoDropQueueFactory implements DropQueueFactory {
    @Override
    public InternalDropQueue create(@NotNull final Player player) {
        return DropManager.getType() == DropQueueType.COLLATED ? new EcoFastCollatedDropQueue(player) : new EcoDropQueue(player);
    }
}
