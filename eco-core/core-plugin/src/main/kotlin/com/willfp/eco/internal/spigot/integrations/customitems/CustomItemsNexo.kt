package com.willfp.eco.internal.spigot.integrations.customitems

import com.nexomc.nexo.api.NexoItems
import com.nexomc.nexo.api.events.NexoItemsLoadedEvent
import com.nexomc.nexo.items.UpdateCallback
import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.integrations.customitems.CustomItemsIntegration
import com.willfp.eco.core.items.CustomItem
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.items.TestableItem
import com.willfp.eco.core.items.provider.ItemProvider
import com.willfp.eco.util.NamespacedKeyUtils
import net.kyori.adventure.key.Key
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack

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

        NexoItems.registerUpdateCallback(
            Key.key("eco:nexo_update"),
            object : UpdateCallback {
                override fun preUpdate(itemStack: ItemStack): ItemStack? {
                    return null
                }

                override fun postUpdate(itemStack: ItemStack): ItemStack {
                    return itemStack
                }
            }
        )
    }

    private class NexoProvider : ItemProvider("nexo") {
        override fun provideForKey(key: String): TestableItem? {
            val item = NexoItems.itemFromId(key) ?: return null
            val id = NexoItems.idFromItem(item)
            val namespacedKey = NamespacedKeyUtils.create("nexo", id.toString())
            return CustomItem(
                namespacedKey,
                { id.equals(NexoItems.idFromItem(it), ignoreCase = true) },
                item.build()
            )
        }
    }
}