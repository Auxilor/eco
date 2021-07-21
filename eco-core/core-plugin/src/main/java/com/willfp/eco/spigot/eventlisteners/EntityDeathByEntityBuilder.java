package com.willfp.eco.spigot.eventlisteners;

import com.willfp.eco.core.events.EntityDeathByEntityEvent;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

class EntityDeathByEntityBuilder {
    @Getter
    @Setter
    private LivingEntity victim = null;
    
    @Getter
    @Setter
    private Entity damager;
    
    @Getter
    @Setter
    private EntityDeathEvent deathEvent;

    @Getter
    @Setter
    private List<ItemStack> drops;

    @Getter
    @Setter
    private int xp = 0;

    public void push() {
        Validate.notNull(victim);
        Validate.notNull(damager);
        Validate.notNull(drops);
        Validate.notNull(deathEvent);

        EntityDeathByEntityEvent event = new EntityDeathByEntityEvent(victim, damager, drops, xp, deathEvent);

        Bukkit.getPluginManager().callEvent(event);
    }
}
