package com.willfp.eco.spigot.eventlisteners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ExpBottleEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class NaturalExpGainListeners implements Listener {
    private final Set<NaturalExpGainBuilder> events = new HashSet<>();
    
    @EventHandler
    public void playerChange(@NotNull final PlayerExpChangeEvent event) {
        NaturalExpGainBuilder builder = new NaturalExpGainBuilder(NaturalExpGainBuilder.BuildReason.PLAYER);
        builder.setEvent(event);

        NaturalExpGainBuilder toRemove = null;
        for (NaturalExpGainBuilder searchBuilder : events) {
            if (!Objects.equals(searchBuilder.getLocation().getWorld(), event.getPlayer().getLocation().getWorld())) {
                continue;
            }

            if (searchBuilder.getReason().equals(NaturalExpGainBuilder.BuildReason.BOTTLE) && searchBuilder.getLocation().distanceSquared(event.getPlayer().getLocation()) > 52) {
                toRemove = searchBuilder;
            }
        }

        if (toRemove != null) {
            events.remove(toRemove);
            return;
        }

        builder.setEvent(event);
        builder.push();

        events.remove(builder);
    }

    @EventHandler
    public void onExpBottle(@NotNull final ExpBottleEvent event) {
        NaturalExpGainBuilder builtEvent = new NaturalExpGainBuilder(NaturalExpGainBuilder.BuildReason.BOTTLE);
        builtEvent.setLocation(event.getEntity().getLocation());

        events.add(builtEvent);
    }
}
