package com.willfp.eco.internal.drops;

import com.willfp.eco.core.drops.DropQueue;
import com.willfp.eco.core.drops.DropQueueFactory;
import com.willfp.eco.internal.drops.impl.FastCollatedDropQueue;
import com.willfp.eco.internal.drops.impl.InternalDropQueue;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class EcoDropQueueFactory implements DropQueueFactory {
    @Override
    public DropQueue create(@NotNull final Player player) {
        return DropManager.getType() == DropQueueType.COLLATED ? new FastCollatedDropQueue(player) : new InternalDropQueue(player);
    }
}
