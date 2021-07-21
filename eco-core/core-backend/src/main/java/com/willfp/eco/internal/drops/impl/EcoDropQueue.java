package com.willfp.eco.internal.drops.impl;

import com.willfp.eco.core.drops.DropQueue;
import com.willfp.eco.util.TelekinesisUtils;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class EcoDropQueue extends DropQueue {
    @Getter(AccessLevel.PROTECTED)
    private final List<ItemStack> items;
    
    @Getter(AccessLevel.PROTECTED)
    private int xp;
    
    @Getter(AccessLevel.PROTECTED)
    private final Player player;

    @Getter(AccessLevel.PROTECTED)
    private Location loc;
    
    @Getter(AccessLevel.PROTECTED)
    private boolean hasTelekinesis = false;

    public EcoDropQueue(@NotNull final Player player) {
        super(player);
        this.items = new ArrayList<>();
        this.xp = 0;
        this.player = player;
        this.loc = player.getLocation();
    }

    @Override
    public DropQueue addItem(@NotNull final ItemStack item) {
        this.items.add(item);
        return this;
    }

    @Override
    public DropQueue addItems(@NotNull final Collection<ItemStack> itemStacks) {
        this.items.addAll(itemStacks);
        return this;
    }

    @Override
    public DropQueue addXP(final int amount) {
        this.xp += amount;
        return this;
    }

    @Override
    public DropQueue setLocation(@NotNull final Location location) {
        this.loc = location;
        return this;
    }

    @Override
    public DropQueue forceTelekinesis() {
        this.hasTelekinesis = true;
        return this;
    }

    @Override
    public void push() {
        if (!hasTelekinesis) {
            hasTelekinesis = TelekinesisUtils.testPlayer(player);
        }

        World world = loc.getWorld();
        assert world != null;
        loc = loc.add(0.5, 0.5, 0.5);

        items.removeIf(itemStack -> itemStack.getType() == Material.AIR);
        if (items.isEmpty()) {
            return;
        }

        if (hasTelekinesis) {
            HashMap<Integer, ItemStack> leftover = player.getInventory().addItem(items.toArray(new ItemStack[0]));
            for (ItemStack drop : leftover.values()) {
                world.dropItem(loc, drop).setVelocity(new Vector());
            }
            if (xp > 0) {
                PlayerExpChangeEvent event = new PlayerExpChangeEvent(player, xp);
                Bukkit.getPluginManager().callEvent(event);
                ExperienceOrb orb = (ExperienceOrb) world.spawnEntity(player.getLocation().add(0, 0.2, 0), EntityType.EXPERIENCE_ORB);
                orb.setVelocity(new Vector(0, 0, 0));
                orb.setExperience(event.getAmount());
            }
        } else {
            for (ItemStack drop : items) {
                world.dropItem(loc, drop).setVelocity(new Vector());
            }
            if (xp > 0) {
                ExperienceOrb orb = (ExperienceOrb) world.spawnEntity(loc, EntityType.EXPERIENCE_ORB);
                orb.setExperience(xp);
            }
        }
    }
}
