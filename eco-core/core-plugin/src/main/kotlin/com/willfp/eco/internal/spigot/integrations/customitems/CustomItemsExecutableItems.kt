package com.willfp.eco.internal.spigot.integrations.customitems

import com.ssomar.executableitems.api.ExecutableItemsAPI
import com.willfp.eco.core.integrations.customitems.CustomItemsWrapper
import com.willfp.eco.core.items.CustomItem
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.items.TestableItem
import com.willfp.eco.core.items.provider.ItemProvider
import com.willfp.eco.util.NamespacedKeyUtils
import org.bukkit.inventory.ItemStack
import java.util.function.Predicate

class CustomItemsExecutableItems : CustomItemsWrapper {
    override fun registerProvider() {
        Items.registerItemProvider(ExecutableItemsProvider())
    }

    override fun getPluginName(): String {
        return "ExecutableItems"
    }

    private class ExecutableItemsProvider : ItemProvider("executableitems") {
        override fun provideForKey(key: String): TestableItem? {
            val item = ExecutableItemsAPI.getExecutableItem(key) ?: return null
            val namespacedKey = NamespacedKeyUtils.create("executableitems", key)
            return CustomItem(
                namespacedKey,
                Predicate { test: ItemStack ->
                    val customStack = ExecutableItemsAPI.getExecutableItemConfig(test) ?: return@Predicate false
                    customStack.id.equals(key, ignoreCase = true)
                },
                item
            )
        }
    }
}
