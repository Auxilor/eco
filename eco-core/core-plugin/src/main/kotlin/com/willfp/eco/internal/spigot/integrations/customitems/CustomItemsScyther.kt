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
            val material = Material.matchMaterial(key.uppercase()) ?: Material.WOODEN_HOE

            val hoe = ScytherAPI.createHarvesterHoe(
                Scyther.getInstance(),
                material,
                0,
                1.0,
                1,
                Int.MAX_VALUE,
                "AUTOSELL",
                true
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
