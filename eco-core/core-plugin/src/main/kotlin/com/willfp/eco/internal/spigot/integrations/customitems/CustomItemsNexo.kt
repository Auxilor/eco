package com.willfp.eco.internal.spigot.integrations.customitems

import com.nexomc.nexo.api.NexoItems
import com.nexomc.nexo.api.events.NexoItemsLoadedEvent
import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.integrations.customitems.CustomItemsIntegration
import com.willfp.eco.core.items.CustomItem
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.items.TestableItem
import com.willfp.eco.core.items.provider.ItemProvider
import com.willfp.eco.util.namespacedKeyOf
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class CustomItemsNexo(
    private val plugin: EcoPlugin
) : CustomItemsIntegration, Listener {
    override fun registerProvider() {
        plugin.eventManager.registerListener(this)
    }

    override fun getPluginName(): String {
        return "Nexo"
    }

    @EventHandler
    @Suppress("UNUSED_PARAMETER")
    fun onItemRegister(event: NexoItemsLoadedEvent) {
        Items.registerItemProvider(NexoProvider())
    }

    private class NexoProvider : ItemProvider("nexo") {
        override fun provideForKey(key: String): TestableItem? {
            val item = NexoItems.itemFromId(key) ?: return null
            val id = NexoItems.idFromItem(item)
            val namespacedKey = namespacedKeyOf("nexo", id.toString())
            return CustomItem(
                namespacedKey,
                { id.equals(NexoItems.idFromItem(it), ignoreCase = true) },
                item.build()
            )
        }
    }
}