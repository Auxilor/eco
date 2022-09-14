package com.willfp.eco.internal.spigot.integrations.customitems

import com.willfp.eco.core.integrations.customitems.CustomItemsIntegration
import com.willfp.eco.core.items.CustomItem
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.items.TestableItem
import com.willfp.eco.core.items.provider.ItemProvider
import com.willfp.eco.util.NamespacedKeyUtils
import dev.norska.scyther.Scyther
import dev.norska.scyther.api.ScytherAPI
import org.bukkit.Material

class CustomItemsScyther : CustomItemsIntegration {
    override fun registerProvider() {
        Items.registerItemProvider(ScytherProvider())
    }

    override fun getPluginName(): String {
        return "Scyther"
    }

    private class ScytherProvider : ItemProvider("scyther") {
        override fun provideForKey(key: String): TestableItem {
            val split = key.split("::")

            val material = Material.matchMaterial(split.first().uppercase()) ?: Material.WOODEN_HOE

            val materialData = split.getOrNull(1)?.toIntOrNull() ?: -1

            val sellMultiplier = split.getOrNull(2)?.toDoubleOrNull() ?: 1.0

            val dropMultiplier = split.getOrNull(3)?.toIntOrNull() ?: 1

            val uses = split.getOrNull(4)?.toIntOrNull() ?: 1

            val defaultMode = split.getOrNull(4)?: "autosell"

            val glow = split.getOrNull(5)?.toBoolean() ?: false

            val hoe = ScytherAPI.createHarvesterHoe(
                Scyther.getInstance(),
                material,
                materialData,
                sellMultiplier,
                dropMultiplier,
                uses,
                defaultMode,
                glow
            )

            val namespacedKey = NamespacedKeyUtils.create("scyther", key)

            return CustomItem(
                namespacedKey,
                {
                    ScytherAPI.isHarvesterItem(it) && it.type == material
                },
                hoe
            )
        }
    }
}
