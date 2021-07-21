package com.willfp.eco.spigot.eventlisteners;

import com.willfp.eco.core.events.NaturalExpGainEvent;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.jetbrains.annotations.NotNull;

class NaturalExpGainBuilder {
    @Getter
    @Setter
    private boolean cancelled = false;

    @Getter
    @Setter
    private PlayerExpChangeEvent event;

    @Getter
    @Setter
    private Location location;
    
    @Getter
    @Setter
    private BuildReason reason;
    
    NaturalExpGainBuilder(@NotNull final BuildReason reason) {
        this.reason = reason;
    }

    public void push() {
        Validate.notNull(event);
        if (this.cancelled) {
            return;
        }

        NaturalExpGainEvent naturalExpGainEvent = new NaturalExpGainEvent(event);

        Bukkit.getPluginManager().callEvent(naturalExpGainEvent);
    }

    public enum BuildReason {
        BOTTLE,
        PLAYER
    }
}
