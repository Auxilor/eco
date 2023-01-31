package com.willfp.eco.internal.spigot.integrations.customitems

import com.willfp.eco.core.integrations.customitems.CustomItemsIntegration
import com.willfp.eco.core.items.CustomItem
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.items.TestableItem
import com.willfp.eco.core.items.provider.ItemProvider
import com.willfp.eco.util.NamespacedKeyUtils
import io.th0rgal.oraxen.api.OraxenItems

class CustomItemsOraxen : CustomItemsIntegration {
    override fun registerProvider() {
        Items.registerItemProvider(OraxenProvider())
    }

    override fun getPluginName(): String {
        return "Oraxen"
    }

    private class OraxenProvider : ItemProvider("oraxen") {
        override fun provideForKey(key: String): TestableItem? {
            val item = OraxenItems.getItemById(key) ?: return null
            val id = OraxenItems.getIdByItem(item)
            val namespacedKey = NamespacedKeyUtils.create("oraxen", id)
            return CustomItem(
                namespacedKey,
                { id.equals(OraxenItems.getIdByItem(it), ignoreCase = true) },
                item.build()
            )
        }
    }
}
