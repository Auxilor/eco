package com.willfp.eco.internal.spigot.integrations.customitems

import com.willfp.eco.core.integrations.customitems.CustomItemsWrapper
import com.willfp.eco.core.items.CustomItem
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.items.TestableItem
import com.willfp.eco.core.items.provider.ItemProvider
import com.willfp.eco.util.NamespacedKeyUtils
import io.th0rgal.oraxen.items.OraxenItems

class CustomItemsOraxen : CustomItemsWrapper {
    override fun registerAllItems() {
        Items.registerItemProvider(OraxenProvider())
    }

    override fun getPluginName(): String {
        return "Oraxen"
    }

    private class OraxenProvider : ItemProvider("oraxen") {
        override fun provideForKey(id: String): TestableItem? {
            val item = OraxenItems.getItemById(id) ?: return null
            val key = NamespacedKeyUtils.create("oraxen", id)
            return CustomItem(
                key,
                { id.equals(OraxenItems.getIdByItem(it), ignoreCase = true) },
                item.build()
            )
        }
    }
}
