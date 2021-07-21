package com.willfp.eco.internal.drops.impl;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EcoFastCollatedDropQueue extends EcoDropQueue {
    public static final Map<Player, CollatedDrops> COLLATED_MAP = new ConcurrentHashMap<>();

    public EcoFastCollatedDropQueue(@NotNull final Player player) {
        super(player);
    }

    @Override
    public void push() {
        CollatedDrops fetched = COLLATED_MAP.get(getPlayer());
        CollatedDrops collatedDrops = fetched == null ? new CollatedDrops(getItems(), getLoc(), getXp()) : fetched.addDrops(getItems()).setLocation(getLoc()).addXp(getXp());
        COLLATED_MAP.put(this.getPlayer(), collatedDrops);
    }

    @ToString
    public static final class CollatedDrops {
        @Getter
        private final List<ItemStack> drops;

        @Getter
        @Setter
        @Accessors(chain = true)
        private Location location;

        @Getter
        private int xp;

        private CollatedDrops(@NotNull final List<ItemStack> drops,
                              @NotNull final Location location,
                              final int xp) {
            this.drops = drops;
            this.location = location;
            this.xp = xp;
        }

        public CollatedDrops addDrops(@NotNull final List<ItemStack> toAdd) {
            drops.addAll(toAdd);
            return this;
        }

        public CollatedDrops addXp(final int xp) {
            this.xp += xp;
            return this;
        }
    }
}
