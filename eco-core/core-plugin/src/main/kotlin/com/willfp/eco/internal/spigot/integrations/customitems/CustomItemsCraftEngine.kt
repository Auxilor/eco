package com.willfp.eco.internal.spigot.integrations.customitems

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.integrations.customitems.CustomItemsIntegration
import com.willfp.eco.core.items.CustomItem
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.items.TestableItem
import com.willfp.eco.core.items.provider.ItemProvider
import com.willfp.eco.util.NamespacedKeyUtils
import net.momirealms.craftengine.bukkit.api.CraftEngineItems
import net.momirealms.craftengine.bukkit.api.event.CraftEngineReloadEvent
import net.momirealms.craftengine.core.util.Key
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack
import java.util.function.Predicate

class CustomItemsCraftEngine(
    private val plugin: EcoPlugin
) : CustomItemsIntegration, Listener {
    override fun registerProvider() {
        plugin.eventManager.registerListener(this)
    }

    override fun getPluginName(): String {
        return "CraftEngine"
    }

    @EventHandler
    @Suppress("UNUSED_PARAMETER")
    fun onItemRegister(event: CraftEngineReloadEvent) {
        Items.registerItemProvider(CraftEngineProvider())
    }

    private class CraftEngineProvider : ItemProvider("craftengine") {
        override fun provideForKey(key: String): TestableItem? {
            val split = key.split(":").toMutableList()

            if (split.size < 2) {
                return null
            }

            val namespace = split[0]
            val value = split[1]

            val id = Key.of(namespace, value)
            val item = CraftEngineItems.byId(id) ?: return null
            val namespacedKey = NamespacedKeyUtils.create("craftengine", key.lowercase().replace(":", "__"))
            val stack = item.buildItemStack()

            return CustomItem(
                namespacedKey,
                Predicate { test: ItemStack ->
                    val customStack = CraftEngineItems.byItemStack(test) ?: return@Predicate false
                    customStack.id().equals(id)
                },
                stack
            )
        }

    }
}