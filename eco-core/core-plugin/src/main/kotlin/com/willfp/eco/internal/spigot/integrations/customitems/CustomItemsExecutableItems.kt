package com.willfp.eco.internal.spigot.integrations.customitems

import com.ssomar.score.api.executableitems.ExecutableItemsAPI
import com.willfp.eco.core.integrations.customitems.CustomItemsIntegration
import com.willfp.eco.core.items.CustomItem
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.items.TestableItem
import com.willfp.eco.core.items.provider.ItemProvider
import com.willfp.eco.util.NamespacedKeyUtils
import java.util.Optional
import java.util.function.Predicate
import org.bukkit.inventory.ItemStack

class CustomItemsExecutableItems : CustomItemsIntegration {
    override fun registerProvider() {
        Items.registerItemProvider(ExecutableItemsProvider())
    }

    override fun getPluginName(): String {
        return "ExecutableItems"
    }

    private class ExecutableItemsProvider : ItemProvider("executableitems") {
        /*
        Item lookup strings are lowercased, but ExecutableItems IDs are
        case-sensitive, so resolve the actual ID before looking the item up.
         */
        override fun provideForKey(key: String): TestableItem? {
            val manager = ExecutableItemsAPI.getExecutableItemsManager()
            val id = manager.executableItemIdsList.firstOrNull { it.equals(key, ignoreCase = true) }
            val item = id?.let { manager.getExecutableItem(it).orElse(null) } ?: return null
            return CustomItem(
                NamespacedKeyUtils.create("executableitems", key.lowercase()),
                Predicate { test: ItemStack ->
                    val customStack = ExecutableItemsAPI.getExecutableItemsManager().getExecutableItem(test).orElse(null) ?: return@Predicate false
                    customStack.id.equals(id, ignoreCase = true)
                },
                item.buildItem(1, Optional.empty())
            )
        }

        override fun getKey(itemStack: ItemStack): String? =
            ExecutableItemsAPI.getExecutableItemsManager().getExecutableItem(itemStack).orElse(null)?.id
    }
}
