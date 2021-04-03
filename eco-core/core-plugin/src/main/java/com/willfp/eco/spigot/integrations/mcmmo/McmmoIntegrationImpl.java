package com.willfp.eco.spigot.integrations.mcmmo;

import com.gmail.nossr50.datatypes.meta.BonusDropMeta;
import com.gmail.nossr50.events.fake.FakeEvent;
import com.gmail.nossr50.mcMMO;
import com.willfp.eco.util.ClassUtils;
import com.willfp.eco.core.integrations.mcmmo.McmmoWrapper;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

public class McmmoIntegrationImpl implements McmmoWrapper {
    /**
     * Disabled if mcmmo is outdated or missing classes.
     */
    boolean disabled = false;

    /**
     * Initialize mcMMO integration.
     */
    public McmmoIntegrationImpl() {
        if (!ClassUtils.exists("com.gmail.nossr50.events.fake.FakeEvent")) {
            disabled = true;
        }
    }

    @Override
    public int getBonusDropCount(@NotNull final Block block) {
        if (disabled) {
            return 0;
        }

        if (block.getMetadata(mcMMO.BONUS_DROPS_METAKEY).size() > 0) {
            BonusDropMeta bonusDropMeta = (BonusDropMeta) block.getMetadata(mcMMO.BONUS_DROPS_METAKEY).get(0);
            return bonusDropMeta.asInt();
        }

        return 0;
    }

    @Override
    public boolean isFake(@NotNull final Event event) {
        if (disabled) {
            return false;
        }

        return event instanceof FakeEvent;
    }
}
