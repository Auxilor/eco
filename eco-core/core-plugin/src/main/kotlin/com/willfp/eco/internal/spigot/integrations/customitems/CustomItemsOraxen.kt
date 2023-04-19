package com.willfp.eco.internal.spigot.integrations.customitems

import com.willfp.eco.core.Eco
import com.willfp.eco.core.integrations.customitems.CustomItemsIntegration
import com.willfp.eco.core.items.CustomItem
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.items.TestableItem
import com.willfp.eco.core.items.provider.ItemProvider
import com.willfp.eco.util.NamespacedKeyUtils
import io.th0rgal.oraxen.api.OraxenItems
import io.th0rgal.oraxen.api.events.OraxenItemsLoadedEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class CustomItemsOraxen(
    private val plugin: EcoPlugin
) : CustomItemsIntegration, Listener {
    override fun registerProvider() {
        plugin.eventManager.registerListener(this)
    }

    override fun getPluginName(): String {
        return "Oraxen"
    }

    @EventHandler
    fun onItemRegister(event: OraxenItemsLoadedEvent) {
        Items.registerItemProvider(OraxenProvider())
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
