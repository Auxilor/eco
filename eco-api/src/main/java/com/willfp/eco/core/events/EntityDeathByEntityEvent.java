package com.willfp.eco.core.events;

import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EntityDeathByEntityEvent extends Event {
    /**
     * Internal, for bukkit.
     */
    private static final HandlerList HANDLERS = new HandlerList();

    /**
     * The {@link LivingEntity} killed.
     */
    @Getter
    private final LivingEntity victim;

    /**
     * The {@link Entity} that killed.
     */
    @Getter
    private final Entity killer;

    /**
     * The associated {@link EntityDeathEvent}.
     */
    @Getter
    private final EntityDeathEvent deathEvent;

    /**
     * The entity drops.
     */
    @Getter
    private final List<ItemStack> drops;

    /**
     * The xp to drop.
     */
    @Getter
    private final int xp;

    /**
     * Create event based off parameters.
     *
     * @param victim     The killed entity
     * @param killer     The killer
     * @param drops      The item drops
     * @param xp         The amount of xp to drop
     * @param deathEvent The associated {@link EntityDeathEvent}
     */
    public EntityDeathByEntityEvent(@NotNull final LivingEntity victim,
                                    @NotNull final Entity killer,
                                    @NotNull final List<ItemStack> drops,
                                    final int xp,
                                    @NotNull final EntityDeathEvent deathEvent) {
        this.victim = victim;
        this.killer = killer;
        this.drops = drops;
        this.xp = xp;
        this.deathEvent = deathEvent;
    }

    /**
     * Internal bukkit.
     *
     * @return Get the handlers.
     */
    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    /**
     * Internal bukkit.
     *
     * @return The handlers.
     */
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
