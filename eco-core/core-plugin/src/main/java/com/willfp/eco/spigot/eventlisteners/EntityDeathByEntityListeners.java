package com.willfp.eco.spigot.eventlisteners;

import com.willfp.eco.core.PluginDependent;
import com.willfp.eco.core.EcoPlugin;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class EntityDeathByEntityListeners extends PluginDependent<EcoPlugin> implements Listener {
    private final Set<EntityDeathByEntityBuilder> events = new HashSet<>();

    @ApiStatus.Internal
    public EntityDeathByEntityListeners(@NotNull final EcoPlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamage(@NotNull final EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof LivingEntity victim)) {
            return;
        }

        if (victim.getHealth() > event.getFinalDamage()) {
            return;
        }

        EntityDeathByEntityBuilder builtEvent = new EntityDeathByEntityBuilder();
        builtEvent.setVictim(victim);
        builtEvent.setDamager(event.getDamager());
        events.add(builtEvent);

        this.getPlugin().getScheduler().runLater(() -> events.remove(builtEvent), 1);
    }

    @EventHandler
    public void onEntityDeath(@NotNull final EntityDeathEvent event) {
        LivingEntity victim = event.getEntity();

        List<ItemStack> drops = event.getDrops();
        int xp = event.getDroppedExp();

        AtomicReference<EntityDeathByEntityBuilder> atomicBuiltEvent = new AtomicReference<>(null);
        EntityDeathByEntityBuilder builtEvent;

        events.forEach(deathByEntityEvent -> {
            if (deathByEntityEvent.getVictim().equals(victim)) {
                atomicBuiltEvent.set(deathByEntityEvent);
            }
        });

        if (atomicBuiltEvent.get() == null) {
            return;
        }

        builtEvent = atomicBuiltEvent.get();
        events.remove(builtEvent);
        builtEvent.setDrops(drops);
        builtEvent.setXp(xp);
        builtEvent.setDeathEvent(event);

        builtEvent.push();
    }
}
