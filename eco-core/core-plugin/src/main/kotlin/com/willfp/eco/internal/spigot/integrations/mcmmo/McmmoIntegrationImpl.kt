package com.willfp.eco.internal.spigot.integrations.mcmmo

import com.gmail.nossr50.datatypes.meta.BonusDropMeta
import com.gmail.nossr50.events.fake.FakeEvent
import com.gmail.nossr50.util.MetadataConstants
import com.willfp.eco.core.integrations.mcmmo.McmmoIntegration
import com.willfp.eco.util.ClassUtils
import org.bukkit.block.Block
import org.bukkit.event.Event

class McmmoIntegrationImpl : McmmoIntegration {
    private var disabled = false
    override fun getBonusDropCount(block: Block): Int {
        if (disabled) {
            return 0
        }
        if (block.getMetadata(MetadataConstants.METADATA_KEY_BONUS_DROPS).isNotEmpty()) {
            val bonusDropMeta = block.getMetadata(MetadataConstants.METADATA_KEY_BONUS_DROPS)[0] as BonusDropMeta
            return bonusDropMeta.asInt()
        }
        return 0
    }

    override fun isFake(event: Event): Boolean {
        return if (disabled) {
            false
        } else event is FakeEvent
    }

    override fun getPluginName(): String {
        return "mcMMO"
    }

    init {
        if (!ClassUtils.exists("com.gmail.nossr50.events.fake.FakeEvent")) {
            disabled = true
        }
    }
}